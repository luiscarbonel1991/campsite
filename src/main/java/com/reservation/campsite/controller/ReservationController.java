package com.reservation.campsite.controller;

import com.reservation.campsite.dto.request.ReservationRequestDTO;
import com.reservation.campsite.dto.request.ReservationUpdateDTO;
import com.reservation.campsite.dto.response.GeneralResponseDTO;
import com.reservation.campsite.services.reservation.ReservationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

import static com.reservation.campsite.mapper.Mapper.mapper;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    private static final String CANCELLED_MSG = "Reservation cancelled successfully";

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
    public ResponseEntity<Map<String, Long>> create(
           @RequestBody ReservationRequestDTO reservationRequestDTO
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                reservationService.create(reservationRequestDTO)
        );
    }

    @PutMapping("/{reservationId}")
    public ResponseEntity<GeneralResponseDTO> update(
            @PathVariable Long reservationId,
            @RequestBody ReservationUpdateDTO reservationUpdateDTO
    ) {
        reservationService.update(reservationId, reservationUpdateDTO);
        return ResponseEntity.ok(
                mapper(HttpStatus.OK.name(), HttpStatus.OK.value(), CANCELLED_MSG ).toGeneralResponseDTO()
        );
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<GeneralResponseDTO> cancel(
            @PathVariable Long reservationId
    ) {
        reservationService.cancel(reservationId);
        return ResponseEntity.ok(
                mapper(HttpStatus.OK.name(), HttpStatus.OK.value(), CANCELLED_MSG ).toGeneralResponseDTO()
        );
    }
}
