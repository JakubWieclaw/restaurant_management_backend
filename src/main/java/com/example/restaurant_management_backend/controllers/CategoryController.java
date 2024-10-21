package com.example.restaurant_management_backend.controllers;

import com.example.restaurant_management_backend.jpa.model.Category;
import com.example.restaurant_management_backend.jpa.model.command.CategoryAddCommand;
import com.example.restaurant_management_backend.services.CategoryService;
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
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Validated
public class CategoryController {

    private final CategoryService categoryService;
    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @Operation(summary = "Get all categories")
    @GetMapping("/all")
    public ResponseEntity<List<Category>> getAllCategories() {
        logger.info("Getting all categories");
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @Operation(summary = "Get category by id")
    @GetMapping("/get/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        logger.info("Getting category with id: {}", id);
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @Operation(summary = "Add a category")
    @PostMapping("/add")
    public ResponseEntity<Category> addCategory(@RequestBody @Valid CategoryAddCommand categoryAddCommand) {
        logger.info("Adding a category");
        return ResponseEntity.ok(categoryService.addCategory(categoryAddCommand));
    }

    @Operation(summary = "Delete a category by id")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCategoryById(@PathVariable Long id) {
        categoryService.deleteCategoryById(id);
        logger.info("Category with id: {} deleted", id);
        return ResponseEntity.ok("Kategoria została usunięta");
    }

    @Operation(summary = "Update a category by id")
    @PutMapping("/update/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id,
                                                   @RequestBody @Valid CategoryAddCommand categoryAddCommand) {
        logger.info("Updating category with id: {}", id);
        return ResponseEntity.ok(categoryService.updateCategory(id, categoryAddCommand));
    }
}
