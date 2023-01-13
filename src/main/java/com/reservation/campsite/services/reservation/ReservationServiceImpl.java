package com.reservation.campsite.services.reservation;

import com.reservation.campsite.dto.request.ReservationRequestDTO;
import com.reservation.campsite.exception.BadRequestException;
import com.reservation.campsite.exception.NotFoundException;
import com.reservation.campsite.persistence.entity.Reservation;
import com.reservation.campsite.persistence.repository.ReservationRepository;
import com.reservation.campsite.services.validation.ValidateService;
import com.reservation.campsite.util.ParamName;
import com.reservation.campsite.util.RangeDate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.reservation.campsite.persistence.entity.Availability;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.reservation.campsite.mapper.Mapper.mapper;

@Service
@Slf4j
public class ReservationServiceImpl implements ReservationService {

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

    private final AvailabilityService availabilityService;

    private final ValidateService validateService;

    private final ReservationRepository reservationRepository;


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
        validateService.validateDateRange(dateFrom, "arrivalDate", dateTo, "departureDate");
        return availabilityService.findAvailability(dateFrom, dateTo)
                .stream()
                .filter(availability -> availability.hasTotalAvailability() && availability.hasAvailability())
                .collect(Collectors.toMap(Availability::getDate, Availability::getAvailable));
    }

    @Transactional
    @Override
    public Map<String, Long> create(ReservationRequestDTO reservationDTO) {
        validateService.isNotNull(reservationDTO, "reservation");
        validateService.isNotEmptyOrNull(reservationDTO.getName(), "name");
        String emailToCreate = reservationDTO.getEmail();
        validateService.isNotEmptyOrNull(reservationDTO.getEmail(), "email");
        validateService.validateEmail(emailToCreate, "email");
        this.validateStayRangeDays(reservationDTO.getArrivalDate(), reservationDTO.getDepartureDate());
        Reservation reservationFound = this.reservationRepository.findByEmail(emailToCreate);
        if (reservationFound != null && reservationFound.isNotCancelled()) {
            throw BadRequestException.reservationAlreadyExists(ParamName.EMAIL.getNameParam(), emailToCreate);
        }
        availabilityService.updateAvailability(reservationDTO.getArrivalDate(), reservationDTO.getDepartureDate(), -1);
        return Map.of("reservationId", this.save(mapper(reservationDTO).toReservation()).getId());
    }

    @Override
    public void cancel(Long id) {
        Optional<Reservation> reservation = reservationRepository.findById(id);
        if (reservation.isPresent()) {
            availabilityService.updateAvailability(reservation.get().getArrivalDate(), reservation.get().getDepartureDate(), 1);
            Reservation reservationToSave = reservation.get();
            reservationToSave.setCancelDate(Instant.now());
            reservationRepository.save(reservationToSave);
        } else {
            throw NotFoundException.reservationIdNotFound(id);
        }
    }
    
    @Transactional
    public Reservation save(Reservation reservationToSave) {
        return this.reservationRepository.save(reservationToSave);
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
