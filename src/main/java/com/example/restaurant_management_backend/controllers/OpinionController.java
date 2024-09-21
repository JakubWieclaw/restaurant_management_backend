package com.example.restaurant_management_backend.controllers;

import com.example.restaurant_management_backend.jpa.model.command.OpinionAddCommand;
import com.example.restaurant_management_backend.jpa.model.dto.OpinionResponseDTO;
import com.example.restaurant_management_backend.services.OpinionService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @Operation(summary = "Add opinion")
    @PostMapping("/add")
    public ResponseEntity<OpinionResponseDTO> addOpinion(@RequestBody @Valid OpinionAddCommand opinionAddCommand) {
        OpinionResponseDTO opinion = opinionService.addOpinion(opinionAddCommand);
        return ResponseEntity.ok(opinion);
    }

    @Operation(summary = "Get average rating for meal")
    @GetMapping("/average-rating/{mealId}")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long mealId) {
        double averageRating = opinionService.getAverageRating(mealId);
        return ResponseEntity.ok(averageRating);
    }

    @Operation(summary = "Get opinions for customer")
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OpinionResponseDTO>> getOpinionsForCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(opinionService.getOpinionsForCustomer(customerId));
    }

    @Operation(summary = "Get opinions for meal")
    @GetMapping("/meal/{mealId}")
    public ResponseEntity<List<OpinionResponseDTO>> getOpinionsForMeal(@PathVariable Long mealId) {
        return ResponseEntity.ok(opinionService.getOpinionsForMeal(mealId));
    }

    @Operation(summary = "Update opinion for customer")
    @PutMapping("/update")
    public ResponseEntity<OpinionResponseDTO> updateOpinion(@RequestBody @Valid OpinionAddCommand opinionAddCommand) {
        OpinionResponseDTO opinion = opinionService.updateOpinion(opinionAddCommand);
        return ResponseEntity.ok(opinion);
    }
}
