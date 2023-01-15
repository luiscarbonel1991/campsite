package com.reservation.campsite.services.validation;

import com.reservation.campsite.util.RangeDate;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public interface ValidateService {
        void validateDateRange(LocalDate dateFrom, String paramNameToDateFrom, LocalDate dateTo, String paramNameToDateTo);
        void isNotNull(Object object, String paramName);

        void isNotEmptyOrNull(String str, @NotNull String paramName);

        void validateStayRangeDays(LocalDate arrivalDate, LocalDate departureDate, int minStayDays, int maxStayDays);

        void validateArrivalDate(LocalDate arrivalDate, LocalDate departureDate, RangeDate<LocalDate> validArrivalDateRange);

        void validateEmail(String email, String paramName);
}
