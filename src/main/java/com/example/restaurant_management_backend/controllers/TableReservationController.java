package com.example.restaurant_management_backend.controllers;

import com.example.restaurant_management_backend.dto.PossibleReservationHoursForDayDTO;
import com.example.restaurant_management_backend.jpa.model.TableReservation;
import com.example.restaurant_management_backend.jpa.model.command.CheckReservationTimesCommand;
import com.example.restaurant_management_backend.jpa.model.command.MakeReservationCommand;
import com.example.restaurant_management_backend.services.TableReservationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/table-reservation")
@RequiredArgsConstructor
@Validated
public class TableReservationController {

    private final TableReservationService tableReservationService;
    private static final Logger logger = LoggerFactory.getLogger(TableReservationController.class);

    @Operation(summary = "Get available hours for a single day")
    @PostMapping("/available-hours/day")
    public ResponseEntity<List<LocalTime>> getAvailableHoursForDay(@RequestBody @Valid CheckReservationTimesCommand request) {
        List<LocalTime> availableHours = tableReservationService.checkPossibleHoursForDay(
                request.getDays().getFirst(), // Use the first day from the list (for single day queries)
                request.getDuration(),
                request.getMinutesToAdd(),
                request.getPeople()
        );
        logger.info("Getting available hours for day: {}", request.getDays().getFirst());
        return ResponseEntity.ok(availableHours);
    }

    @Operation(summary = "Get available hours for multiple days")
    @PostMapping("/available-hours/days")
    public ResponseEntity<List<PossibleReservationHoursForDayDTO>> getAvailableHoursForDays(@RequestBody @Valid CheckReservationTimesCommand request) {
        List<PossibleReservationHoursForDayDTO> availableHoursForDays = tableReservationService.checkPossibleHoursForDays(
                request.getDays(),
                request.getDuration(),
                request.getMinutesToAdd(),
                request.getPeople()
        );
        logger.info("Getting available hours for days: {}", request.getDays());
        return ResponseEntity.ok(availableHoursForDays);
    }

    @Operation(summary = "Make reservation")
    @PostMapping("/make")
    public ResponseEntity<TableReservation> makeReservation(@RequestBody @Valid MakeReservationCommand request) {
        TableReservation tableReservation = tableReservationService.makeReservation(
                request.getDay(),
                request.getStartTime(),
                request.getEndTime(),
                request.getNumberOfPeople(),
                request.getCustomerId()
        );
        logger.info("Made reservation for day: {}, start time: {}, end time: {}, number of people: {}, customer id: {}",
                request.getDay(), request.getStartTime(), request.getEndTime(), request.getNumberOfPeople(), request.getCustomerId());
        return ResponseEntity.ok(tableReservation);
    }

    @Operation(summary = "Get all reservations")
    @GetMapping("/reservations")
    public ResponseEntity<List<TableReservation>> getReservations() {
        List<TableReservation> reservations = tableReservationService.getAllTableReservations();
        logger.info("Getting all reservations");
        return ResponseEntity.ok(reservations);
    }

    @Operation(summary = "Get reservations for a specific day")
    @GetMapping("/reservations")
    public ResponseEntity<List<TableReservation>> getReservations(@RequestParam("day") LocalDate day) {
        List<TableReservation> reservations = tableReservationService.getTableReservationsForDay(day);
        logger.info("Getting reservations for a day {}", day);
        return ResponseEntity.ok(reservations);
    }
}
