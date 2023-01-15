package com.reservation.campsite.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GeneralResponseDTO{
    private String message;
    private int status;
    private Instant timestamp;
    private String code;
}
