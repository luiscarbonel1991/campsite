package com.reservation.campsite.mapper;

import com.reservation.campsite.dto.response.ErrorResponseDTO;
import com.reservation.campsite.dto.request.ReservationRequestDTO;
import com.reservation.campsite.dto.response.GeneralResponseDTO;
import com.reservation.campsite.exception.BusinessException;
import com.reservation.campsite.persistence.entity.Reservation;
import com.reservation.campsite.util.RangeDate;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;

import java.time.Instant;
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

       public static ToDateRange mapper(LocalDate from, LocalDate to) {
              return () -> RangeDate.<LocalDate>builder()
                      .from(from)
                      .to(to)
                      .build();
       }

       public static ToReservation mapper(ReservationRequestDTO dto) {
              return () -> Reservation.builder()
                      .name(dto.getName())
                      .email(dto.getEmail())
                      .arrivalDate(dto.getArrivalDate())
                      .departureDate(dto.getDepartureDate())
                      .createdDate(Instant.now())
                      .build();
       }

       public ToGeneralResponseDTO mapper(String code, int status, String message) {
              return () -> GeneralResponseDTO
                      .builder()
                      .code(code)
                      .status(status)
                      .message(message)
                      .timestamp(Instant.now())
                      .build();
       }

}
