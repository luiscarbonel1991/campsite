package com.reservation.campsite.services;

import com.reservation.campsite.persistence.entity.Availability;
import com.reservation.campsite.persistence.repository.AvailabilityRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AvailabilityServiceImpl implements AvailabilityService{

    private final AvailabilityRepository availabilityRepository;

    public AvailabilityServiceImpl(AvailabilityRepository availabilityRepository) {
        this.availabilityRepository = availabilityRepository;
    }

    @Override
    public Map<LocalDate, Integer> findAvailability(LocalDate from, LocalDate to) {
        return this.availabilityRepository.findAvailabilitiesByDateBetween(from, to)
                .stream().filter(availability -> availability.getAvailable() > 0)
                .collect(Collectors.toMap(Availability::getDate, Availability::getAvailable));
    }
}
