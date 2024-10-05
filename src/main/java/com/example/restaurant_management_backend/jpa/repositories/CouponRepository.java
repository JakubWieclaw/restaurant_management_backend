package com.example.restaurant_management_backend.jpa.repositories;

import com.example.restaurant_management_backend.jpa.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Optional<Coupon> findByCodeAndCustomerId(String code, Long customerId);

    List<Coupon> findByCustomerId(Long customerId);

    List<Coupon> findByMealId(Long mealId);

    List<Coupon> findByCustomerIdAndMealId(Long customerId, Long mealId);
}
