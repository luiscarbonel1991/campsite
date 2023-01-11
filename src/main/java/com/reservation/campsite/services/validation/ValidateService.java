package com.reservation.campsite.services.validation;

import java.time.LocalDate;

public interface ValidateService {
        void validateDateRange(LocalDate from, LocalDate to);
        void isNull(Object object, String name);
}
