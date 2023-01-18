package com.reservation.campsite.util;

import com.reservation.campsite.persistence.entity.Availability;
import org.springframework.data.util.Pair;

import java.time.LocalDate;
import java.util.*;

public class TestDataUtils {

    private TestDataUtils() {
    }

    public static List<Availability> getAvailabilitiesByDateRange(
            LocalDate arrivalDate,
            LocalDate departureDate,
            int available,
            int availableTotal,
            Map<LocalDate, Pair<Integer, Integer>> customAvailabilities) {

        List<Availability> availabilities = new ArrayList<>();
        Map<LocalDate, Pair<Integer, Integer>> finalCustomAvailabilities = Objects.requireNonNullElseGet(customAvailabilities, HashMap::new);
        arrivalDate.datesUntil(departureDate).forEach(date -> {
            Availability availability = new Availability();
            availability.setDate(date);
            Pair<Integer, Integer> result = finalCustomAvailabilities.getOrDefault(date, Pair.of(available, availableTotal));
            availability.setAvailable(result.getFirst());
            availability.setAvailableTotal(result.getSecond());
            availabilities.add(availability);
        });
        return availabilities;
    }
}
