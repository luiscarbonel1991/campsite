package com.reservation.campsite.services.reservation;

import com.reservation.campsite.persistence.entity.Availability;

import java.time.LocalDate;
import java.util.List;


public interface AvailabilityService {
    List<Availability> findAvailability(LocalDate from, LocalDate to);

    void saveAll(List<Availability> availabilities);
}
