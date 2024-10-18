package com.example.restaurant_management_backend.services;

import com.example.restaurant_management_backend.exceptions.NotFoundException;
import com.example.restaurant_management_backend.exceptions.ResourceConflictException;
import com.example.restaurant_management_backend.jpa.model.Category;
import com.example.restaurant_management_backend.jpa.model.command.CategoryAddCommand;
import com.example.restaurant_management_backend.jpa.repositories.CategoryRepository;
import com.example.restaurant_management_backend.jpa.repositories.MealRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    public static final String NOT_FOUND_CATEGORY_WITH_ID = "Nie znaleziono kategorii o id ";
    public static final String CANNOT_REMOVE_CATEGORY_MEALS_ARE_CONNECTED = "Nie można usunąć kategorii, ponieważ są z nią powiązane posiłki.";
    private final CategoryRepository categoryRepository;
    private final MealRepository mealRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_CATEGORY_WITH_ID + id));
    }

    public Category addCategory(CategoryAddCommand categoryAddCommand) {
        Category category = new Category(categoryAddCommand.getName());
        if (categoryAddCommand.getPhotographUrl() != null) {
            category.setPhotographUrl(categoryAddCommand.getPhotographUrl());
        }
        return categoryRepository.save(category);
    }

    public Category updateCategory(Long id, CategoryAddCommand categoryAddCommand) {
        Category category = getCategoryById(id);
        category.setName(categoryAddCommand.getName());
        if (categoryAddCommand.getPhotographUrl() != null) {
            category.setPhotographUrl(categoryAddCommand.getPhotographUrl());
        }
        return categoryRepository.save(category);
    }

    public void deleteCategoryById(Long id) {
        if (mealRepository.existsByCategoryId(id)) {
            throw new ResourceConflictException(CANNOT_REMOVE_CATEGORY_MEALS_ARE_CONNECTED);
        }
        categoryRepository.deleteById(id);
    }
}
