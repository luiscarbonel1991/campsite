package com.reservation.campsite.services.reservation;

import com.reservation.campsite.configuration.CacheConfig;
import com.reservation.campsite.dto.request.ReservationRequestDTO;
import com.reservation.campsite.dto.request.ReservationUpdateDTO;
import com.reservation.campsite.exception.BadRequestException;
import com.reservation.campsite.exception.NotFoundException;
import com.reservation.campsite.persistence.entity.Availability;
import com.reservation.campsite.persistence.entity.Reservation;
import com.reservation.campsite.persistence.repository.ReservationRepository;
import com.reservation.campsite.services.validation.ValidateService;
import com.reservation.campsite.util.RangeDate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.reservation.campsite.mapper.Mapper.mapper;
import static com.reservation.campsite.util.ParamName.*;

@Service
@Slf4j
public class ReservationServiceImpl implements ReservationService {

    private final AvailabilityService availabilityService;

    private final ValidateService validateService;

    private final ReservationRepository reservationRepository;

    private final CacheManager cacheManager;


    @Value("${campsite.max-advance-days}")
    private int maxAdvanceDays;

    @Value("${campsite.max-stay-days}")
    private int maxStayDays;

    @Value("${campsite.min-stay-days}")
    private int minStayDays;

    @Value("${campsite.min-ahead-arrival-days}")
    private int minAheadArrivalDays;

    @Value("${campsite.max-ahead-arrival-days}")
    private int maxAheadArrivalDays;

    private static final int INCREASE_AVAILABILITY = 1;

    private static final int DECREASE_AVAILABILITY = -1;


    public ReservationServiceImpl(AvailabilityService availabilityService, ValidateService validateService, ReservationRepository reservationRepository, CacheManager cacheManager) {
        this.availabilityService = availabilityService;
        this.validateService = validateService;
        this.reservationRepository = reservationRepository;
        this.cacheManager = cacheManager;
    }


    @Override
    public Map<LocalDate, Boolean> findAvailability(LocalDate arrivalDate, LocalDate departureDate) {
        if (arrivalDate == null && departureDate == null) {
            arrivalDate = LocalDate.now().plusDays(1);
            departureDate = arrivalDate.plusDays(maxAdvanceDays);
        }

        this.validateStayRangeDays(arrivalDate, departureDate, minAheadArrivalDays, maxAheadArrivalDays);
        return availabilityService.findAvailability(arrivalDate, departureDate)
                .stream()
                .collect(
                        Collectors.toMap(
                                Availability::getDate,
                                entry -> entry.getAvailable() > 0
                        )
                );
    }


    @Transactional
    @Override
    public Reservation create(ReservationRequestDTO reservationDTO) {
        validateService.isNotNull(reservationDTO, RESERVATION.getNameParam());
        validateService.isNotEmptyOrNull(reservationDTO.getName(), NAME.getNameParam());
        String emailToCreate = reservationDTO.getEmail();
        validateService.validateEmail(emailToCreate, EMAIL.getNameParam());
        LocalDate arrivalDateToCreate = reservationDTO.getArrivalDate();
        LocalDate departureDateToCreate = reservationDTO.getDepartureDate();
        validateStayRangeDays(arrivalDateToCreate, departureDateToCreate, minStayDays, maxStayDays);
        validateNotAlreadyExistReservation(emailToCreate, arrivalDateToCreate, departureDateToCreate);
        availabilityService.updateAvailability(arrivalDateToCreate, departureDateToCreate, DECREASE_AVAILABILITY);
        return this.save(mapper(reservationDTO).toReservation());
    }


