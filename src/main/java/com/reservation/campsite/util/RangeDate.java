package com.reservation.campsite.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.temporal.Temporal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RangeDate <T extends Temporal>{
    private T from;
    private T to;
}
