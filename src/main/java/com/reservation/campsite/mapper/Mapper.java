package com.reservation.campsite.mapper;

import com.reservation.campsite.dto.DateRangeDTO;
import com.reservation.campsite.dto.ErrorResponseDTO;
import com.reservation.campsite.exception.BusinessException;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

@UtilityClass
public class Mapper {

       public static ToErrorResponseDTO mapper(BusinessException exception, HttpStatus status) {
              return () -> ErrorResponseDTO.builder()
                      .code(exception.getErrorCode().name())
                      .message(exception.getMessage())
                      .timestamp(exception.getTimestamp())
                      .status(status.value())
                      .error(status.getReasonPhrase())
                      .build();
       }

       public static ToDateRangeDTO mapper(LocalDate from, LocalDate to) {
              return () -> DateRangeDTO.builder()
                      .from(from)
                      .to(to)
                      .build();
       }

}