    @Transactional
    @Override
    public Reservation update(Long reservationId, ReservationUpdateDTO reservationUpdateDTO) {
        validateService.isNotNull(reservationUpdateDTO, RESERVATION.getNameParam());
        Reservation reservationFound = findById(reservationId);
        validateIsNotCancelled(reservationFound);
        LocalDate arrivalDateToUpdate = reservationUpdateDTO.arrivalDate();
        LocalDate departureDateToUpdate = reservationUpdateDTO.departureDate();
        String emailToUpdate = reservationUpdateDTO.email();

        if (arrivalDateToUpdate == null) {
            arrivalDateToUpdate = reservationFound.getArrivalDate();
        }
        if (departureDateToUpdate == null) {
            departureDateToUpdate = reservationFound.getDepartureDate();
        }

        if(emailToUpdate == null) {
            emailToUpdate = reservationFound.getEmail();
        }

        validateStayRangeDays(arrivalDateToUpdate, departureDateToUpdate, minStayDays, maxStayDays);


        if(!emailToUpdate.equals(reservationFound.getEmail())) {
            validateService.validateEmail(reservationUpdateDTO.email(), EMAIL.getNameParam());
            validateNotAlreadyExistReservation(reservationUpdateDTO.email(), arrivalDateToUpdate, departureDateToUpdate);
        }

        if(needsUpdatingByStayDate(reservationFound, arrivalDateToUpdate, departureDateToUpdate)) {
            availabilityService.updateAvailability(reservationFound.getArrivalDate(), reservationFound.getDepartureDate(), INCREASE_AVAILABILITY);
            availabilityService.updateAvailability(arrivalDateToUpdate, departureDateToUpdate, DECREASE_AVAILABILITY);
        }


        if(reservationUpdateDTO.name() != null) {
            reservationFound.setName(reservationUpdateDTO.name());
        }

        reservationFound.setArrivalDate(arrivalDateToUpdate);
        reservationFound.setDepartureDate(departureDateToUpdate);
        reservationFound.setUpdateDate(Instant.now());
        return save(reservationFound);
    }

    @Transactional
    @Override
    public void cancel(Long id) {
        Reservation reservationToCancel = findById(id);
        if (reservationToCancel.isNotCancelled()) {
            availabilityService.updateAvailability(reservationToCancel.getArrivalDate(), reservationToCancel.getDepartureDate(), INCREASE_AVAILABILITY);
            reservationToCancel.setCancelDate(Instant.now());
            reservationRepository.save(reservationToCancel);
        } else {
            throw BadRequestException.alreadyCancelled(id);
        }
    }


    public Reservation save(Reservation reservationToSave) {
        return this.reservationRepository.save(reservationToSave);
    }

    private Reservation findById(Long id) {
        Optional<Reservation> reservation = reservationRepository.findById(id);
        if (reservation.isEmpty()) {
            throw NotFoundException.reservationIdNotFound(id);
        }
        return reservation.get();
    }

    private static boolean needsUpdatingByStayDate(Reservation reservationFound, LocalDate arrivalDateToUpdate, LocalDate departureDateToUpdate) {
        return arrivalDateToUpdate != null
                && departureDateToUpdate != null
                && (!arrivalDateToUpdate.equals(reservationFound.getArrivalDate())
                || !departureDateToUpdate.equals(reservationFound.getDepartureDate()));
    }

    private void validateIsNotCancelled(Reservation reservationFound) {
        if (reservationFound.isCancelled()) {
            throw BadRequestException.updateCancelledReservation(reservationFound.getCancelDate());
        }
    }

    private void validateNotAlreadyExistReservation(String emailToCreate, LocalDate arrivalDate, LocalDate departureDate) {
        List<Reservation> reservationsFound =
                this.reservationRepository
                        .findByEmailAndBetweenDateRangeNotCancelled(emailToCreate, arrivalDate, departureDate);

        if (!reservationsFound.isEmpty()) {
            throw BadRequestException.reservationAlreadyExists(mapper(reservationsFound.get(0)).toReservationDTO());
        }
    }


    private void validateStayRangeDays(LocalDate arrivalDate, LocalDate departureDate, int minStayDays, int maxStayDays) {
        validateService.validateStayRangeDays(arrivalDate, departureDate, minStayDays, maxStayDays);
        RangeDate<LocalDate> validArrivalDateRange = getValidRangeToReserve();
        validateService.validateArrivalDate(arrivalDate, departureDate, validArrivalDateRange);
    }

    private RangeDate<LocalDate> getValidRangeToReserve() {
        LocalDate validStartArrivalDate = LocalDate.now().plusDays(minAheadArrivalDays);
        LocalDate validEndArrivalDate = LocalDate.now().plusDays(maxAheadArrivalDays);
        return mapper(validStartArrivalDate, validEndArrivalDate).toDateRange();
    }

    @CacheEvict(value = CacheConfig.AVAILABILITY_RANGE_DATES_CACHE, key = "#arrivalDate.toString() + #departureDate.toString()")
    public void clearAvailabilityRangeDatesCache(LocalDate arrivalDate, LocalDate departureDate) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Clearing cache for availability range dates: {} - {}", arrivalDate, departureDate);
            }
            Objects.requireNonNull(cacheManager.getCache(CacheConfig.AVAILABILITY_RANGE_DATES_CACHE))
                    .evictIfPresent(arrivalDate.toString() + departureDate.toString());
        } catch (Exception e) {
            log.error("Error clearing cache for availability range dates: {} - {}", arrivalDate, departureDate);
        }
    }
}
