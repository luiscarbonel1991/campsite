package com.reservation.campsite.controller;

import com.reservation.campsite.services.AvailabilityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final AvailabilityService availabilityService;

    public ReservationController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @GetMapping("/availability")
    public ResponseEntity<Map<LocalDate, Integer>> getAvailability() {
        return ResponseEntity.ok(
                availabilityService.findAvailability(LocalDate.now(), LocalDate.now().plus(10, ChronoUnit.DAYS))
        );
    }
}
