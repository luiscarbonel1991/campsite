package com.reservation.campsite.controller;

import com.reservation.campsite.dto.request.ReservationRequestDTO;
import com.reservation.campsite.dto.request.ReservationUpdateDTO;
import com.reservation.campsite.dto.response.GeneralResponseDTO;
import com.reservation.campsite.dto.response.ReservationDTO;
import com.reservation.campsite.services.lock.LockService;
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

    private final LockService lockService;
    private final ReservationService reservationService;

    private static final String LOCK_AVAILABILITY_MODIFY = "lock_availability_modify";
    private static final int LOCK_UPDATE_AVAILABILITY_TIMEOUT_SECONDS = 10;
    private static final String CANCELLED_MSG = "Reservation cancelled successfully";

    public ReservationController(LockService lockService, ReservationService reservationService) {
        this.lockService = lockService;
        this.reservationService = reservationService;
    }

    @GetMapping("/availability")
    public ResponseEntity<Map<LocalDate, Integer>> getAvailability(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate arrivalDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate departureDate
    ) {
        return ResponseEntity.ok(reservationService.findAvailability(arrivalDate, departureDate));
    }

    @PostMapping
    public ResponseEntity<ReservationDTO> create(
            @RequestBody ReservationRequestDTO reservationRequestDTO
    ) {
        return lockService.lock(LOCK_AVAILABILITY_MODIFY, () -> ResponseEntity.status(HttpStatus.CREATED).body(
                mapper(reservationService.create(reservationRequestDTO)).toReservationDTO()), LOCK_UPDATE_AVAILABILITY_TIMEOUT_SECONDS);
    }

    @PutMapping("/{reservationId}")
    public ResponseEntity<ReservationDTO> update(
            @PathVariable Long reservationId,
            @RequestBody ReservationUpdateDTO reservationUpdateDTO
    ) {
        return lockService.lock(LOCK_AVAILABILITY_MODIFY, () -> ResponseEntity.ok(
                mapper(reservationService.update(reservationId, reservationUpdateDTO)).toReservationDTO()),
                LOCK_UPDATE_AVAILABILITY_TIMEOUT_SECONDS);

    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<GeneralResponseDTO> cancel(
            @PathVariable Long reservationId
    ) {
        return lockService.lock(LOCK_AVAILABILITY_MODIFY, () -> {
            reservationService.cancel(reservationId);
            return ResponseEntity.ok(
                    mapper(HttpStatus.OK.name(), HttpStatus.OK.value(), CANCELLED_MSG).toGeneralResponseDTO()
            );
        }, LOCK_UPDATE_AVAILABILITY_TIMEOUT_SECONDS);
    }
}
