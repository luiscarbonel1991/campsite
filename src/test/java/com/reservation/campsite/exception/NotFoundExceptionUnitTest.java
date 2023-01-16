package com.reservation.campsite.exception;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class NotFoundExceptionUnitTest {

    @Test
    void givenDateWithNotAvailabilityWhenNewNotFoundByAvailabilityDateExceptionThenValidException() {
        // GIVEN
        LocalDate availabilityDate = LocalDate.parse("2020-01-01");
        ErrorCode errorCodeExpected = ErrorCode.NOT_FOUND_AVAILABILITY_TO_DATE;

        // WHEN
        BusinessException exception = NotFoundException.availabilityDate(availabilityDate);

        // THEN
        assertThat(exception)
                .isNotNull()
                .hasMessageContaining(errorCodeExpected.getMessageCode())
                .hasFieldOrPropertyWithValue("errorCode", errorCodeExpected);
    }

    @Test
    void givenReservationIdWhenNewNotFoundExceptionByReservationIdNotFoundThenValidException() {
        // GIVEN
        Long reservationId = 1L;
        ErrorCode errorCodeExpected = ErrorCode.NOT_FOUND_RESERVATION_ID;

        // WHEN
        BusinessException exception = NotFoundException.reservationIdNotFound(reservationId);

        // THEN
        assertThat(exception)
                .isNotNull()
                .hasMessageContaining(errorCodeExpected.getMessageCode())
                .hasMessageContaining(String.valueOf(reservationId))
                .hasFieldOrPropertyWithValue("errorCode", errorCodeExpected);
    }
}
