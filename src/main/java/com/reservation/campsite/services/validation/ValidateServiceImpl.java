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
        String arrivalParamName = ParamName.ARRIVAL_DATE.getNameParam();
        String departureParamName  = ParamName.DEPARTURE_DATE.getNameParam();
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
        String arrivalParamName = ParamName.ARRIVAL_DATE.getNameParam();
        String departureParamName  = ParamName.DEPARTURE_DATE.getNameParam();
        isNotNull(arrivalDate, arrivalParamName);
        isNotNull(departureDate, departureParamName);
        validateDateRange(arrivalDate, arrivalParamName, departureDate, departureParamName);

        if(arrivalDate.isBefore(validArrivalDateRange.getFrom())) {
            throw BadRequestException.invalidArrivalDate(arrivalDate, departureDate, validArrivalDateRange);
        }

        if(arrivalDate.isAfter(validArrivalDateRange.getTo())) {
            throw BadRequestException.invalidArrivalDate(arrivalDate, departureDate, validArrivalDateRange);
        }

        if (departureDate.isAfter(validArrivalDateRange.getTo())) {
            throw BadRequestException.invalidArrivalDate(arrivalDate, departureDate, validArrivalDateRange);
        }

        if (departureDate.isBefore(validArrivalDateRange.getFrom())) {
            throw BadRequestException.invalidArrivalDate(arrivalDate, departureDate, validArrivalDateRange);
        }
    }

    @Override
    public void validateEmail(String email, String paramName) {
        isNotEmptyOrNull(email, paramName);
        if(!EmailValidator.getInstance().isValid(email)) {
            throw BadRequestException.invalidEmail(email);
        }
    }
}
