package com.example.restaurant_management_backend.controllers;

import com.example.restaurant_management_backend.dto.PossibleReservationHoursForDayDTO;
import com.example.restaurant_management_backend.jpa.model.TableReservation;
import com.example.restaurant_management_backend.jpa.model.command.MakeReservationCommand;
import com.example.restaurant_management_backend.services.TableReservationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class TableReservationController {
    private static final Logger logger = LoggerFactory.getLogger(TableReservationController.class);
    private final TableReservationService tableReservationService;

    @Operation(summary = "Get all reservations")
    @GetMapping
    public ResponseEntity<List<TableReservation>> getAllReservations() {
        logger.info("Fetching all reservations");
        List<TableReservation> reservations = tableReservationService.getAllTableReservations();
        logger.info("Fetched {} reservations", reservations.size());
        return ResponseEntity.ok(reservations);
    }

    @Operation(summary = "Get reservations for a specific day")
    @GetMapping("/day/{day}")
    public ResponseEntity<List<TableReservation>> getReservationsForDay(@PathVariable LocalDate day) {
        logger.info("Fetching reservations for day: {}", day);
        List<TableReservation> reservations = tableReservationService.getTableReservationsForDay(day);
        logger.info("Fetched {} reservations for day {}", reservations.size(), day);
        return ResponseEntity.ok(reservations);
    }

    @Operation(summary = "Get reservations for a specific customer")
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<TableReservation>> getReservationsForCustomer(@PathVariable Long customerId) {
        logger.info("Fetching reservations for customer ID: {}", customerId);
        List<TableReservation> reservations = tableReservationService.getTableReservationsForCustomer(customerId);
        logger.info("Fetched {} reservations for customer ID {}", reservations.size(), customerId);
        return ResponseEntity.ok(reservations);
    }

    @Operation(summary = "Get reservations for a specific table on a given day")
    @GetMapping("/table/{tableId}/day/{day}")
    public ResponseEntity<List<TableReservation>> getReservationsForTableOnDay(@PathVariable String tableId, @PathVariable LocalDate day) {
        logger.info("Fetching reservations for table ID: {} on day: {}", tableId, day);
        List<TableReservation> reservations = tableReservationService.getReservationsForTableOnDay(tableId, day);
        logger.info("Fetched {} reservations for table ID {} on day {}", reservations.size(), tableId, day);
        return ResponseEntity.ok(reservations);
    }

    @Operation(summary = "Get available hours for a specific day")
    @GetMapping("/available-hours/{day}")
    public ResponseEntity<List<LocalTime>> getPossibleHoursForDay(
            @PathVariable LocalDate day,
            @RequestParam int reservationDuration,
            @RequestParam int minutesToAdd,
            @RequestParam int numberOfPeople
    ) {
        logger.info("Fetching possible reservation hours for day: {} with duration: {}, interval: {}, people: {}",
                day, reservationDuration, minutesToAdd, numberOfPeople);
        List<LocalTime> possibleHours = tableReservationService.checkPossibleHoursForDay(day, reservationDuration, minutesToAdd, numberOfPeople);
        logger.info("Fetched {} possible hours for day {}", possibleHours.size(), day);
        return ResponseEntity.ok(possibleHours);
    }

    @Operation(summary = "Get available hours for multiple days")
    @GetMapping("/available-hours")
    public ResponseEntity<List<PossibleReservationHoursForDayDTO>> getPossibleHoursForDays(
            @RequestParam List<LocalDate> days,
            @RequestParam int reservationDuration,
            @RequestParam int minutesToAdd,
            @RequestParam int numberOfPeople
    ) {
        logger.info("Fetching possible reservation hours for days: {}, duration: {}, interval: {}, people: {}",
                days, reservationDuration, minutesToAdd, numberOfPeople);
        List<PossibleReservationHoursForDayDTO> possibleHours = tableReservationService.checkPossibleHoursForDays(days, reservationDuration, minutesToAdd, numberOfPeople);
        logger.info("Fetched possible hours for {} days", days.size());
        return ResponseEntity.ok(possibleHours);
    }

    @Operation(summary = "Create a new reservation")
    @PostMapping
    public ResponseEntity<TableReservation> createReservation(@RequestBody @Valid MakeReservationCommand request) {
        logger.info("Creating reservation for day: {}, start time: {}, end time: {}, people: {}, customer ID: {}",
                request.getDay(), request.getStartTime(), request.getEndTime(), request.getNumberOfPeople(), request.getCustomerId());
        TableReservation reservation = tableReservationService.makeReservation(request);
        logger.info("Created reservation with ID {}", reservation.getId());
        return ResponseEntity.ok(reservation);
    }

    @Operation(summary = "Get a reservation by ID")
    @GetMapping("/{id}")
    public ResponseEntity<TableReservation> getReservationById(@PathVariable Long id) {
        logger.info("Fetching reservation by ID: {}", id);
        TableReservation reservation = tableReservationService.getTableReservationById(id);
        logger.info("Fetched reservation with ID {}", id);
        return ResponseEntity.ok(reservation);
    }

    @Operation(summary = "Delete a reservation by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservationById(@PathVariable Long id) {
        logger.info("Deleting reservation by ID: {}", id);
        tableReservationService.deleteReservationById(id);
        logger.info("Deleted reservation with ID {}", id);
        return ResponseEntity.noContent().build();
    }
}
