package com.reservation.campsite.dto.request;

import java.time.LocalDate;

public record ReservationUpdateDTO(
    String name,
    String email,
    LocalDate arrivalDate,
    LocalDate departureDate
) {
}
