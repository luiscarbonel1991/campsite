package com.reservation.campsite.services.reservation;

import com.reservation.campsite.configuration.CacheConfig;
import com.reservation.campsite.dto.request.ReservationRequestDTO;
import com.reservation.campsite.dto.request.ReservationUpdateDTO;
import com.reservation.campsite.exception.BadRequestException;
import com.reservation.campsite.exception.NotFoundException;
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
    public Map<LocalDate, Integer> findAvailability(LocalDate arrivalDate, LocalDate departureDate) {
        if (arrivalDate == null && departureDate == null) {
            arrivalDate = LocalDate.now();
            departureDate = arrivalDate.plusDays(maxAdvanceDays);
        }

        this.validateStayRangeDays(arrivalDate, departureDate, minAheadArrivalDays, maxAheadArrivalDays);
        Map<LocalDate, Integer> map = new LinkedHashMap<>();

        Objects.requireNonNull(arrivalDate).datesUntil(departureDate.plus(1, ChronoUnit.DAYS))
                .forEach(date -> map.put(date, 0));
        availabilityService.findAvailability(arrivalDate, departureDate)
                .forEach(availability -> map.put(availability.getDate(), availability.getAvailable()));
        return map;
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
        this.validateStayRangeDays(arrivalDateToCreate, departureDateToCreate, minStayDays, maxStayDays);
        validateNotAlreadyExistReservation(emailToCreate, arrivalDateToCreate, departureDateToCreate);
        clearAvailabilityRangeDatesCache(arrivalDateToCreate, departureDateToCreate);
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

        if (Boolean.TRUE.equals(needsUpdatingByStayDate(reservationFound, arrivalDateToUpdate, departureDateToUpdate))) {
            validateStayRangeDays(arrivalDateToUpdate, departureDateToUpdate, minStayDays, maxStayDays);
            validateNotAlreadyExistReservation(reservationFound.getEmail(), arrivalDateToUpdate, departureDateToUpdate);
            clearAvailabilityRangeDatesCache(reservationFound.getArrivalDate(), reservationFound.getDepartureDate());
            clearAvailabilityRangeDatesCache(arrivalDateToUpdate, departureDateToUpdate);
            availabilityService.updateAvailability(reservationFound.getArrivalDate(), reservationFound.getDepartureDate(), INCREASE_AVAILABILITY);
            availabilityService.updateAvailability(arrivalDateToUpdate, departureDateToUpdate, DECREASE_AVAILABILITY);
        } else {
            validateService.isNotNull(arrivalDateToUpdate, ARRIVAL_DATE.getNameParam());
            validateService.isNotNull(departureDateToUpdate, DEPARTURE.getNameParam());
        }

        String reservationName = reservationUpdateDTO.name();
        if (reservationName != null) {
            reservationFound.setName(reservationName);
        }
        String reservationEmail = reservationUpdateDTO.email();
        if (reservationEmail != null) {
            validateService.validateEmail(reservationEmail, EMAIL.getNameParam());
            reservationFound.setEmail(reservationEmail);
        }
        reservationFound.setArrivalDate(arrivalDateToUpdate);
        reservationFound.setDepartureDate(departureDateToUpdate);
        reservationFound.setUpdateDate(Instant.now());
        return this.save(reservationFound);
    }

    @Transactional
    @Override
    public void cancel(Long id) {
        Reservation reservationToCancel = findById(id);
        if (reservationToCancel.isNotCancelled()) {
            clearAvailabilityRangeDatesCache(reservationToCancel.getArrivalDate(), reservationToCancel.getDepartureDate());
            availabilityService.updateAvailability(reservationToCancel.getArrivalDate(), reservationToCancel.getDepartureDate(), INCREASE_AVAILABILITY);
            reservationToCancel.setCancelDate(Instant.now());
            reservationRepository.save(reservationToCancel);
        } else {
            throw BadRequestException.alreadyCancelled();
        }
    }


    @Transactional
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
        List<Reservation> reservationFound =
                this.reservationRepository
                        .findByEmailAndBetweenDateRangeNotCancelled(emailToCreate, arrivalDate, departureDate);

        if (!reservationFound.isEmpty()) {
            throw BadRequestException.reservationAlreadyExists(EMAIL.getNameParam(), emailToCreate);
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
