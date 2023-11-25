package com.reservation.campsite.controller;

import com.reservation.campsite.dto.request.ReservationRequestDTO;
import com.reservation.campsite.dto.request.ReservationUpdateDTO;
import com.reservation.campsite.dto.response.ErrorResponseDTO;
import com.reservation.campsite.dto.response.GeneralResponseDTO;
import com.reservation.campsite.dto.response.ReservationDTO;
import com.reservation.campsite.services.reservation.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Search availability by arrival and departure dates")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search availability by arrival and departure dates"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))}),
    })
    @GetMapping("/availability")
    public ResponseEntity<Map<LocalDate, Boolean>> getAvailability(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate arrivalDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate departureDate
    ) {
        return ResponseEntity.ok(reservationService.findAvailability(arrivalDate, departureDate));
    }


    @Operation(summary = "Create a reservation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create a reservation",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ReservationDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))}),
    })
    @PostMapping
    public ResponseEntity<ReservationDTO> create(
            @RequestBody ReservationRequestDTO reservationRequestDTO
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper(reservationService.create(reservationRequestDTO))
                        .toReservationDTO());
    }


    @Operation(summary = "Update a reservation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update a reservation",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ReservationDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Reservation not found by id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))}),
    })
    @PutMapping("/{reservationId}")
    public ResponseEntity<ReservationDTO> update(
            @PathVariable Long reservationId,
            @RequestBody ReservationUpdateDTO reservationUpdateDTO
    ) {
        return ResponseEntity.ok(
                mapper(reservationService.update(reservationId, reservationUpdateDTO)).toReservationDTO());

    }


    @Operation(summary = "Cancel a reservation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cancel a reservation",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = GeneralResponseDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Reservation not found by id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))}),
    })
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<GeneralResponseDTO> cancel(
            @PathVariable Long reservationId
    ) {
        reservationService.cancel(reservationId);
        return ResponseEntity.ok(
                mapper(HttpStatus.OK.name(), HttpStatus.OK.value(), CANCELLED_MSG).toGeneralResponseDTO()
        );
    }
}
