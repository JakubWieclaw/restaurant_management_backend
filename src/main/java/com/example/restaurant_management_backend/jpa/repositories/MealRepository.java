package com.example.restaurant_management_backend.jpa.repositories;

import com.example.restaurant_management_backend.jpa.model.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {
}
