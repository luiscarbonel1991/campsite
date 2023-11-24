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


    //@Cacheable(cacheNames = {CacheConfig.AVAILABILITY_RANGE_DATES_CACHE}, key = "#arrivalDate.toString() + #departureDate.toString()")
    @Override
    public List<Availability> findAvailability(LocalDate arrivalDate, LocalDate departureDate) {
        return this.availabilityRepository.findAvailabilitiesByDateBetweenOrderByDate(arrivalDate, departureDate);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public void updateAvailability(LocalDate arrivalDate, LocalDate departureDate, int plus) {

        List<Availability> availabilities = findAvailability(arrivalDate, departureDate);

        availabilities.forEach(availability -> {
            if (availability.getAvailable() == 0 && Integer.signum(plus) == -1) {
                throw NotFoundException.availabilityDate(availability.getDate());
            }
            availability.setAvailable(availability.getAvailable() + plus);
        });

        availabilityRepository.saveAll(availabilities);

    }
}
