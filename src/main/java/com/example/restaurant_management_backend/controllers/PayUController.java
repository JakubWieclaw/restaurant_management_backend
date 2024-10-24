package com.example.restaurant_management_backend.controllers;

import com.example.restaurant_management_backend.payU.PayUOrderResponse;
import com.example.restaurant_management_backend.payU.PayUService;
import com.example.restaurant_management_backend.payU.PayUTokenResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
        logger.info("Creating response with orderId: {} and amount: {}", orderId, amount);
        PayUTokenResponseDTO payUToken = payUService.getPayUToken();
        PayUOrderResponse response = payUService.createOrder(payUToken.access_token(), amount, orderId);
        return ResponseEntity.status(HttpStatus.SEE_OTHER).location(response.redirectUri()).build();
    }

    @Operation(summary = "Get order details")
    @GetMapping
    public ResponseEntity<String> getOrder(
            @RequestParam("orderId") Long orderId) {
        logger.info("Checking status of orderId {}", orderId);
        String payUOrderId = payUService.getPayUOrderId(orderId);
        return ResponseEntity.ok(payUService.getPayUOrderDetails(payUOrderId));
    }
}
