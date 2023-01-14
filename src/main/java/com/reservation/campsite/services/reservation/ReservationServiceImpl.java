package com.reservation.campsite.services.reservation;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.reservation.campsite.mapper.Mapper.mapper;
import static com.reservation.campsite.util.ParamName.*;

@Service
@Slf4j
public class ReservationServiceImpl implements ReservationService {

    private final AvailabilityService availabilityService;

    private final ValidateService validateService;

    private final ReservationRepository reservationRepository;


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


    public ReservationServiceImpl(AvailabilityService availabilityService, ValidateService validateService, ReservationRepository reservationRepository) {
        this.availabilityService = availabilityService;
        this.validateService = validateService;
        this.reservationRepository = reservationRepository;
    }

    @Override
    public Map<LocalDate, Integer> findAvailability(LocalDate dateFrom, LocalDate dateTo) {
        if (dateFrom == null) {
            dateFrom = LocalDate.now();
        }
        if (dateTo == null) {
            dateTo = dateFrom.plusDays(maxAdvanceDays);
        }
        validateService.validateDateRange(dateFrom, ARRIVAL_DATE.getNameParam(), dateTo, DEPARTURE.getNameParam());
        return availabilityService.findAvailability(dateFrom, dateTo)
                .stream()
                .filter(availability -> availability.hasTotalAvailability() && availability.hasAvailability())
                .collect(Collectors.toMap(Availability::getDate, Availability::getAvailable));
    }

    @Transactional
    @Override
    public Map<String, Long> create(ReservationRequestDTO reservationDTO) {
        validateService.isNotNull(reservationDTO, RESERVATION.getNameParam());
        validateService.isNotEmptyOrNull(reservationDTO.getName(), NAME.getNameParam());
        String emailToCreate = reservationDTO.getEmail();
        validateService.validateEmail(emailToCreate, EMAIL.getNameParam());
        LocalDate arrivalDateToCreate = reservationDTO.getArrivalDate();
        LocalDate departureDateToCreate = reservationDTO.getDepartureDate();
        this.validateStayRangeDays(arrivalDateToCreate, departureDateToCreate);
        validateReservationAlreadyExist(emailToCreate, arrivalDateToCreate, departureDateToCreate);
        availabilityService.updateAvailability(reservationDTO.getArrivalDate(), reservationDTO.getDepartureDate(), -1);
        return Map.of(RESERVATION_ID.getNameParam(), this.save(mapper(reservationDTO).toReservation()).getId());
    }


    @Override
    public void update(Long reservationId, ReservationUpdateDTO reservationUpdateDTO) {
        validateService.isNotNull(reservationUpdateDTO, RESERVATION.getNameParam());
        Reservation reservationFound = findById(reservationId);
        LocalDate arrivalDateToUpdate = reservationUpdateDTO.arrivalDate();
        LocalDate departureDateToUpdate = reservationUpdateDTO.departureDate();
        validateStayRangeDays(arrivalDateToUpdate, departureDateToUpdate);
        validateReservationAlreadyExist(reservationFound.getEmail(), arrivalDateToUpdate, departureDateToUpdate);

        availabilityService.updateAvailability(reservationFound.getArrivalDate(), reservationFound.getDepartureDate(), INCREASE_AVAILABILITY);
        availabilityService.updateAvailability(arrivalDateToUpdate, departureDateToUpdate, DECREASE_AVAILABILITY);

        reservationFound.setName(reservationUpdateDTO.name());
        reservationFound.setEmail(reservationUpdateDTO.email());
        reservationFound.setArrivalDate(arrivalDateToUpdate);
        reservationFound.setDepartureDate(departureDateToUpdate);
        reservationFound.setUpdateDate(Instant.now());
    }

    @Override
    public void cancel(Long id) {
        Reservation reservationToSave = findById(id);
        availabilityService.updateAvailability(reservationToSave.getArrivalDate(), reservationToSave.getDepartureDate(), 1);
        reservationToSave.setCancelDate(Instant.now());
        reservationRepository.save(reservationToSave);
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

    private void validateReservationAlreadyExist(String emailToCreate, LocalDate arrivalDateToCreate, LocalDate departureDateToCreate) {
        Reservation reservationFound =
                this.reservationRepository
                        .findByEmailAndBetweenDateRangeNotCancelled(emailToCreate, arrivalDateToCreate, departureDateToCreate);

        if (reservationFound != null) {
            throw BadRequestException.reservationAlreadyExists(EMAIL.getNameParam(), emailToCreate);
        }
    }


    private void validateStayRangeDays(LocalDate arrivalDate, LocalDate departureDate) {
        validateService.validateStayRangeDays(arrivalDate, departureDate, minStayDays, maxStayDays);
        RangeDate<LocalDate> validArrivalDateRange = getValidRangeToReserve();
        validateService.validateArrivalDate(arrivalDate, departureDate, validArrivalDateRange);
    }

    private RangeDate<LocalDate> getValidRangeToReserve() {
        LocalDate validStartArrivalDate = LocalDate.now().plusDays(minAheadArrivalDays);
        LocalDate validEndArrivalDate = LocalDate.now().plusDays(maxAheadArrivalDays);
        return mapper(validStartArrivalDate, validEndArrivalDate).toDateRange();
    }
}
