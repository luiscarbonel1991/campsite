package com.reservation.campsite.persistence.repository;

import com.reservation.campsite.persistence.entity.Availability;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AvailabilityRepository extends CrudRepository<Availability, Integer> {
    List<Availability> findAvailabilitiesByDateBetweenOrderByDate(LocalDate from, LocalDate to);
}
