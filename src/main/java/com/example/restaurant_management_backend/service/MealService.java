package com.example.restaurant_management_backend.service;

// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.restaurant_management_backend.jpa.repositories.CategoryRepository;
import com.example.restaurant_management_backend.jpa.repositories.MealRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import com.example.restaurant_management_backend.jpa.model.Meal;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MealService {
    
    private final MealRepository mealRepository;

    private final CategoryRepository categoryRepository;
    
    public List<Meal> getAllMeals() {
        return mealRepository.findAll();
    }

    public Optional<Meal> getMealById(Long id) {
        return mealRepository.findById(id);
    }

    public Meal saveMeal(Meal meal) {
        return mealRepository.save(meal);
    }

    public void deleteMealById(Long id) {
        mealRepository.deleteById(id);
    }

    @Transactional
    public void deleteMealsByCategoryId(Long categoryId) {
        // Optionally, check if the category exists before deleting meals
        if (!categoryRepository.existsById(categoryId)) {
            throw new IllegalArgumentException("Category with id " + categoryId + " does not exist.");
        }
        mealRepository.deleteByCategoryId(categoryId);
    }

    public List<Meal> getMealsByCategoryId(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new IllegalArgumentException("Category with id " + categoryId + " does not exist.");
        }
        return mealRepository.findByCategoryId(categoryId);
    }
}