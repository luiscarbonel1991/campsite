package com.reservation.campsite.dto.response;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationDTO {


    private Long id;

    private String name;

    private String email;

    private LocalDate arrivalDate;

    private LocalDate departureDate;

    private Instant createdDate;

    private Instant updateDate;

    private Instant cancelDate;
}
