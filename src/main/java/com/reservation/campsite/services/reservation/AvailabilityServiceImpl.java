package com.reservation.campsite.services.reservation;

import com.reservation.campsite.persistence.entity.Availability;
import com.reservation.campsite.persistence.repository.AvailabilityRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AvailabilityServiceImpl implements AvailabilityService{

    private final AvailabilityRepository availabilityRepository;

    public AvailabilityServiceImpl(AvailabilityRepository availabilityRepository) {
        this.availabilityRepository = availabilityRepository;
    }

    @Override
    public List<Availability> findAvailability(LocalDate dateFrom, LocalDate dateTo) {
        return this.availabilityRepository.findAvailabilitiesByDateBetween(dateFrom, dateTo)
                .stream().filter(availability ->
                        availability.hasTotalAvailability() &&
                        availability.hasAvailability()).toList();

    }


}
