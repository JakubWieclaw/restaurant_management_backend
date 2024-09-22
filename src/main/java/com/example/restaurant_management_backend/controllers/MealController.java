package com.example.restaurant_management_backend.controllers;

import com.example.restaurant_management_backend.jpa.model.command.MealAddCommand;
import com.example.restaurant_management_backend.services.MealService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/meals")
@RequiredArgsConstructor
@Validated
public class MealController {
    private final MealService mealService;

    @Operation(summary = "Get all meals")
    @GetMapping("/all")
    public ResponseEntity<?> getAllMeals() {
        return ResponseEntity.ok(mealService.getAllMeals());
    }

    @Operation(summary = "Get meal by id")
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getMealById(@PathVariable Long id) {
        return ResponseEntity.ok(mealService.getMealById(id));
    }

    @Operation(summary = "Add a meal")
    @PostMapping("/add")
    public ResponseEntity<?> addMeal(@RequestBody MealAddCommand mealAddCommand) {
        return ResponseEntity.ok(mealService.addMeal(mealAddCommand));
    }

    @Operation(summary = "Delete a meal by id")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteMealById(@PathVariable Long id) {
        mealService.deleteMealById(id);
        return ResponseEntity.ok("Danie usunięte");
    }

    @Operation(summary = "Update a meal by id")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateMeal(@PathVariable Long id, @RequestBody MealAddCommand mealAddCommand) {
        return ResponseEntity.ok(mealService.updateMeal(id, mealAddCommand));
    }

    @Operation(summary = "Delete all meals with a given category id")
    @DeleteMapping("/delete-meals/{categoryId}")
    public ResponseEntity<?> deleteAllMealsByCategory(@PathVariable Long categoryId) {
        mealService.deleteMealsByCategoryId(categoryId);
        return ResponseEntity.ok("Wszystkie dania z kategorii o id " + categoryId + " zostały usunięte");
    }

    @Operation(summary = "Get all meals with a given category id")
    @GetMapping("/get-meals/{categoryId}")
    public ResponseEntity<?> getMealsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(mealService.getMealsByCategoryId(categoryId));
    }
}
