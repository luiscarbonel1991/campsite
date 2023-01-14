package com.reservation.campsite.mapper;

import com.reservation.campsite.dto.response.ReservationDTO;
import com.reservation.campsite.persistence.entity.Reservation;

public interface ToReservationDTO {

    ReservationDTO toReservationDTO();
}
