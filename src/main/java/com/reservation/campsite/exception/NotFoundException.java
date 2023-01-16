package com.reservation.campsite.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDate;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends BusinessException {

    private NotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public static NotFoundException availabilityDate(LocalDate date) {
        ErrorCode errorCode = ErrorCode.NOT_FOUND_AVAILABILITY_TO_DATE;
        return new NotFoundException(errorCode,
                String.format("%s. Date: %s", errorCode.getMessageCode(), date));
    }

    public static NotFoundException reservationIdNotFound(Long id) {
        ErrorCode errorCode = ErrorCode.NOT_FOUND_RESERVATION_ID;
        return new NotFoundException(errorCode,
                String.format("%s. Id: %s", errorCode.getMessageCode(), id));
    }
}
