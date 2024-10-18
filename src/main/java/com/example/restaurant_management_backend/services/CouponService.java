package com.example.restaurant_management_backend.services;

import com.example.restaurant_management_backend.exceptions.CouponInvalidException;
import com.example.restaurant_management_backend.exceptions.NotFoundException;
import com.example.restaurant_management_backend.exceptions.ResourceConflictException;
import com.example.restaurant_management_backend.jpa.model.Coupon;
import com.example.restaurant_management_backend.jpa.model.Customer;
import com.example.restaurant_management_backend.jpa.model.Meal;
import com.example.restaurant_management_backend.jpa.repositories.CouponRepository;
import com.example.restaurant_management_backend.jpa.repositories.CustomerRepository;
import com.example.restaurant_management_backend.jpa.repositories.MealRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {

    public static final String CLIENT_NOT_FOUND = "Nie znaleziono klienta";
    public static final String MEAL_NOT_FOUND = "Nie znaleziono posiłku";
    public static final String COUPON_NOT_FOUND = "Nie znaleziono kuponu";
    public static final String COUPON_INACTIVE = "Kupon jest nieaktywny lub wygasł";
    public static final String COUPON_NOT_VALID_FOR_THIS_MEAL = "Kupon nie jest ważny dla tego posiłku";
    private final CouponRepository couponRepository;
    private final CustomerRepository customerRepository;
    private final MealRepository mealRepository;

    public Coupon createCoupon(String code, Double discountPercentage, Long customerId, Long mealId, LocalDateTime expiryDate) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException(CLIENT_NOT_FOUND));

        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new NotFoundException(MEAL_NOT_FOUND));

        if (couponRepository.findByCodeAndCustomerId(code, customerId).isPresent()) {
            throw new ResourceConflictException("Kupon o kodzie " + code + " już istnieje dla tego klienta");
        }

        Coupon coupon = Coupon.builder()
                .code(code)
                .discountPercentage(discountPercentage)
                .customer(customer)
                .meal(meal)
                .expiryDate(expiryDate)
                .active(true)
                .build();

        return couponRepository.save(coupon);
    }

    public void deactivateCoupon(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new NotFoundException(COUPON_NOT_FOUND));
        coupon.setActive(false);
        couponRepository.save(coupon);
    }

    public boolean isCouponValid(String code, Long customerId, Long mealId) {
        Coupon coupon = couponRepository.findByCodeAndCustomerId(code, customerId)
                .orElseThrow(() -> new NotFoundException(COUPON_NOT_FOUND));

        if (!coupon.getActive() || coupon.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new CouponInvalidException(COUPON_INACTIVE);
        }

        if (!coupon.getMeal().getId().equals(mealId)) {
            throw new CouponInvalidException(COUPON_NOT_VALID_FOR_THIS_MEAL);
        }

        return true;
    }

    public double applyCoupon(String code, Long customerId, Long mealId, double originalPrice) {
        if (isCouponValid(code, customerId, mealId)) {
            Coupon coupon = couponRepository.findByCodeAndCustomerId(code, customerId)
                    .orElseThrow(() -> new NotFoundException(COUPON_NOT_FOUND));
            double discountAmount = originalPrice * (coupon.getDiscountPercentage() / 100);
            return originalPrice - discountAmount;
        }
        return originalPrice;
    }

    public List<Coupon> getCouponsForCustomer(Long customerId) {
        return couponRepository.findByCustomerId(customerId);
    }

    public List<Coupon> getCouponsForMeal(Long mealId) {
        return couponRepository.findByMealId(mealId);
    }
}
