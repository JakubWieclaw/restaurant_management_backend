package com.example.restaurant_management_backend.services;

import com.example.restaurant_management_backend.exceptions.CouponInvalidException;
import com.example.restaurant_management_backend.exceptions.NotFoundException;
import com.example.restaurant_management_backend.exceptions.ResourceConflictException;
import com.example.restaurant_management_backend.jpa.model.Coupon;
import com.example.restaurant_management_backend.jpa.model.Meal;
import com.example.restaurant_management_backend.jpa.repositories.CouponRepository;
import com.example.restaurant_management_backend.jpa.repositories.MealRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private MealRepository mealRepository;

    @Mock
    private CustomerUserDetailsService customerService;

    @InjectMocks
    private CouponService couponService;

    private Meal meal;
    private Coupon coupon;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        meal = new Meal();
        meal.setId(2L);

        coupon = Coupon.builder()
                .id(3L)
                .code("POZNAN20")
                .discountPercentage(20.0)
                .meal(meal)
                .expiryDate(LocalDateTime.now().plusDays(1))
                .active(true)
                .build();
    }

    @Test
    void testCreateCouponSuccess() {
        when(mealRepository.findById(2L)).thenReturn(Optional.of(meal));
        when(couponRepository.findByCodeAndCustomerId("POZNAN20", 1L)).thenReturn(Optional.empty());
        when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);

        Coupon createdCoupon = couponService.createCoupon("POZNAN20", 20.0, 1L, 2L, LocalDateTime.now().plusDays(1));

        assertNotNull(createdCoupon);
        assertEquals("POZNAN20", createdCoupon.getCode());
        verify(couponRepository, times(1)).save(any(Coupon.class));
    }

    @Test
    void testCreateCouponCustomerNotFound() {
        when(mealRepository.findById(2L)).thenReturn(Optional.of(meal));
        when(customerService.getCustomerByIdOrThrowException(1L)).thenThrow(new NotFoundException("Nie znaleziono klienta"));
        NotFoundException thrown = assertThrows(NotFoundException.class, () ->
                couponService.createCoupon("POZNAN20", 20.0, 1L, 2L, LocalDateTime.now().plusDays(1)));

        assertEquals("Nie znaleziono klienta", thrown.getMessage());
    }

    @Test
    void testCreateCouponAlreadyExists() {
        when(mealRepository.findById(2L)).thenReturn(Optional.of(meal));
        when(couponRepository.findByCodeAndCustomerId("POZNAN20", 1L)).thenReturn(Optional.of(coupon));

        ResourceConflictException thrown = assertThrows(ResourceConflictException.class, () ->
                couponService.createCoupon("POZNAN20", 20.0, 1L, 2L, LocalDateTime.now().plusDays(1)));

        assertEquals("Kupon o kodzie POZNAN20 już istnieje dla tego klienta", thrown.getMessage());
    }

    @Test
    void testDeactivateCouponSuccess() {
        when(couponRepository.findById(3L)).thenReturn(Optional.of(coupon));

        couponService.deactivateCoupon(3L);

        assertFalse(coupon.getActive());
        verify(couponRepository, times(1)).save(coupon);
    }

    @Test
    void testDeactivateCouponNotFound() {
        when(couponRepository.findById(3L)).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class, () ->
                couponService.deactivateCoupon(3L));

        assertEquals("Nie znaleziono kuponu o kodzie 3", thrown.getMessage());
    }

    @Test
    void testIsCouponValidSuccess() {
        when(couponRepository.findByCodeAndCustomerId("POZNAN20", 1L)).thenReturn(Optional.of(coupon));

        boolean isValid = couponService.isCouponValid("POZNAN20", 1L, 2L);

        assertTrue(isValid);
    }

    @Test
    void testIsCouponInvalidExpired() {
        coupon.setExpiryDate(LocalDateTime.now().minusDays(1));
        when(couponRepository.findByCodeAndCustomerId("POZNAN20", 1L)).thenReturn(Optional.of(coupon));

        CouponInvalidException thrown = assertThrows(CouponInvalidException.class, () ->
                couponService.isCouponValid("POZNAN20", 1L, 2L));

        assertEquals("Kupon jest nieaktywny lub wygasł", thrown.getMessage());
    }

    @Test
    void testApplyCouponSuccess() {
        when(couponRepository.findByCodeAndCustomerId("POZNAN20", 1L)).thenReturn(Optional.of(coupon));

        double discountedPrice = couponService.applyCoupon("POZNAN20", 1L, 2L, 100.0);

        assertEquals(80.0, discountedPrice);
    }
}
