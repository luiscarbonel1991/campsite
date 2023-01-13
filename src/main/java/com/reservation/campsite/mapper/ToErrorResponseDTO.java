package com.reservation.campsite.mapper;

import com.reservation.campsite.dto.response.ErrorResponseDTO;

@FunctionalInterface
public interface ToErrorResponseDTO {
    ErrorResponseDTO toErrorResponseDTO();
}
