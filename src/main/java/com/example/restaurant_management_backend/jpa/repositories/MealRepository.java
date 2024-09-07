package com.example.restaurant_management_backend.jpa.repositories;


import com.example.restaurant_management_backend.jpa.model.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {

    boolean existsByCategoryId(Long categoryId); // Check for meals by category

    void deleteByCategoryId(Long categoryId); // Delete all meals by category

    List<Meal> findByCategoryId(Long categoryId); // Find all meals by category

    List<Meal> findByNameContainingIgnoreCase(String name); // Search meals by name (case-insensitive, partial matches)
}
