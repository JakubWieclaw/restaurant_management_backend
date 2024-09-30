package com.example.restaurant_management_backend.jpa.repositories;

import com.example.restaurant_management_backend.jpa.model.Opinion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OpinionRepository extends JpaRepository<Opinion, Long> {

    boolean existsByMealIdAndCustomerId(Long mealId, Long customerId);

    List<Opinion> findByMealId(Long mealId);

    List<Opinion> findByCustomerId(Long customerId);

    Optional<Opinion> findByMealIdAndCustomerId(Long mealId, Long customerId);

    List<Opinion> findByMealIdAndRating(Long mealId, int rating);
}
