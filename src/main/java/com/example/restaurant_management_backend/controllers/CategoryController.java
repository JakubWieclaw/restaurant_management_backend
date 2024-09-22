package com.example.restaurant_management_backend.controllers;

import com.example.restaurant_management_backend.jpa.model.command.CategoryAddCommand;
import com.example.restaurant_management_backend.services.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @Operation(summary = "Add a category")
    @PostMapping("/add")
    public ResponseEntity<?> addCategory(@RequestBody @Valid CategoryAddCommand categoryAddCommand) {
        return ResponseEntity.ok(categoryService.addCategory(categoryAddCommand));
    }

    @Operation(summary = "Delete a category by id")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCategoryById(@PathVariable Long id) {
        categoryService.deleteCategoryById(id);
        return ResponseEntity.ok("Kategoria została usunięta");
    }

    @Operation(summary = "Update a category by id")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id,
                                            @RequestBody @Valid CategoryAddCommand categoryAddCommand) {
        return ResponseEntity.ok(categoryService.updateCategory(id, categoryAddCommand));
    }
}
