package com.example.restaurant_management_backend.jpa.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.restaurant_management_backend.jpa.model.Meal;

@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {
    
    boolean existsByCategoryId(Long categoryId); // Check for meals by category

    void deleteByCategoryId(Long categoryId); // Delete all meals by category
}
