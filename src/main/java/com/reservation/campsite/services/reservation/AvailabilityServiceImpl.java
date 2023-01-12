package com.reservation.campsite.services.reservation;

import com.reservation.campsite.persistence.entity.Availability;
import com.reservation.campsite.persistence.repository.AvailabilityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
        return this.availabilityRepository.findAvailabilitiesByDateBetween(dateFrom, dateTo);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public void saveAll(List<Availability> availabilities) {
        this.availabilityRepository.saveAll(availabilities);
    }
}
