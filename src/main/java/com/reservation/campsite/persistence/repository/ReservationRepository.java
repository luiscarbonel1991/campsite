package com.reservation.campsite.persistence.repository;

import com.reservation.campsite.persistence.entity.Reservation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends CrudRepository<Reservation, Long> {
    @Query(value = "SELECT r FROM Reservation r WHERE r.email = ?1 AND r.arrivalDate <= ?2 AND r.departureDate >= ?2 AND r.cancelDate IS NULL")
    List<Reservation> findByEmailAndBetweenDateRangeNotCancelled(String email, LocalDate arrivalDate, LocalDate departureDate);

    @Query(value = "SELECT r FROM Reservation r WHERE r.arrivalDate >= ?1 AND r.departureDate <= ?2")
    List<Reservation> findReservationsByBetweenArrivalDateAndDepartureDate(LocalDate arrivalDate, LocalDate departureDate);

}
