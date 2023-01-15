package com.reservation.campsite.exception;

import lombok.Getter;

import java.time.Instant;

@Getter
public class BusinessException extends RuntimeException {

    private final Instant timestamp = Instant.now();
    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
