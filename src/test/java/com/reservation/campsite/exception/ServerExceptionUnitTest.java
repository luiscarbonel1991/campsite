package com.reservation.campsite.exception;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ServerExceptionUnitTest {

    @Test
    void givenMessageWhenNewServerExceptionThenValidException() {
        // GIVEN
        String message = "Unexpected error";

        // WHEN
        BusinessException exception = ServerException.unexpectedError(new Exception(message));

        // THEN
        assertThat(exception)
                .isNotNull()
                .hasMessage(message)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INTERNAL_SERVER_ERROR);
    }
}
