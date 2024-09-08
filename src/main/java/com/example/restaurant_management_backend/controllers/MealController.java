package com.example.restaurant_management_backend.controllers;

import com.example.restaurant_management_backend.jpa.model.Meal;
import com.example.restaurant_management_backend.jpa.model.command.MealAddCommand;
import com.example.restaurant_management_backend.services.CategoryService;
import com.example.restaurant_management_backend.services.MealService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/meals")
@RequiredArgsConstructor
@Validated
public class MealController {

    private final MealService mealService;
    private final CategoryService categoryService;

    @Operation(summary = "Get all meals")
    @GetMapping("/all")
    public ResponseEntity<?> getAllMeals() {
        return ResponseEntity.ok(mealService.getAllMeals());
    }

    @Operation(summary = "Get meal by id")
    @ApiResponse(description = "Returns a meal with a given id", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = Meal.class))})
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getMealById(@PathVariable Long id) {
        try {
            Optional<Meal> meal = mealService.getMealById(id);
            if (!meal.isEmpty()) { // do not change to isPresent(), Optional.Empty is still treated as present
                return ResponseEntity.ok(meal.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nie znaleziono dania");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nie znaleziono tego dania");
        }
    }

    @Operation(summary = "Get list of meals by provided ids")
    @GetMapping("/get-meals-by-ids")
    public ResponseEntity<?> getMealsByIds(@RequestParam("ids") List<Long> ids) {
        try {
            // if at least one meal is not found, return 404
            final var list = mealService.getMealsByIds(ids);
            if (list.size() != ids.size()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nie znaleziono tych dań");
            }
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nie znaleziono tych dań");
        }
    }


    @Operation(summary = "Add a meal")
    @PostMapping("/add")
    public ResponseEntity<?> addMeal(@RequestBody MealAddCommand mealAddCommand) {
        var logger = LoggerFactory.getLogger(MealController.class);
        try {

            // Validate if category exists
            if (!categoryService.getCategoryById(mealAddCommand.getCategoryId()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Kategoria o id " + mealAddCommand.getCategoryId() + " nie istnieje");
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nie znaleziono dania");
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
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nie znaleziono dania");
            }
        } catch (Exception e) {
            logger.error("Error updating meal", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nie znaleziono dania");
        }
    }

    @Operation(summary = "Delete all meals with a given category id")
    @DeleteMapping("/delete-meals/{categoryId}")
    public ResponseEntity<String> deleteAllMealsByCategory(@PathVariable Long categoryId) {
        var logger = LoggerFactory.getLogger(MealController.class);
        try {
            mealService.deleteMealsByCategoryId(categoryId);
            return ResponseEntity.ok("Wszystkie dania z kategorii o id " + categoryId + " zostały usunięte");
        } catch (IllegalArgumentException e) {
            logger.error("Category does not exist", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error deleting meals", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Błąd przy usuwaniu dań");
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
        } catch (Exception e) {
            logger.error("Error fetching meals", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Błąd podczas pobierania dań");
        }
    }

    @Operation(summary = "Search meals by name")
    @GetMapping("/search")
    public ResponseEntity<?> searchMealsByName(@RequestParam("name") String name) {
        var logger = LoggerFactory.getLogger(MealController.class);
        try {
            var meals = mealService.searchMealsByName(name);
            if (meals.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nie znaleziono dania o nazwie: " + name);
            }
            return ResponseEntity.ok(meals);
        } catch (Exception e) {
            logger.error("Error searching meals by name", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Błąd przy wyszukiwaniu dań");
        }
    }
}
