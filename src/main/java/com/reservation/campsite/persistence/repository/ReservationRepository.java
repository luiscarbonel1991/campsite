package com.reservation.campsite.persistence.repository;

import com.reservation.campsite.persistence.entity.Reservation;
import org.springframework.data.repository.CrudRepository;

public interface ReservationRepository extends CrudRepository<Reservation, Long> {
    Boolean existsByEmail(String email);
}
