package com.reservation.campsite.controller;

import com.reservation.campsite.exception.BusinessException;
import com.reservation.campsite.exception.ServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static com.reservation.campsite.mapper.Mapper.mapper;

@RestControllerAdvice(annotations = RestController.class)
@Slf4j
public class ControllerAdviseHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> handleException(BusinessException e) {
        if (log.isDebugEnabled()) {
            log.debug(e.getMessage(), e);
        }
        return getResponseEntityByException(e);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception e) {
        log.error(e.getMessage(), e);
        return getResponseEntityByException(ServerException.unexpectedError(e));
    }

    private static ResponseEntity<Object> getResponseEntityByException(BusinessException e) {
        ResponseStatus responseStatus = AnnotatedElementUtils.findMergedAnnotation(e.getClass(), ResponseStatus.class);
        HttpStatus status = responseStatus != null ? responseStatus.value() : HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status).body(mapper(e, status).toErrorResponseDTO());
    }
}
