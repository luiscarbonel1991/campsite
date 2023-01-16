package com.reservation.campsite.service.reservation;

import com.reservation.campsite.persistence.entity.Availability;
import com.reservation.campsite.persistence.repository.ReservationRepository;
import com.reservation.campsite.services.reservation.AvailabilityService;
import com.reservation.campsite.services.reservation.ReservationService;
import com.reservation.campsite.services.reservation.ReservationServiceImpl;
import com.reservation.campsite.services.validation.ValidateService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.data.util.Pair;

import java.time.LocalDate;
import java.util.*;

@SpringBootTest
class ReservationServiceUnitTest {

    @MockBean
    private AvailabilityService availabilityService;

    @MockBean
    private ValidateService validateService;

    @MockBean
    private ReservationRepository reservationRepository;

    @MockBean
    private CacheManager cacheManager;

    @Test
    void givenArrivalAndDepartureDatesWhenFindAvailabilityThenAvailabilitiesExpectedByDates() {
        int expectedSize = 31;
        int expectedAvailable = 10;
        LocalDate arrivalDate = LocalDate.now().plusDays(1);
        LocalDate departureDate = LocalDate.now().plusDays(expectedSize);
        Map<LocalDate, Pair<Integer, Integer>> customAvailabilitiesToSet =
                Map.of(LocalDate.parse(arrivalDate.toString()), Pair.of(0, 10), LocalDate.parse(departureDate.toString()), Pair.of(0, 10));
        List<Availability> availabilitiesData =
                getAvailabilitiesByDateRange(arrivalDate, departureDate, expectedAvailable, expectedAvailable, customAvailabilitiesToSet);
        Integer[] expectedAvailabilities = availabilitiesData.stream().map(Availability::getAvailable).toList().toArray(new Integer[0]);

        // GIVEN
        ReservationService service = getReservationService();

        // WHEN
        Mockito.when(availabilityService.findAvailability(arrivalDate, departureDate)).thenReturn(availabilitiesData);

        // THEN
        Map<LocalDate, Integer> availabilities = service.findAvailability(arrivalDate, departureDate);
        Assertions.assertThat(availabilities)
                .isNotNull()
                .isNotEmpty()
                .hasSize(expectedSize)
                .containsValues(expectedAvailabilities);
    }

    private ReservationService getReservationService() {
        return new ReservationServiceImpl(availabilityService, validateService, reservationRepository, cacheManager);
    }

    private List<Availability> getAvailabilitiesByDateRange(
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
