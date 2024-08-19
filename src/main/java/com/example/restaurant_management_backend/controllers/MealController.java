package com.example.restaurant_management_backend.controllers;

import java.util.Optional;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.restaurant_management_backend.jpa.model.Meal;
import com.example.restaurant_management_backend.jpa.model.command.MealAddCommand;
import com.example.restaurant_management_backend.service.CategoryService;
import com.example.restaurant_management_backend.service.MealService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.ConstraintViolationException;
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

    @Autowired
    private final CategoryService categoryService;

    @Operation(summary = "Get all meals")
    @GetMapping("/all")
    public ResponseEntity<?> getAllMeals() {
        return ResponseEntity.ok(mealService.getAllMeals());
    }

    @Operation(summary = "Get meal by id")
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getMealById(@PathVariable Long id) {
        try {
            Optional<Meal> meal = mealService.getMealById(id);
            if (!meal.isEmpty()) { // do not chnage to isPresent(), Optional.Empty is still treated as present
                return ResponseEntity.ok(meal.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(404).body("ORA-20001: Nie znaleziono tego dania");
        }
    }

    @Operation(summary = "Add a meal")
    @PostMapping("/add")
    public ResponseEntity<?> addMeal(@RequestBody MealAddCommand mealAddCommand) {
        var logger = LoggerFactory.getLogger(MealController.class);
        try {

            // Validate if category exists
            if (!categoryService.getCategoryById(mealAddCommand.getCategoryId()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Category with id " + mealAddCommand.getCategoryId() + " does not exist");
            }

            logger.info("Adding meal");
            final var meal = new Meal();
            meal.setName(mealAddCommand.getName());
            meal.setPrice(mealAddCommand.getPrice());
            meal.setPhotographUrl(mealAddCommand.getPhotographUrl());
            meal.setIngredients(mealAddCommand.getIngredients());
            meal.setWeightOrVolume(mealAddCommand.getWeightOrVolume());
            meal.setUnitType(mealAddCommand.getUnitType());
            meal.setCategoryId(mealAddCommand.getCategoryId());
            meal.setAllergens(mealAddCommand.getAllergens());
            meal.setCalories(mealAddCommand.getCalories());
            final var savedMeal = mealService.saveMeal(meal);
            logger.info("Meal saved successfully: {}", savedMeal);
            return ResponseEntity.ok(savedMeal);
        } catch (TransactionSystemException e) {
            Throwable cause = e.getRootCause();
            if (cause instanceof ConstraintViolationException) {
                logger.error("Validation error", cause);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cena nie może być ujemna");
            }
            logger.error("Transaction error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Błąd przy dodawaniu dania");
        } catch (Exception e) {
            logger.error("Error adding meal", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Błąd przy dodawaniu dania");
        }
    }

    @Operation(summary = "Delete a meal by id")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteMealById(@PathVariable Long id) {
        try {
            mealService.deleteMealById(id);
            return ResponseEntity.ok("Danie usunięte");
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Nie znaleziono dania");
        }
    }

    @Operation(summary = "Update a meal by id")
    @PutMapping("update/{id}")
    public ResponseEntity<?> updateMeal(@PathVariable Long id, @RequestBody MealAddCommand mealAddCommand) {
        var logger = LoggerFactory.getLogger(MealController.class);
        try {
            Optional<Meal> mealToUpdate = mealService.getMealById(id);
            if (!mealToUpdate.isEmpty()) {

                // Validate if category exists
                if (!categoryService.getCategoryById(mealAddCommand.getCategoryId()).isPresent()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Podana kategoria nie istnieje");
                }

                final var meal = mealToUpdate.get();
                meal.setName(mealAddCommand.getName());
                meal.setPrice(mealAddCommand.getPrice());
                meal.setPhotographUrl(mealAddCommand.getPhotographUrl());
                meal.setIngredients(mealAddCommand.getIngredients());
                meal.setWeightOrVolume(mealAddCommand.getWeightOrVolume());
                meal.setUnitType(mealAddCommand.getUnitType());
                meal.setCategoryId(mealAddCommand.getCategoryId());
                meal.setAllergens(mealAddCommand.getAllergens());
                meal.setCalories(mealAddCommand.getCalories());
                final var updatedMeal = mealService.saveMeal(meal);
                logger.info("Meal updated successfully: {}", updatedMeal);
                return ResponseEntity.ok(updatedMeal);
            } else {
                return ResponseEntity.status(404).body("Nie znaleziono dania");
            }
        } catch (Exception e) {
            logger.error("Error updating meal", e);
            return ResponseEntity.status(404).body("Nie znaleziono dania");
        }
    }

    @Operation(summary = "Delete all meals with a given category id")
    @DeleteMapping("/delete-meals/{categoryId}")
    public ResponseEntity<String> deleteAllMealsByCategory(@PathVariable Long categoryId) {
        var logger = LoggerFactory.getLogger(MealController.class);
        try {
            mealService.deleteMealsByCategoryId(categoryId);
            return ResponseEntity.ok("All meals with category id " + categoryId + " have been deleted");
        } catch (IllegalArgumentException e) {
            logger.error("Category does not exist", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error deleting meals", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting meals");
        }
    }

    @Operation(summary = "Get all meals with a given category id")
    @GetMapping("/get-meals/{categoryId}")
    public ResponseEntity<?> getMealsByCategory(@PathVariable Long categoryId) {
        var logger = LoggerFactory.getLogger(MealController.class);
        try {
            return ResponseEntity.ok(mealService.getMealsByCategoryId(categoryId));
        } catch (IllegalArgumentException e) {
            logger.error("Category does not exist", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Podana kategoria nie istnieje");
        } 
        catch (Exception e) {
            logger.error("Error fetching meals", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Błąd podczas pobierania dań");
        }
    }

}
