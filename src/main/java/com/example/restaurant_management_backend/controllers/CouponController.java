package com.example.restaurant_management_backend.controllers;

import com.example.restaurant_management_backend.jpa.model.Coupon;
import com.example.restaurant_management_backend.jpa.model.command.CouponAddCommand;
import com.example.restaurant_management_backend.services.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(CouponController.class);

    @Operation(summary = "Create coupon")
    @PostMapping("/create")
    public ResponseEntity<Coupon> createCoupon(@RequestBody @Valid CouponAddCommand couponRequestDTO) {
        Coupon coupon = couponService.createCoupon(
                couponRequestDTO.getCode(),
                couponRequestDTO.getDiscountPercentage(),
                couponRequestDTO.getCustomerId(),
                couponRequestDTO.getMealId(),
                couponRequestDTO.getExpiryDate()
        );
        logger.info("Creating a coupon");
        return new ResponseEntity<>(coupon, HttpStatus.CREATED);
    }

    @Operation(summary = "Deactivate coupon")
    @PutMapping("/deactivate/{couponId}")
    public ResponseEntity<Void> deactivateCoupon(@PathVariable Long couponId) {
        couponService.deactivateCoupon(couponId);
        logger.info("Deactivating coupon with id: {}", couponId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Validate coupon")
    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateCoupon(@RequestParam String code,
                                                  @RequestParam Long customerId,
                                                  @RequestParam Long mealId) {
        boolean isValid = couponService.isCouponValid(code, customerId, mealId);
        logger.info("Validating coupon with code: {}", code);
        return new ResponseEntity<>(isValid, HttpStatus.OK);
    }

    @Operation(summary = "Apply coupon")
    @GetMapping("/apply")
    public ResponseEntity<Double> applyCoupon(@RequestParam String code,
                                              @RequestParam Long customerId,
                                              @RequestParam Long mealId,
                                              @RequestParam double originalPrice) {
        double discountedPrice = couponService.applyCoupon(code, customerId, mealId, originalPrice);
        logger.info("Applying coupon with code: {}", code);
        return new ResponseEntity<>(discountedPrice, HttpStatus.OK);
    }

    @Operation(summary = "Get all coupons for a customer")
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Coupon>> getCouponsForCustomer(@PathVariable Long customerId) {
        List<Coupon> coupons = couponService.getCouponsForCustomer(customerId);
        logger.info("Getting all coupons for customer with id: {}", customerId);
        return new ResponseEntity<>(coupons, HttpStatus.OK);
    }

    @Operation(summary = "Get all coupons for a meal")
    @GetMapping("/meal/{mealId}")
    public ResponseEntity<List<Coupon>> getCouponsForMeal(@PathVariable Long mealId) {
        List<Coupon> coupons = couponService.getCouponsForMeal(mealId);
        logger.info("Getting all coupons for meal with id: {}", mealId);
        return new ResponseEntity<>(coupons, HttpStatus.OK);
    }
}
