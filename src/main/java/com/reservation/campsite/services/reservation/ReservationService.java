package com.reservation.campsite.services.reservation;

import com.reservation.campsite.dto.request.ReservationRequestDTO;
import com.reservation.campsite.dto.request.ReservationUpdateDTO;
import com.reservation.campsite.persistence.entity.Reservation;

import java.time.LocalDate;
import java.util.Map;

public interface ReservationService {

    Map<LocalDate, Integer> findAvailability(LocalDate dateFrom, LocalDate dateTo);

    Reservation create(ReservationRequestDTO reservationRequestDTO);

    Reservation update(Long reservationId, ReservationUpdateDTO reservationUpdateDTO);

    void cancel(Long id);
}
