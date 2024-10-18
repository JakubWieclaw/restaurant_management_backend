package com.example.restaurant_management_backend.controllers;

import com.example.restaurant_management_backend.dto.PossibleReservationHoursForDay;
import com.example.restaurant_management_backend.dto.ReservationQueryRequest;
import com.example.restaurant_management_backend.dto.ReservationRequest;
import com.example.restaurant_management_backend.exceptions.InvalidReservationException;
import com.example.restaurant_management_backend.services.TableReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/table-reservation")
@RequiredArgsConstructor
public class TableReservationController {

    private final TableReservationService tableReservationService;

    // Endpoint to check possible reservation hours for a specific day using RequestBody
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

    // Endpoint to check possible reservation hours for multiple days using RequestBody
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

    // Endpoint to make a reservation using RequestBody
    @PostMapping("/make")
    public ResponseEntity<String> makeReservation(@RequestBody ReservationRequest reservationRequest) {
        try {
            tableReservationService.makeReservation(
                    reservationRequest.getDay(),
                    reservationRequest.getStartTime(),
                    reservationRequest.getEndTime(),
                    reservationRequest.getNumberOfPeople(),
                    reservationRequest.getCustomerId()
            );
            return ResponseEntity.ok("Reservation successful");
        } catch (InvalidReservationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
