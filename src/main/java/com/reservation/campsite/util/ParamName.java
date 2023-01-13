package com.reservation.campsite.util;

public enum ParamName {
    ARRIVAL("arrival"),
    ARRIVAL_DATE("arrivalDate"),
    DEPARTURE("departure"),
    DEPARTURE_DATE("departureDate"),
    EMAIL("email"),
    NAME("name"),
    RESERVATION("reservation"),
    RESERVATION_ID("reservationId"),
    RESPONSE_ERROR( "error");

    private final String nameParam;
    ParamName(String nameParam) {
        this.nameParam = nameParam;
    }

    public String getNameParam() {
        return nameParam;
    }
}
