package com.reservation.campsite.controller;

import com.reservation.campsite.dto.request.ReservationRequestDTO;
import com.reservation.campsite.services.reservation.ReservationService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/availability")
    public ResponseEntity<Map<LocalDate, Integer>> getAvailability(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo
    ) {
        return ResponseEntity.ok(reservationService.findAvailability(dateFrom, dateTo));
    }

    @PostMapping
    public ResponseEntity<String> create(
            @Valid @RequestBody ReservationRequestDTO reservationRequestDTO
    ) {
        reservationService.create(reservationRequestDTO);
        return ResponseEntity.ok("Hello");
    }
}
