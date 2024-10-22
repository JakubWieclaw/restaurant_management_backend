package com.example.restaurant_management_backend.controllers;

import com.example.restaurant_management_backend.payU.PayUOrderResponse;
import com.example.restaurant_management_backend.payU.PayUService;
import com.example.restaurant_management_backend.payU.PayUTokenResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Validated
public class PayUController {
    private static final Logger logger = LoggerFactory.getLogger(PayUController.class);
    private final PayUService payUService;

    @Operation(summary = "Create order")
    @PostMapping
    public ResponseEntity<PayUOrderResponse> createOrder(
            @RequestParam("orderId") Long orderId,
            @RequestParam("amount") Double amount) {
        logger.info("Creating order with orderId: {} and amount: {}", orderId, amount);
        PayUTokenResponseDTO payUToken = payUService.getPayUToken();
        return ResponseEntity.ok(payUService.createOrder(payUToken.access_token(), amount, orderId));
    }
}
