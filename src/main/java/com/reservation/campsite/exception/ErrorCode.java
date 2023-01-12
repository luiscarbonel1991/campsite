package com.reservation.campsite.exception;

public enum ErrorCode {
    BAD_REQUEST_MISSING_PARAMETERS("Missing parameters"),
    BAD_REQUEST_INVALID_DATE_RANGE("Invalid date range"),
    INTERNAL_SERVER_ERROR("Unexpected error"),
    BAD_REQUEST_MALFORMED_BODY("Malformed body"),;

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
