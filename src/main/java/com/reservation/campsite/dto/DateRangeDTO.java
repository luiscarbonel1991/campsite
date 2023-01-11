package com.reservation.campsite.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DateRangeDTO {

    @NotNull
    private LocalDate from;
    @NotNull
    private LocalDate to;
}
