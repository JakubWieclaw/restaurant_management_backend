package com.example.restaurant_management_backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.restaurant_management_backend.jpa.model.Customer;
import com.example.restaurant_management_backend.jpa.model.command.RegisterCustomerCommand;
import com.example.restaurant_management_backend.jpa.model.command.RegisterUserCommand;
import com.example.restaurant_management_backend.services.AuthService;
import com.example.restaurant_management_backend.services.CustomerCRUDService;
import com.example.restaurant_management_backend.services.CustomerUserDetailsService;
import com.example.restaurant_management_backend.services.EmailService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
@Validated
public class CustomerController {

    private final CustomerUserDetailsService customerService;
    private final CustomerCRUDService customerCRUDService;
    private final AuthService authService;
    private final EmailService emailService;

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

    @Operation(summary = "Delete customer by id")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCustomerById(@PathVariable Long id) {
        customerCRUDService.deleteCustomerById(id);
        logger.info("Deleting customer with id: {}", id);
        return ResponseEntity.ok("Customer deleted successfully");
    }

    @Operation(summary = "Register new customer (non admin)")
    @PostMapping("/add")
    public ResponseEntity<?> registerCustomer(@RequestBody RegisterCustomerCommand registerCustomerCommand) {
        customerCRUDService.validateEmail(registerCustomerCommand.getEmail());
        final var registerUserCommand = new RegisterUserCommand(registerCustomerCommand.getName(),
                registerCustomerCommand.getSurname(), registerCustomerCommand.getEmail(),
                registerCustomerCommand.getPhone(), registerCustomerCommand.getPassword(), false);
        final var customer = authService.registerUser(registerUserCommand);
        try {
            emailService.sendRegistrationConfirmationEmail(registerUserCommand.getEmail(),
                    registerUserCommand.getName());
        } catch (MessagingException e) {
            // Return response saying that account was created but email was not sent
            return ResponseEntity.ok(customer + "\nUtworzono konto, ale nie udało się wysłać emaila potwierdzającego");
        }
        ;
        logger.info("Adding new customer");
        return ResponseEntity.ok(customer);
    }

    @Operation(summary = "Update customer (non admin)")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable Long id,
            @RequestBody RegisterCustomerCommand registerCustomerCommand) {
        final var registerUserCommand = new RegisterUserCommand(registerCustomerCommand.getName(),
                registerCustomerCommand.getSurname(), registerCustomerCommand.getEmail(),
                registerCustomerCommand.getPhone(), registerCustomerCommand.getPassword(), false);
        final var customer = customerCRUDService.updateCustomer(id, registerUserCommand);
        logger.info("Updating customer with id: {}", id);
        return ResponseEntity.ok(customer);
    }
}
