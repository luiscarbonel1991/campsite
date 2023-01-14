package com.reservation.campsite.exception;

public enum ErrorCode {
    BAD_REQUEST_MISSING_PARAMETERS("Missing parameters"),
    BAD_REQUEST_INVALID_DATE_RANGE("Invalid date range"),
    INTERNAL_SERVER_ERROR("Unexpected error"),
    BAD_REQUEST_MALFORMED_BODY("Malformed body"),
    BAD_REQUEST_INVALID_STAY_RANGE( "Invalid stay range"),
    BAD_REQUEST_INVALID_EMAIL(  "Invalid email"),
    BAD_REQUEST_RESERVATION_ALREADY_EXISTS( "Reservation already exists."),
    NOT_FOUND_AVAILABILITY_TO_DATE( "Not found availability to specific date"),
    NOT_FOUND_RESERVATION_ID( "Not found reservation id"),
    BAD_REQUEST_TO_HIGH_DEMAND( "To high demand");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
