package com.example.restaurant_management_backend.controllers;

import com.example.restaurant_management_backend.jpa.model.Category;
import com.example.restaurant_management_backend.jpa.model.command.CategoryAddCommand;
import com.example.restaurant_management_backend.services.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Validated
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Get all categories")
    @GetMapping("/all")
    public ResponseEntity<?> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @Operation(summary = "Get category by id")
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        try {
            Optional<Category> category = categoryService.getCategoryById(id);
            if (category.isPresent()) {
                return ResponseEntity.ok(category.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Błąd podczas pobierania kategorii");
        }
    }

    @Operation(summary = "Add a category")
    @PostMapping("/add")
    public ResponseEntity<?> addCategory(@RequestBody CategoryAddCommand categoryAddCommand) {
        try {
            var category = new Category(categoryAddCommand.getName());
            var savedCategory = categoryService.saveCategory(category);
            return ResponseEntity.ok(savedCategory);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding category");
        }
    }

    @Operation(summary = "Delete a category by id")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCategoryById(@PathVariable Long id) {
        try {
            categoryService.deleteCategoryById(id);
            return ResponseEntity.ok("Kategoria została usunięta");
        } catch (IllegalStateException e) {
            // Handle specific exception when meals are associated with the category
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nie można usunąć kategorii dopóki związane są z nią posiłki.");
        } catch (Exception e) {
            // General error handling for other exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Błąd podczas usuwania kategorii");
        }
    }

    @Operation(summary = "Update a category by id")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id,
                                            @RequestBody @Valid CategoryAddCommand categoryAddCommand) {
        try {
            Optional<Category> categoryToUpdate = categoryService.getCategoryById(id);
            if (categoryToUpdate.isPresent()) {
                var category = categoryToUpdate.get();
                category.setName(categoryAddCommand.getName());
                var updatedCategory = categoryService.saveCategory(category);
                return ResponseEntity.ok(updatedCategory);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nie znaleziono kategorii");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Błąd podczas aktualizacji kategorii");
        }
    }
}
