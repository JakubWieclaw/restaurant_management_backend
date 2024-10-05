package com.example.restaurant_management_backend.controllers;

import com.example.restaurant_management_backend.jpa.model.Coupon;
import com.example.restaurant_management_backend.services.CouponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CouponControllerTest {

    @Mock
    private CouponService couponService;

    @InjectMocks
    private CouponController couponController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(couponController).build();
    }

    @Test
    void testCreateCouponSuccess() throws Exception {
        Coupon coupon = Coupon.builder().id(1L).code("POZNAN20").discountPercentage(20.0).build();

        when(couponService.createCoupon(anyString(), anyDouble(), anyLong(), anyLong(), any(LocalDateTime.class))).thenReturn(coupon);

        mockMvc.perform(post("/api/coupons/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\": \"POZNAN20\", " +
                                "\"discountPercentage\": 20.0, " +
                                "\"customerId\": 1, " +
                                "\"mealId\": 2, " +
                                "\"expiryDate\": \"2024-12-31T23:59:59\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("POZNAN20"))
                .andExpect(jsonPath("$.discountPercentage").value(20.0));
    }
}
