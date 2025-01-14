package com.example.restaurant_management_backend.services;

import com.stripe.Stripe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StripeConfigServiceTest {

    private StripeConfigService stripeConfigService;

    @Value("${stripe.secret.key}")
    private String stripeApiKey;

    @BeforeEach
    void setUp() {
        stripeConfigService = new StripeConfigService();
        ReflectionTestUtils.setField(stripeConfigService, "stripeApiKey", "test_secret_key");
    }

    @Test
    void testInit_SetsApiKey() {
        // Act
        stripeConfigService.init();

        // Assert
        assertEquals("test_secret_key", Stripe.apiKey, "Stripe API key should be initialized with the provided value.");
    }
}
