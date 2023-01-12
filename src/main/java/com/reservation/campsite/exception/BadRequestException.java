package com.reservation.campsite.exception;

import com.reservation.campsite.util.RangeDate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDate;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends BusinessException {

    private BadRequestException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public static BadRequestException missingParam(String... params) {
        ErrorCode code = ErrorCode.BAD_REQUEST_MISSING_PARAMETERS;
        return new BadRequestException(code,
                String.format("%s. [%s]", code.getMessage(), String.join(", ", params)));
    }

    public static BadRequestException invalidDateRange(LocalDate from, String nameFromParam, LocalDate to, String nameToParam) {
        ErrorCode code = ErrorCode.BAD_REQUEST_INVALID_DATE_RANGE;
        return new BadRequestException(code,
                String.format("%s. Start date can not be after end date. %s: %s, %s: %s",code.getMessage(),
                        nameFromParam, from, nameToParam, to));
    }

    public static BadRequestException invalidStayRange(LocalDate arrivalDate, LocalDate departureDate, int minStayDays, int maxStayDays) {
        ErrorCode code = ErrorCode.BAD_REQUEST_INVALID_STAY_RANGE;
        return new BadRequestException(code,
                String.format("%s. Stay range must be between %d and %d days. Arrival date: %s, Departure date: %s",
                        code.getMessage(), minStayDays, maxStayDays, arrivalDate, departureDate));
    }

    public static BadRequestException invalidArrivalDate(LocalDate arrivalDate, RangeDate<LocalDate> validRange) {
        ErrorCode code = ErrorCode.BAD_REQUEST_INVALID_DATE_RANGE;
        return new BadRequestException(code,
                String.format("%s. Reservation must be made between %s and %s. Arrival date: %s",
                        code.getMessage(), validRange.getFrom(), validRange.getTo(), arrivalDate));
    }

    public static BadRequestException invalidEmail(String email) {
        ErrorCode code = ErrorCode.BAD_REQUEST_INVALID_EMAIL;
        return new BadRequestException(code,
                String.format("%s. Email: %s", code.getMessage(), email));
    }

    public static BadRequestException alreadyExists(String paramName, Object object) {
        ErrorCode code = ErrorCode.BAD_REQUEST_ALREADY_EXISTS;
        return new BadRequestException(code,
                String.format("%s. %s: %s", code.getMessage(), paramName, object));
    }
}
