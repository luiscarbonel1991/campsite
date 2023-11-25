package com.reservation.campsite.service.reservation;

import com.reservation.campsite.exception.NotFoundException;
import com.reservation.campsite.persistence.entity.Availability;
import com.reservation.campsite.persistence.repository.AvailabilityRepository;
import com.reservation.campsite.services.reservation.AvailabilityService;
import com.reservation.campsite.services.reservation.AvailabilityServiceImpl;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.util.Pair;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static com.reservation.campsite.exception.ErrorCode.NOT_FOUND_AVAILABILITY_TO_DATE;
import static com.reservation.campsite.util.TestDataUtils.getAvailabilitiesByDateRange;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class AvailabilityServiceUnitTest {

    @MockBean
    private AvailabilityRepository availabilityRepository;

    @Test
    void givenArrivalAndDepartureDateWhenFindAvailabilityThenExpectedAvailable() {
        // GIVEN
        int expectedSize = 3;
        int expectedAvailable = 10;
        int expectedAvailableTotal = 10;
        LocalDate arrivalDate = LocalDate.now().plusDays(1);
        LocalDate departureDate = LocalDate.now().plusDays(expectedSize);

        List<Availability> availabilitiesData = getAvailabilitiesByDateRange(arrivalDate, departureDate.plusDays(1), expectedAvailable, expectedAvailableTotal, null);
        AvailabilityService service = getAvailabilityService();

        // WHEN
        when(availabilityRepository.findAvailabilitiesByDateBetweenOrderByDate(arrivalDate, departureDate)).thenReturn(availabilitiesData);

        // THEN
        List<Availability> availabilities = service.findAvailability(arrivalDate, departureDate);
        Assertions.assertThat(availabilities)
                .isNotNull()
                .isNotEmpty()
                .hasSize(expectedSize)
                .allMatch(availability -> availability.getAvailable() == expectedAvailable)
                .allMatch(availability -> availability.getAvailableTotal() == expectedAvailableTotal);
    }

    @Test
    void givenArrivalAndDepartureDateWhenFindAvailabilityThenExpectedNonAvailable() {
        // GIVEN
        int expectedSize = 3;
        int expectedAvailable = 10;
        int expectedAvailableTotal = 10;
        LocalDate arrivalDate = LocalDate.now().plusDays(1);
        LocalDate departureDate = LocalDate.now().plusDays(expectedSize);
        Map<LocalDate, Pair<Integer, Integer>> expectedDatesWithNonAvailability =
                Map.of(LocalDate.parse(arrivalDate.toString()), Pair.of(0, 10), LocalDate.parse(departureDate.toString()), Pair.of(0, 10));
        List<Availability> availabilitiesData =
                getAvailabilitiesByDateRange(arrivalDate, departureDate.plusDays(1), expectedAvailable, expectedAvailableTotal, expectedDatesWithNonAvailability);
        AvailabilityService service = getAvailabilityService();

        // WHEN
        when(availabilityRepository.findAvailabilitiesByDateBetweenOrderByDate(arrivalDate, departureDate)).thenReturn(availabilitiesData);

        // THEN
        List<Availability> availabilities = service.findAvailability(arrivalDate, departureDate);
        Predicate<Availability> hasNotAvailable = availability -> Boolean.FALSE.equals(availability.hasAvailability());
        Assertions.assertThat(availabilities)
                .isNotNull()
                .isNotEmpty()
                .hasSize(expectedSize)
                .areExactly(expectedDatesWithNonAvailability.size(), new Condition<>(hasNotAvailable, "hasNotAvailable"));
    }


    @Test
    void givenArrivalDateAndDepartureDateWhenUpdateAvailabilityThenDecreaseAvailabilityByDate() {
        // GIVEN
        int expectedSize = 3;
        int expectedAvailable = 10;
        int expectedAvailableTotal = 10;
        int decreaseIn = -1;
        LocalDate arrivalDate = LocalDate.now().plusDays(1);
        LocalDate departureDate = LocalDate.now().plusDays(expectedSize);
        List<Availability> availabilitiesData =
                getAvailabilitiesByDateRange(arrivalDate, departureDate.plusDays(1), expectedAvailable, expectedAvailableTotal, null);
        AvailabilityService service = getAvailabilityService();

        // WHEN
        when(availabilityRepository.findAvailabilitiesByDateBetweenOrderByDate(any(), any())).thenReturn(availabilitiesData);
        when(availabilityRepository.saveAll(any())).thenReturn(availabilitiesData);

        // THEN
        service.updateAvailability(arrivalDate, departureDate, decreaseIn);
        Assertions.assertThat(availabilitiesData)
                .isNotNull()
                .isNotEmpty()
                .hasSize(expectedSize)
                .areExactly(availabilitiesData.size(), new Condition<>(availability -> availability.getAvailable() == expectedAvailable + decreaseIn, String.format("Decrease available should be %d", expectedAvailable + decreaseIn)))
                .areExactly(availabilitiesData.size(), new Condition<>(availability -> availability.getAvailableTotal() == expectedAvailableTotal, String.format("Available total should be %d", expectedAvailableTotal)));

    }

    @Test
    void givenArrivalDateAndDepartureDateWhenUpdateAvailabilityThenIncreaseAvailabilityByDate() {
        // GIVEN
        int expectedSize = 3;
        int expectedAvailable = 0;
        int expectedAvailableTotal = 10;
        int increaseIn = 5;
        LocalDate arrivalDate = LocalDate.now().plusDays(1);
        LocalDate departureDate = LocalDate.now().plusDays(expectedSize);
        List<Availability> availabilitiesData =
                getAvailabilitiesByDateRange(arrivalDate, departureDate.plusDays(1), expectedAvailable, expectedAvailableTotal, null);
        AvailabilityService service = getAvailabilityService();

        // WHEN
        when(availabilityRepository.findAvailabilitiesByDateBetweenOrderByDate(any(), any())).thenReturn(availabilitiesData);
        when(availabilityRepository.saveAll(any())).thenReturn(availabilitiesData);

        // THEN
        service.updateAvailability(arrivalDate, departureDate, increaseIn);
        Assertions.assertThat(availabilitiesData)
                .isNotNull()
                .isNotEmpty()
                .hasSize(expectedSize)
                .areExactly(availabilitiesData.size(), new Condition<>(availability -> availability.getAvailable() == expectedAvailable + increaseIn, String.format("Decrease available should be %d", expectedAvailable + increaseIn)))
                .areExactly(availabilitiesData.size(), new Condition<>(availability -> availability.getAvailableTotal() == expectedAvailableTotal, String.format("Available total should be %d", expectedAvailableTotal)));

    }

    @Test
    void givenArrivalDateAndDepartureDateWhenUpdateAvailabilityThenThrowExceptionByDecreaseLowerThanZero() {
        // GIVEN

        int available = 0;
        int availableTotal = 10;
        int decreaseIn = -1;
        LocalDate arrivalDate = LocalDate.now().plusDays(1);
        LocalDate departureDate = LocalDate.now().plusDays(3);
        List<Availability> availabilitiesData =
                getAvailabilitiesByDateRange(arrivalDate, departureDate.plusDays(1), available, availableTotal, null);
        AvailabilityService service = getAvailabilityService();

        // WHEN
        when(availabilityRepository.findAvailabilitiesByDateBetweenOrderByDate(any(), any())).thenReturn(availabilitiesData);
        when(availabilityRepository.saveAll(any())).thenReturn(availabilitiesData);

        // THEN

        Assertions.assertThatThrownBy(() -> service.updateAvailability(arrivalDate, departureDate, decreaseIn))
                .isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("errorCode", NOT_FOUND_AVAILABILITY_TO_DATE);

    }


    private AvailabilityService getAvailabilityService() {
        return new AvailabilityServiceImpl(availabilityRepository, cacheManager);
    }
}
