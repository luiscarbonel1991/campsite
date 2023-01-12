package com.reservation.campsite.services.reservation;

import com.reservation.campsite.dto.request.ReservationRequestDTO;
import com.reservation.campsite.services.validation.ValidateService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.reservation.campsite.persistence.entity.Availability;
import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReservationServiceImpl implements ReservationService {

    @Value("${campsite.max-advance-days}")
    private int maxAdvanceDays = 30;

    private final AvailabilityService availabilityService;

    private final ValidateService validateService;

    public ReservationServiceImpl(AvailabilityService availabilityService, ValidateService validateService) {
        this.availabilityService = availabilityService;
        this.validateService = validateService;
    }

    @Override
    public Map<LocalDate, Integer> findAvailability(LocalDate dateFrom, LocalDate dateTo) {
        if(dateFrom == null) {
            dateFrom = LocalDate.now();
        }
        if(dateTo == null) {
            dateTo = dateFrom.plusDays(maxAdvanceDays);
        }
        validateService.validateDateRange(dateFrom, "arrivalDate", dateTo, "departureDate");
        return availabilityService.findAvailability(dateFrom, dateTo)
                .stream().collect(Collectors.toMap(Availability::getDate, Availability::getAvailable));
    }
    @Override
    public void create(ReservationRequestDTO reservationDTO) {
        validateService.isNotNull(reservationDTO, "reservation");
        validateService.isNotEmptyOrNull(reservationDTO.getName(), "name");
        validateService.isNotEmptyOrNull(reservationDTO.getEmail(), "email");
        validateService.isNotNull(reservationDTO.getArrivalDate(), "arrivalDate");
        validateService.isNotNull(reservationDTO.getDepartureDate(), "departureDate");
        validateService.validateDateRange(reservationDTO.getArrivalDate(),"arrivalDate", reservationDTO.getDepartureDate(), "departureDate");

    }
}
