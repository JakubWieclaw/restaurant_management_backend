package com.example.restaurant_management_backend.mappers;

import com.example.restaurant_management_backend.jpa.model.Category;
import com.example.restaurant_management_backend.jpa.model.Meal;
import com.example.restaurant_management_backend.jpa.model.command.MealAddCommand;
import org.springframework.stereotype.Component;

@Component
public class MealMapper {
    public Meal toMeal(MealAddCommand mealAddCommand, Category category) {
        return new Meal(
                mealAddCommand.getName(),
                mealAddCommand.getPrice(),
                mealAddCommand.getPhotographUrl(),
                mealAddCommand.getIngredients(),
                mealAddCommand.getWeightOrVolume(),
                mealAddCommand.getUnitType(),
                category,
                mealAddCommand.getAllergens(),
                mealAddCommand.getCalories()
        );
    }

    public void updateMeal(Meal meal, MealAddCommand mealAddCommand, Category category) {
        meal.setName(mealAddCommand.getName());
        meal.setPrice(mealAddCommand.getPrice());
        meal.setPhotographUrl(mealAddCommand.getPhotographUrl());
        meal.setIngredients(mealAddCommand.getIngredients());
        meal.setWeightOrVolume(mealAddCommand.getWeightOrVolume());
        meal.setUnitType(mealAddCommand.getUnitType());
        meal.setCategory(category);
        meal.setAllergens(mealAddCommand.getAllergens());
        meal.setCalories(mealAddCommand.getCalories());
    }
}
