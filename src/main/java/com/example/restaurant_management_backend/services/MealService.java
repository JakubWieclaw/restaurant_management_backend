package com.example.restaurant_management_backend.services;

import com.example.restaurant_management_backend.jpa.model.Meal;
import com.example.restaurant_management_backend.jpa.model.command.MealAddCommand;
import com.example.restaurant_management_backend.jpa.repositories.CategoryRepository;
import com.example.restaurant_management_backend.jpa.repositories.MealRepository;
import com.example.restaurant_management_backend.mappers.MealMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MealService {
    private final MealRepository mealRepository;
    private final CategoryRepository categoryRepository;
    private final MealMapper mealMapper;

    public List<Meal> getAllMeals() {
        return mealRepository.findAll();
    }

    public Meal getMealById(Long id) {
        return mealRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono dania o id " + id));
    }

    public Meal addMeal(MealAddCommand mealAddCommand) {
        validateCategory(mealAddCommand.getCategoryId());
        Meal meal = mealMapper.toMeal(mealAddCommand);
        return mealRepository.save(meal);
    }

    public Meal updateMeal(Long id, MealAddCommand mealAddCommand) {
        Meal meal = getMealById(id);
        validateCategory(mealAddCommand.getCategoryId());
        mealMapper.updateMeal(meal, mealAddCommand);
        return mealRepository.save(meal);
    }

    public void deleteMealById(Long id) {
        if (!mealRepository.existsById(id)) {
            throw new IllegalArgumentException("Nie znaleziono dania o id " + id);
        }
        mealRepository.deleteById(id);
    }

    @Transactional
    public void deleteMealsByCategoryId(Long categoryId) {
        validateCategory(categoryId);
        mealRepository.deleteByCategoryId(categoryId);
    }

    public List<Meal> getMealsByCategoryId(Long categoryId) {
        validateCategory(categoryId);
        return mealRepository.findByCategoryId(categoryId);
    }

    private void validateCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new IllegalArgumentException("Kategoria o id " + categoryId + " nie istnieje");
        }
    }
}
