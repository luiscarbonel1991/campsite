package com.reservation.campsite.services;

import java.time.LocalDate;
import java.util.Map;


public interface AvailabilityService {
    Map<LocalDate, Integer> findAvailability(LocalDate from, LocalDate to);
}
