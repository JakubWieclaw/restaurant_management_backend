package com.example.restaurant_management_backend.controllers;

import com.example.restaurant_management_backend.dto.AverageRatingResponseDTO;
import com.example.restaurant_management_backend.dto.OpinionResponseDTO;
import com.example.restaurant_management_backend.jpa.model.command.OpinionAddCommand;
import com.example.restaurant_management_backend.services.OpinionService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/opinions")
@RequiredArgsConstructor
@Validated
public class OpinionController {

    private final OpinionService opinionService;
    private static final Logger logger = LoggerFactory.getLogger(OpinionController.class);

    @Operation(summary = "Add opinion")
    @PostMapping("/add")
    public ResponseEntity<OpinionResponseDTO> addOpinion(@RequestBody @Valid OpinionAddCommand opinionAddCommand) {
        OpinionResponseDTO opinion = opinionService.addOpinion(opinionAddCommand);
        logger.info("Opinion added");
        return ResponseEntity.status(201).body(opinion);
    }

    @Operation(summary = "Get average rating for meal")
    @GetMapping("/average-rating/{mealId}")
    public ResponseEntity<AverageRatingResponseDTO> getAverageRating(@PathVariable Long mealId) {
        logger.info("Getting average rating for meal with id: {}", mealId);
        return ResponseEntity.ok(opinionService.getAverageRating(mealId));
    }

    @Operation(summary = "Get opinions for customer")
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OpinionResponseDTO>> getOpinionsForCustomer(@PathVariable Long customerId) {
        logger.info("Getting opinions for customer with id: {}", customerId);
        return ResponseEntity.ok(opinionService.getOpinionsForCustomer(customerId));
    }

    @Operation(summary = "Get opinions for meal")
    @GetMapping("/meal/{mealId}")
    public ResponseEntity<List<OpinionResponseDTO>> getOpinionsForMeal(@PathVariable Long mealId) {
        logger.info("Getting opinions for meal with id: {}", mealId);
        return ResponseEntity.ok(opinionService.getOpinionsForMeal(mealId));
    }

    @Operation(summary = "Update opinion for customer")
    @PutMapping("/update")
    public ResponseEntity<OpinionResponseDTO> updateOpinion(@RequestBody @Valid OpinionAddCommand opinionAddCommand) {
        logger.info("Updating opinion for meal: {}", opinionAddCommand.getMealId());
        return ResponseEntity.ok(opinionService.updateOpinion(opinionAddCommand));
    }
}
