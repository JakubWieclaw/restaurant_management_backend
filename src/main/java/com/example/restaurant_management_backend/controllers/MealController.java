package com.example.restaurant_management_backend.controllers;

import com.example.restaurant_management_backend.jpa.model.Meal;
import com.example.restaurant_management_backend.jpa.model.command.MealAddCommand;
import com.example.restaurant_management_backend.services.MealService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meals")
@RequiredArgsConstructor
@Validated
public class MealController {
    private final MealService mealService;

    @Operation(summary = "Get all meals")
    @GetMapping("/all")
    public ResponseEntity<List<Meal>> getAllMeals() {
        return ResponseEntity.ok(mealService.getAllMeals());
    }

    @Operation(summary = "Get meal by id")
    @GetMapping("/get/{id}")
    public ResponseEntity<Meal> getMealById(@PathVariable Long id) {
        return ResponseEntity.ok(mealService.getMealById(id));
    }

    @Operation(summary = "Add a meal")
    @PostMapping("/add")
    public ResponseEntity<Meal> addMeal(@RequestBody MealAddCommand mealAddCommand) {
        return ResponseEntity.ok(mealService.addMeal(mealAddCommand));
    }

    @Operation(summary = "Delete a meal by id")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteMealById(@PathVariable Long id) {
        mealService.deleteMealById(id);
        return ResponseEntity.ok("Danie usunięte");
    }

    @Operation(summary = "Update a meal by id")
    @PutMapping("/update/{id}")
    public ResponseEntity<Meal> updateMeal(@PathVariable Long id, @RequestBody MealAddCommand mealAddCommand) {
        return ResponseEntity.ok(mealService.updateMeal(id, mealAddCommand));
    }

    @Operation(summary = "Delete all meals with a given category id")
    @DeleteMapping("/delete-meals/{categoryId}")
    public ResponseEntity<String> deleteAllMealsByCategory(@PathVariable Long categoryId) {
        mealService.deleteMealsByCategoryId(categoryId);
        return ResponseEntity.ok("Wszystkie dania z kategorii o id " + categoryId + " zostały usunięte");
    }

    @Operation(summary = "Get all meals with a given category id")
    @GetMapping("/get-meals/{categoryId}")
    public ResponseEntity<List<Meal>> getMealsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(mealService.getMealsByCategoryId(categoryId));
    }

    @Operation(summary = "Search meals by name")
    @GetMapping("/search")
    public ResponseEntity<?> searchMealsByName(@RequestParam("name") String name) {
        var meals = mealService.searchMealsByName(name);
        if (meals.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nie znaleziono dania o nazwie: " + name);
        }
        return ResponseEntity.ok(meals);
    }
}
