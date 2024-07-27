package com.example.restaurant_management_backend.jpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.restaurant_management_backend.jpa.repositories.MealRepository;
import com.example.restaurant_management_backend.jpa.model.Meal;

import java.util.List;
import java.util.Optional;

@Service
public class MealService {
    
    @Autowired
    private MealRepository mealRepository;
    
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

}