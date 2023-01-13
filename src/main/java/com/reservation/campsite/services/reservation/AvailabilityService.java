package com.reservation.campsite.services.reservation;

import com.reservation.campsite.persistence.entity.Availability;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


public interface AvailabilityService {
    List<Availability> findAvailability(LocalDate from, LocalDate to);

    @Transactional(propagation = Propagation.SUPPORTS)
    void updateAvailability(LocalDate arrivalDate, LocalDate departureDate, int plus);
}
