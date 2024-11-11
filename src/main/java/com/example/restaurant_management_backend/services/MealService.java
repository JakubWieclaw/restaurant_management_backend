package com.example.restaurant_management_backend.services;

import com.example.restaurant_management_backend.exceptions.NotFoundException;
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
    public static final String NOT_FOUND_MEAL_ID = "Nie znaleziono dania o id ";
    public static final String CATEGORY_WITH_ID = "Kategoria o id ";
    public static final String DOES_NOT_EXIST = " nie istnieje";
    private final MealRepository mealRepository;
    private final CategoryRepository categoryRepository;
    private final MealMapper mealMapper;

    public List<Meal> searchMealsByName(String name) {
        return mealRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Meal> getAllMeals() {
        return mealRepository.findAll();
    }

    public Meal getMealById(Long id) {
        return mealRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MEAL_ID + id));
    }

    public Meal addMeal(MealAddCommand mealAddCommand) {
        validateCategory(mealAddCommand.getCategoryId());
        validateRemovableIngredients(mealAddCommand);
        Meal meal = mealMapper.toMeal(mealAddCommand);
        return mealRepository.save(meal);
    }

    public Meal updateMeal(Long id, MealAddCommand mealAddCommand) {
        Meal meal = getMealById(id);
        validateCategory(mealAddCommand.getCategoryId());
        validateRemovableIngredients(mealAddCommand);
        mealMapper.updateMeal(meal, mealAddCommand);
        return mealRepository.save(meal);
    }

    public void deleteMealById(Long id) {
        if (!mealRepository.existsById(id)) {
            throw new NotFoundException(NOT_FOUND_MEAL_ID + id);
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

    public boolean mealContainsIngredients(Long mealId, List<String> ingredients) {
        Meal meal = getMealById(mealId);
        // Check if all ingredients are present in the meal
        return meal.getIngredients().containsAll(ingredients);
    }

    private void validateCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new NotFoundException(CATEGORY_WITH_ID + categoryId + DOES_NOT_EXIST);
        }
    }

    public boolean mealExists(Long mealId) {
        return mealRepository.existsById(mealId);
    }
    
    private void validateRemovableIngredients(MealAddCommand mealAddCommand) {
        if (mealAddCommand.getRemovableIngredientsList() == null || mealAddCommand.getRemovableIngredientsList().isEmpty()) {
            return;
        }
        final var allIngredients = mealAddCommand.getIngredients();
        final var removableIngredients = mealAddCommand.getRemovableIngredientsList();
        // Check if all removable ingredients are present in the meal
        if (!allIngredients.containsAll(removableIngredients)) {
            throw new NotFoundException("Nie wszystkie składniki możliwe do usunięcia są obecne w daniu");
        }
    }
}
