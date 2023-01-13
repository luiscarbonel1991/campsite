package com.reservation.campsite.persistence.repository;

import com.reservation.campsite.persistence.entity.Reservation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;

public interface ReservationRepository extends CrudRepository<Reservation, Long> {
    @Query(value = "SELECT r FROM Reservation r WHERE r.email = ?1 AND r.arrivalDate <= ?2 AND r.departureDate >= ?2 AND r.cancelDate IS NULL")
    Reservation findByEmailAndBetweenDateRangeNotCancelled(String email, LocalDate arrivalDate, LocalDate departureDate);

}
