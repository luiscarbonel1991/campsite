package com.reservation.campsite.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ServerException extends BusinessException{
    public ServerException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }


    public static BusinessException unexpectedError(Exception e) {
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        return new ServerException(errorCode, e.getMessage());
    }
}
