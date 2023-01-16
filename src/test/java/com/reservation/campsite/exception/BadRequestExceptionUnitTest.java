package com.reservation.campsite.exception;

import com.reservation.campsite.dto.response.ReservationDTO;
import com.reservation.campsite.util.ParamName;
import com.reservation.campsite.util.RangeDate;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BadRequestExceptionUnitTest {

    @Test
    void givenInvalidDateRangeWhenNewBadRequestExceptionByInvalidDateRangeThenValidException() {
        // GIVEN
        String startDate = "2020-01-01";
        String endDate = "2020-01-02";
        LocalDate arrivalDate = LocalDate.parse(startDate);
        LocalDate departureDate = LocalDate.parse(endDate);
        String arrivalParamName = ParamName.ARRIVAL_DATE.getNameParam();
        String departureParamName  = ParamName.DEPARTURE_DATE.getNameParam();
        ErrorCode errorCodeExpected = ErrorCode.BAD_REQUEST_INVALID_DATE_RANGE;

        // WHEN
        BusinessException exception = BadRequestException.invalidDateRange(arrivalDate, arrivalParamName, departureDate, departureParamName);

        // THEN
        assertThat(exception)
                .isNotNull()
                .hasMessageContaining(errorCodeExpected.getMessageCode())
                .hasMessageContaining(arrivalParamName)
                .hasMessageContaining(departureParamName)
                .hasMessageContaining(startDate)
                .hasMessageContaining(endDate)
                .hasFieldOrPropertyWithValue("errorCode", errorCodeExpected);
    }

    @Test
    void givenMissingParamWhenNewBadRequestExceptionByMissingParamThenValidException() {
        // GIVEN
        String paramNameOne = "paramName";
        String paramNameTwo = "paramName";
        String[] missingPrams = new String[]{paramNameOne, paramNameTwo};
        String joinParameters = String.join(", ", missingPrams);
        ErrorCode errorCodeExpected = ErrorCode.BAD_REQUEST_MISSING_PARAMETERS;

        // WHEN
        BusinessException exception = BadRequestException.missingParam(missingPrams);

        // THEN
        assertThat(exception)
                .isNotNull()
                .hasMessageContaining(errorCodeExpected.getMessageCode())
                .hasMessageContaining(paramNameOne)
                .hasMessageContaining(paramNameTwo)
                .hasMessageContaining(joinParameters)
                .hasFieldOrPropertyWithValue("errorCode", errorCodeExpected);
    }

    @Test
    void givenStayRangeWhenNewBadRequestExceptionByInvalidStayRangeThenValidException() {
        // GIVEN
        String startDate = "2020-01-01";
        String endDate = "2020-01-02";
        LocalDate arrivalDate = LocalDate.parse(startDate);
        LocalDate departureDate = LocalDate.parse(endDate);
        int minStayDays = 1;
        int maxStayDays = 3;
        ErrorCode errorCodeExpected = ErrorCode.BAD_REQUEST_INVALID_STAY_RANGE;

        // WHEN
        BusinessException exception = BadRequestException.invalidStayRange(arrivalDate, departureDate, minStayDays, maxStayDays);

        // THEN
        assertThat(exception)
                .isNotNull()
                .hasMessageContaining(errorCodeExpected.getMessageCode())
                .hasMessageContaining(startDate)
                .hasMessageContaining(endDate)
                .hasMessageContaining(String.valueOf(minStayDays))
                .hasMessageContaining(String.valueOf(maxStayDays))
                .hasFieldOrPropertyWithValue("errorCode", errorCodeExpected);
    }

    @Test
    void givenArrivalDateWhenNewBadRequestExceptionThenValidInvalidArrivalDateException() {
        // GIVEN
        String startDate = "2020-01-01";
        String endDate = "2020-01-02";
        LocalDate arrivalDate = LocalDate.parse(startDate);
        LocalDate departureDate = LocalDate.parse(endDate);
        LocalDate validArrivalDate = LocalDate.now().plusDays(1);
        LocalDate validDepartureDate = validArrivalDate.plusDays(31);
        RangeDate<LocalDate> validRange = new RangeDate<>(validDepartureDate, validDepartureDate);
        ErrorCode errorCodeExpected = ErrorCode.BAD_REQUEST_INVALID_DATE_RANGE;

        // WHEN
        BusinessException exception = BadRequestException.invalidArrivalDate(arrivalDate, departureDate, validRange);

        // THEN
        assertThat(exception)
                .isNotNull()
                .hasMessageContaining(errorCodeExpected.getMessageCode())
                .hasMessageContaining(arrivalDate.toString())
                .hasMessageContaining(departureDate.toString())
                .hasMessageContaining(validRange.getFrom().toString())
                .hasMessageContaining(validRange.getTo().toString())
                .hasFieldOrPropertyWithValue("errorCode", errorCodeExpected);
    }

    @Test
    void givenInvalidEmailWhenNewBadRequestExceptionInvalidEmailThenValidException() {
        // GIVEN
        String email = "invalidEmail@invalid.error";
        ErrorCode errorCodeExpected = ErrorCode.BAD_REQUEST_INVALID_EMAIL;

        // WHEN
        BusinessException exception = BadRequestException.invalidEmail(email);

        // THEN
        assertThat(exception)
                .isNotNull()
                .hasMessageContaining(errorCodeExpected.getMessageCode())
                .hasMessageContaining(email)
                .hasFieldOrPropertyWithValue("errorCode", errorCodeExpected);
    }

    @Test
    void givenReservationThatAlreadyExistWhenNewBadRequestExceptionReservationAlreadyExistsThenValidException() {
        // GIVEN
        ReservationDTO reservation = ReservationDTO.builder()
                .email("email@email.com")
                .arrivalDate(LocalDate.now().plusDays(1))
                .departureDate(LocalDate.now().plusDays(2))
                .build();
        ErrorCode errorCodeExpected = ErrorCode.BAD_REQUEST_RESERVATION_ALREADY_EXISTS;

        // WHEN
        BusinessException exception = BadRequestException.reservationAlreadyExists(reservation);

        // THEN
        assertThat(exception)
                .isNotNull()
                .hasMessageContaining(errorCodeExpected.getMessageCode())
                .hasMessageContaining(reservation.getEmail())
                .hasMessageContaining(reservation.getArrivalDate().toString())
                .hasMessageContaining(reservation.getDepartureDate().toString())
                .hasFieldOrPropertyWithValue("errorCode", errorCodeExpected);
    }

    @Test
    void givenCancelledDateReservationWhenNewBadRequestExceptionByUpdateCancelledReservationThenValidException() {
        // GIVEN
        Instant cancelledDate = Instant.now();
        ErrorCode errorCodeExpected = ErrorCode.BAD_REQUEST_UPDATE_CANCELLED_RESERVATION;

        // WHEN
        BusinessException exception = BadRequestException.updateCancelledReservation(cancelledDate);

        // THEN
        assertThat(exception)
                .isNotNull()
                .hasMessageContaining(errorCodeExpected.getMessageCode())
                .hasMessageContaining(cancelledDate.toString())
                .hasFieldOrPropertyWithValue("errorCode", errorCodeExpected);
    }

    @Test
    void givenCancelledReservationIdWhenNewBadRequestExceptionByAlreadyCancelledThenValidException() {

        // GIVEN
        Long reservationId = 1L;
        ErrorCode errorCodeExpected = ErrorCode.BAD_REQUEST_ALREADY_CANCELLED;

        // WHEN
        BusinessException exception = BadRequestException.alreadyCancelled(reservationId);

        // THEN
        assertThat(exception)
                .isNotNull()
                .hasMessageContaining(errorCodeExpected.getMessageCode())
                .hasMessageContaining(reservationId.toString())
                .hasFieldOrPropertyWithValue("errorCode", errorCodeExpected);
    }

    @Test
    void whenNewBadRequestExceptionByLockThenValidException() {

        // WHEN
        BusinessException exception = BadRequestException.lock();
        ErrorCode errorCodeExpected = ErrorCode.BAD_REQUEST_TO_HIGH_DEMAND;

        // THEN
        assertThat(exception)
                .isNotNull()
                .hasMessageContaining(errorCodeExpected.getMessageCode())
                .hasFieldOrPropertyWithValue("errorCode", errorCodeExpected);
    }
}
