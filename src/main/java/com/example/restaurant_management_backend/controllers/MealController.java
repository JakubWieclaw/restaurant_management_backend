package com.example.restaurant_management_backend.controllers;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.restaurant_management_backend.jpa.model.Meal;
import com.example.restaurant_management_backend.jpa.model.command.MealAddCommand;
import com.example.restaurant_management_backend.service.MealService;

// import io.micrometer.core.ipc.http.HttpSender.Response;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
// import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/meals")
@RequiredArgsConstructor
@Validated
public class MealController {

    @Autowired
    private final MealService mealService;

    @Operation(summary = "Get all meals")
    @GetMapping("/all")
    public ResponseEntity<?> getAllMeals() {
        return ResponseEntity.ok(mealService.getAllMeals());
    }

    @Operation(summary = "Get meal by id")
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getMealById(@RequestParam Long id) {
        try {
            Optional<Meal> meal = mealService.getMealById(id);
            if (!meal.isEmpty()) { // do not chnage to isPresent(), Optional.Empty is still treated as present
                return ResponseEntity.ok(meal.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Meal of id " + id + " not found");
        }
    }

    @Operation(summary = "Add a meal")
    @PostMapping("/add")
    public ResponseEntity<?> addMeal(@RequestBody MealAddCommand mealAddCommand) {
        var logger = LoggerFactory.getLogger(MealController.class);
        try {
            logger.info("Adding meal");
            final var meal = new Meal(mealAddCommand.getName(), mealAddCommand.getPrice());
            final var savedMeal = mealService.saveMeal(meal);
            logger.info("Meal saved successfully: {}", savedMeal);
            return ResponseEntity.ok(savedMeal);
        } catch (ConstraintViolationException e) {
            logger.error("Validation error", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Validation error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error adding meal", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding meal");
        }
    }

    @Operation(summary = "Delete a meal by id")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteMealById(@RequestParam Long id) {
        try {
            mealService.deleteMealById(id);
            return ResponseEntity.ok("Meal of id " + id + " deleted");
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Meal of id " + id + " not found");
        }
    }

    @Operation(summary = "Update a meal by id")
    @PutMapping("update/{id}")
    public ResponseEntity<?> updateMeal(@PathVariable Long id, @RequestBody Meal meal) {
        try {
            Optional<Meal> mealToUpdate = mealService.getMealById(id);
            if (!mealToUpdate.isEmpty()) {
                meal.setId(id);
                final var updatedMeal = mealService.saveMeal(meal);
                return ResponseEntity.ok(updatedMeal);
            } else {
                return ResponseEntity.status(404).body("Meal of id " + id + " not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Meal of id " + id + " not found");
        }
    }

}
