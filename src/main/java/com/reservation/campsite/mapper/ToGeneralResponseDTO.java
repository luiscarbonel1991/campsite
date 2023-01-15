package com.reservation.campsite.mapper;

import com.reservation.campsite.dto.response.GeneralResponseDTO;

@FunctionalInterface
public interface ToGeneralResponseDTO {
    GeneralResponseDTO toGeneralResponseDTO();
}
