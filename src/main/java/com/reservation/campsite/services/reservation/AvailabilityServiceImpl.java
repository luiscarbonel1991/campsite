package com.reservation.campsite.services.reservation;

import com.reservation.campsite.exception.NotFoundException;
import com.reservation.campsite.persistence.entity.Availability;
import com.reservation.campsite.persistence.repository.AvailabilityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class AvailabilityServiceImpl implements AvailabilityService {

    private final AvailabilityRepository availabilityRepository;

    public AvailabilityServiceImpl(AvailabilityRepository availabilityRepository) {
        this.availabilityRepository = availabilityRepository;
    }

    @Override
    public List<Availability> findAvailability(LocalDate dateFrom, LocalDate dateTo) {
        return this.availabilityRepository.findAvailabilitiesByDateBetweenOrderByDate(dateFrom, dateTo);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public void updateAvailability(LocalDate arrivalDate, LocalDate departureDate, int plus) {

            List<Availability> availabilities = findAvailability(arrivalDate, departureDate);

            availabilities.forEach(availability -> {
                if(availability.getAvailable() == 0 && Integer.signum(plus) == -1) {
                    throw NotFoundException.availabilityDateRange(availability.getDate());
                }
                availability.setAvailable(availability.getAvailable() + plus);
            });

            availabilityRepository.saveAll(availabilities);

    }
}
