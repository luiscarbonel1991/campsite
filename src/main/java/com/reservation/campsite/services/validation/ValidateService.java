package com.reservation.campsite.services.validation;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public interface ValidateService {
        void validateDateRange(LocalDate from, String nameFromParam, LocalDate to, String nameToParam);
        void isNotNull(Object object, String name);

        void isNotEmptyOrNull(String str, @NotNull String name);
}
