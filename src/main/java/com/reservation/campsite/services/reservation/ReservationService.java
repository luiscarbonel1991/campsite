package com.reservation.campsite.services.reservation;

import java.time.LocalDate;
import java.util.Map;

public interface ReservationService {

    Map<LocalDate, Integer> findAvailability(LocalDate dateFrom, LocalDate dateTo);
}
