package com.reservation.campsite.services.validation;

import com.reservation.campsite.exception.BadRequestException;
import com.reservation.campsite.util.ParamName;
import com.reservation.campsite.util.RangeDate;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ValidateServiceImpl implements ValidateService {

    @Override
    public void validateDateRange(LocalDate dateFrom, String paramNameToDateFrom, LocalDate dateTo, String paramNameToDateTo) {
        if (dateFrom.isAfter(dateTo)) {
            throw BadRequestException.invalidDateRange(dateFrom, paramNameToDateFrom, dateTo, paramNameToDateTo);
        }
    }

    @Override
    public void isNotNull(Object object, @NotNull String paramName) {
        if (object == null) {
            throw BadRequestException.missingParam(paramName);
        }
    }

    @Override
    public void isNotEmptyOrNull(String str, @NotNull String paramName) {
        if (str == null || str.isEmpty()) {
            throw BadRequestException.missingParam(paramName);
        }
    }

    @Override
    public void validateStayRangeDays(@NotNull LocalDate arrivalDate, @NotNull LocalDate departureDate, int minStayDays, int maxStayDays) {
        String arrivalParamName = getParamName(ParamName.ARRIVAL, "arrivalDate");
        String departureParamName  = getParamName(ParamName.DEPARTURE, "departureDate");
        isNotNull(arrivalDate, arrivalParamName);
        isNotNull(departureDate, departureParamName);
        validateDateRange(arrivalDate, arrivalParamName, departureDate, departureParamName);
        long days = arrivalDate.datesUntil(departureDate).count();
        if (days < minStayDays || days > maxStayDays) {
            throw BadRequestException.invalidStayRange(arrivalDate, departureDate, minStayDays, maxStayDays);
        }
    }

    @Override
    public void validateArrivalDate(@NotNull LocalDate arrivalDate, @NotNull LocalDate departureDate, RangeDate<LocalDate> validArrivalDateRange) {

        if(arrivalDate.isBefore(validArrivalDateRange.getFrom())) {
            throw BadRequestException.invalidArrivalDate(arrivalDate, validArrivalDateRange);
        }

        if(arrivalDate.isAfter(validArrivalDateRange.getTo())) {
            throw BadRequestException.invalidArrivalDate(arrivalDate, validArrivalDateRange);
        }
    }

    @Override
    public void validateEmail(String email, String paramName) {
        if(!EmailValidator.getInstance().isValid(email)) {
            throw BadRequestException.invalidEmail(email);
        }
    }

    private String getParamName(ParamName paramName, String defaultValue) {
        return paramName == null ? defaultValue : paramName.getNameParam();
    }
}
