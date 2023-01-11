package com.reservation.campsite.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDate;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends BusinessException {

    private BadRequestException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public static BadRequestException missingParams(String... params) {
        ErrorCode code = ErrorCode.BAD_REQUEST_MISSING_PARAMETERS;
        return new BadRequestException(code,
                String.format("%s. [%s]", code.getMessage(), String.join(", ", params)));
    }

    public static BadRequestException invalidDateRange(LocalDate from, LocalDate to) {
        ErrorCode code = ErrorCode.BAD_REQUEST_INVALID_DATE_RANGE;
        return new BadRequestException(code,
                String.format("%s. Start date can not be after end date. Start date: %s, End date: %s",code.getMessage(), from, to));
    }
}
