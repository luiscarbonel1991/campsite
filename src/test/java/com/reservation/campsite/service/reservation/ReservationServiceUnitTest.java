package com.reservation.campsite.service.reservation;

import com.reservation.campsite.dto.request.ReservationRequestDTO;
import com.reservation.campsite.dto.request.ReservationUpdateDTO;
import com.reservation.campsite.exception.NotFoundException;
import com.reservation.campsite.persistence.entity.Availability;
import com.reservation.campsite.persistence.entity.Reservation;
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

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

import static com.reservation.campsite.mapper.Mapper.mapper;


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
                getAvailabilitiesByDateRange(arrivalDate, departureDate.plusDays(1), expectedAvailable, expectedAvailable, customAvailabilitiesToSet);
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

    @Test
    void givenReservationDTOWhenCreateReservationThenSaveReservation() {
        // GIVEN
        ReservationService service = getReservationService();
        String email = "johndoe@email.com";
        String name = "John Doe";
        LocalDate arrivalDate = LocalDate.now().plusDays(1);
        LocalDate departureDate = LocalDate.now().plusDays(2);

        ReservationRequestDTO reservationDTO = getReservationDTO(email, name, arrivalDate, departureDate);
        Reservation reservationToSave = getReservation(reservationDTO);

        // WHEN
        Mockito.when(reservationRepository.findByEmailAndBetweenDateRangeNotCancelled(Mockito.anyString(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of());
        Mockito.when(cacheManager.getCache(Mockito.anyString())).thenReturn(null);
        Mockito.when(reservationRepository.save(Mockito.any())).thenReturn(reservationToSave);

        // THEN
        Reservation reservation = service.create(reservationDTO);
        Assertions.assertThat(reservation)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", reservationToSave.getId())
                .hasFieldOrPropertyWithValue("arrivalDate", reservationToSave.getArrivalDate())
                .hasFieldOrPropertyWithValue("departureDate", reservationToSave.getDepartureDate())
                .hasFieldOrPropertyWithValue("email", reservationToSave.getEmail())
                .hasFieldOrPropertyWithValue("name", reservationToSave.getName());

    }

    @Test
    void givenReservationIdWhenCancelReservationThenCancelReservation() {
        // GIVEN
        Long reservationId = 1L;
        ReservationService service = getReservationService();
        Reservation reservationToCancel = getDefaultReservation();
        reservationToCancel.setId(reservationId);

        // WHEN
        Mockito.when(reservationRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(reservationToCancel));

        // THEN
        Assertions.assertThatCode(() -> service.cancel(reservationId))
                .doesNotThrowAnyException();

    }

    @Test
    void givenReservationIdWhenCancelReservationThenThrowException() {
        // GIVEN
        Long reservationId = 1L;
        ReservationService service = getReservationService();
        Reservation reservationToCancel = getDefaultReservation();
        reservationToCancel.setId(reservationId);

        // WHEN
        Mockito.when(reservationRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        // THEN
        Assertions.assertThatThrownBy(() -> service.cancel(reservationId))
                .isInstanceOf(NotFoundException.class);

    }

    @Test
    void givenReservationIdAndReservationDTOWhenUpdateReservationThenUpdateReservation() {
        // GIVEN
        Long reservationId = 1L;
        ReservationUpdateDTO reservationUpdateDTO = getReservationUpdateDTO();
        ReservationService service = getReservationService();

        // WHEN
        Mockito.when(reservationRepository.findById(Mockito.any())).thenReturn(Optional.of(getDefaultReservation()));
        Mockito.when(reservationRepository.findByEmailAndBetweenDateRangeNotCancelled(Mockito.anyString(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of());

        // THEN
        Assertions.assertThatCode(() -> service.update(reservationId, reservationUpdateDTO))
                .doesNotThrowAnyException();
    }

    @Test
    void givenReservationIdNotFoundAndReservationDTOWhenUpdateReservationThenThrowException() {
        // GIVEN
        Long reservationId = 1L;
        ReservationUpdateDTO reservationUpdateDTO = getReservationUpdateDTO();
        ReservationService service = getReservationService();

        // WHEN
        Mockito.when(reservationRepository.findById(Mockito.any())).thenReturn(Optional.empty());
        Mockito.when(reservationRepository.findByEmailAndBetweenDateRangeNotCancelled(Mockito.anyString(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of());

        // THEN
        Assertions.assertThatThrownBy(() -> service.update(reservationId, reservationUpdateDTO))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void givenArrivalDateAfterDepartureDateWhenFindAvailabilityThenThrowException() {
        // GIVEN
        LocalDate arrivalDate = LocalDate.parse("2021-01-01");
        LocalDate departureDate = LocalDate.parse("2020-03-01");
        ReservationService service = getReservationService();

        // WHEN
        Mockito.when(availabilityService.findAvailability(arrivalDate, departureDate)).thenReturn(Collections.emptyList());

        // THEN
        Assertions.assertThatThrownBy(() -> service.findAvailability(arrivalDate, departureDate))
                .isInstanceOf(Exception.class);
    }

    private ReservationService getReservationService() {
        return new ReservationServiceImpl(availabilityService, validateService, reservationRepository, cacheManager);
    }

    private Reservation getReservation(ReservationRequestDTO reservationDTO) {
        return mapper(reservationDTO).toReservation();
    }

    private ReservationUpdateDTO getReservationUpdateDTO() {

        return new ReservationUpdateDTO(
                "test test test",
                "test@test.com",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(2)
        );
    }

    private Reservation getDefaultReservation() {
        Reservation.ReservationBuilder builder = Reservation.builder()
                .id(new Random().nextLong(10))
                .arrivalDate(LocalDate.now().plusDays(1))
                .departureDate(LocalDate.now().plusDays(2))
                .email("test@test.com")
                .createdDate(Instant.now())
                .name("test test test");
        return builder.build();
    }

    private ReservationRequestDTO getReservationDTO(String email, String fullName, LocalDate arrivalDate, LocalDate departureDate) {
        return ReservationRequestDTO.builder()
                .arrivalDate(arrivalDate)
                .departureDate(departureDate)
                .email(email)
                .name(fullName)
                .build();
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
