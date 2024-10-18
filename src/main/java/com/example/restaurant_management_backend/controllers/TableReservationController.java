package com.example.restaurant_management_backend.controllers;

import com.example.restaurant_management_backend.dto.PossibleReservationHoursForDay;
import com.example.restaurant_management_backend.dto.ReservationQueryRequest;
import com.example.restaurant_management_backend.dto.ReservationRequest;
import com.example.restaurant_management_backend.services.TableReservationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/table-reservation")
@RequiredArgsConstructor
@Validated
public class TableReservationController {

    private final TableReservationService tableReservationService;

    @Operation(summary = "Get available hours for a single day")
    @PostMapping("/available-hours/day")
    public ResponseEntity<List<LocalTime>> getAvailableHoursForDay(@RequestBody ReservationQueryRequest queryRequest) {
        List<LocalTime> availableHours = tableReservationService.checkPossibleHoursForDay(
                queryRequest.getDays().getFirst(), // Use the first day from the list (for single day queries)
                queryRequest.getDuration(),
                queryRequest.getMinutesToAdd(),
                queryRequest.getPeople()
        );
        return ResponseEntity.ok(availableHours);
    }

    @Operation(summary = "Get available hours for multiple days")
    @PostMapping("/available-hours/days")
    public ResponseEntity<List<PossibleReservationHoursForDay>> getAvailableHoursForDays(@RequestBody ReservationQueryRequest queryRequest) {
        List<PossibleReservationHoursForDay> availableHoursForDays = tableReservationService.checkPossibleHoursForDays(
                queryRequest.getDays(),
                queryRequest.getDuration(),
                queryRequest.getMinutesToAdd(),
                queryRequest.getPeople()
        );
        return ResponseEntity.ok(availableHoursForDays);
    }

    @Operation(summary = "Make reservation")
    @PostMapping("/make")
    public ResponseEntity<Void> makeReservation(@RequestBody ReservationRequest reservationRequest) {
        tableReservationService.makeReservation(
                reservationRequest.getDay(),
                reservationRequest.getStartTime(),
                reservationRequest.getEndTime(),
                reservationRequest.getNumberOfPeople(),
                reservationRequest.getCustomerId()
        );
        return ResponseEntity.noContent().build();
    }
}
