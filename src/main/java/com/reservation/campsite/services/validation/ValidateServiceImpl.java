package com.reservation.campsite.services.validation;

import com.reservation.campsite.exception.BadRequestException;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ValidateServiceImpl implements ValidateService {

    @Override
    public void validateDateRange(LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw BadRequestException.invalidDateRange(from, to);
        }
    }

    @Override
    public void isNull(Object object, @NotNull String name) {
        if (object == null) {
            throw BadRequestException.missingParams(name);
        }
    }
}
