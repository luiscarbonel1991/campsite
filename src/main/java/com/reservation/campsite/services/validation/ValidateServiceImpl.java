package com.reservation.campsite.services.validation;

import com.reservation.campsite.exception.BadRequestException;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ValidateServiceImpl implements ValidateService {

    @Override
    public void validateDateRange(LocalDate from, String nameFromParam, LocalDate to, String nameToParam) {
        if (from.isAfter(to)) {
            throw BadRequestException.invalidDateRange(from, nameFromParam, to, nameToParam);
        }
    }

    @Override
    public void isNotNull(Object object, @NotNull String name) {
        if (object == null) {
            throw BadRequestException.missingParam(name);
        }
    }

    @Override
    public void isNotEmptyOrNull(String str, @NotNull String name) {
        if (str == null || str.isEmpty()) {
            throw BadRequestException.missingParam(name);
        }
    }
}
