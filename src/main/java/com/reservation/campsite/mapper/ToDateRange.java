package com.reservation.campsite.mapper;

import com.reservation.campsite.util.RangeDate;

import java.time.LocalDate;

@FunctionalInterface
public interface ToDateRange {

    RangeDate<LocalDate> toDateRange();
}
