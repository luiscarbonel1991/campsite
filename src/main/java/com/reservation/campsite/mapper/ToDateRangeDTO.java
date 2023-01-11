package com.reservation.campsite.mapper;

import com.reservation.campsite.dto.DateRangeDTO;

@FunctionalInterface
public interface ToDateRangeDTO {

    DateRangeDTO toDateRangeDTO();
}
