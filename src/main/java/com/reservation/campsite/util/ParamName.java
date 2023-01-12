package com.reservation.campsite.util;

public enum ParamName {
    EMAIL("email"),
    NAME("name"),
    ARRIVAL("arrival"),
    DEPARTURE("departure"),
    RESPONSE_ERROR( "error");

    private final String nameParam;
    ParamName(String nameParam) {
        this.nameParam = nameParam;
    }

    public String getNameParam() {
        return nameParam;
    }
}
