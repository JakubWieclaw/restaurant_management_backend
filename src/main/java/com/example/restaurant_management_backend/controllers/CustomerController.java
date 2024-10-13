package com.example.restaurant_management_backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.restaurant_management_backend.jpa.model.Customer;
import com.example.restaurant_management_backend.services.CustomerUserDetailsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
@Validated
public class CustomerController {

    private final CustomerUserDetailsService customerService;

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);


    @Operation(summary = "Get all customers")
    @GetMapping("/all")
    public ResponseEntity<?> getAllCustomers() {
        final var customersList = customerService.getAllCustomers();
        logger.info("Getting all customers");
        return ResponseEntity.ok(customersList);
    }

    @Operation(summary = "Get customer by id")
    @ApiResponse(description = "Returns an customerwith a given id", content = {
        @Content(mediaType = "application/json", schema = @Schema(implementation = Customer.class)) })
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable Long id) {
        final var customer = customerService.getCustomerByIdOrThrowException(id);
        logger.info("Getting customer with id: {}", id);
        return ResponseEntity.ok(customer);
    }
}
