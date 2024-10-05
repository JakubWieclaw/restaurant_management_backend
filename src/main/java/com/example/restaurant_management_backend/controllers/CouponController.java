package com.example.restaurant_management_backend.controllers;

import com.example.restaurant_management_backend.jpa.model.Coupon;
import com.example.restaurant_management_backend.jpa.model.command.CouponAddCommand;
import com.example.restaurant_management_backend.services.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
@Validated
public class CouponController {

    private final CouponService couponService;

    @PostMapping("/create")
    public ResponseEntity<Coupon> createCoupon(@RequestBody @Valid CouponAddCommand couponRequestDTO) {
        Coupon coupon = couponService.createCoupon(
                couponRequestDTO.getCode(),
                couponRequestDTO.getDiscountPercentage(),
                couponRequestDTO.getCustomerId(),
                couponRequestDTO.getMealId(),
                couponRequestDTO.getExpiryDate()
        );
        return new ResponseEntity<>(coupon, HttpStatus.CREATED);
    }

    // Endpoint to deactivate a coupon
    @PutMapping("/deactivate/{couponId}")
    public ResponseEntity<Void> deactivateCoupon(@PathVariable Long couponId) {
        couponService.deactivateCoupon(couponId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Endpoint to validate a coupon
    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateCoupon(@RequestParam String code,
                                                  @RequestParam Long customerId,
                                                  @RequestParam Long mealId) {
        boolean isValid = couponService.isCouponValid(code, customerId, mealId);
        return new ResponseEntity<>(isValid, HttpStatus.OK);
    }

    // Endpoint to apply a coupon
    @GetMapping("/apply")
    public ResponseEntity<Double> applyCoupon(@RequestParam String code,
                                              @RequestParam Long customerId,
                                              @RequestParam Long mealId,
                                              @RequestParam double originalPrice) {
        double discountedPrice = couponService.applyCoupon(code, customerId, mealId, originalPrice);
        return new ResponseEntity<>(discountedPrice, HttpStatus.OK);
    }

    // Endpoint to get all coupons for a customer
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Coupon>> getCouponsForCustomer(@PathVariable Long customerId) {
        List<Coupon> coupons = couponService.getCouponsForCustomer(customerId);
        return new ResponseEntity<>(coupons, HttpStatus.OK);
    }

    // Endpoint to get all coupons for a specific meal
    @GetMapping("/meal/{mealId}")
    public ResponseEntity<List<Coupon>> getCouponsForMeal(@PathVariable Long mealId) {
        List<Coupon> coupons = couponService.getCouponsForMeal(mealId);
        return new ResponseEntity<>(coupons, HttpStatus.OK);
    }
}
