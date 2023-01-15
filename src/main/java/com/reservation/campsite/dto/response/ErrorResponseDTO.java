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
public class ErrorResponseDTO {

    private String message;
    private String error;
    private Integer status;
    private Instant timestamp;
    private String code;

}
