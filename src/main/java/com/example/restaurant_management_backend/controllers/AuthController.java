package com.example.restaurant_management_backend.controllers;

import com.example.restaurant_management_backend.dto.LoginResponseDTO;
import com.example.restaurant_management_backend.dto.RegisterResponseDTO;
import com.example.restaurant_management_backend.jpa.model.command.LoginCommand;
import com.example.restaurant_management_backend.jpa.model.command.RegisterUserCommand;
import com.example.restaurant_management_backend.services.AuthService;
import com.example.restaurant_management_backend.services.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<RegisterResponseDTO> registerUser(@Valid @RequestBody RegisterUserCommand registerUserCommand) {
        RegisterResponseDTO response = authService.registerUser(registerUserCommand);
        logger.info("User registered with email: {}", registerUserCommand.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Log in")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginCommand loginCommand) {
        LoginResponseDTO response = authService.login(loginCommand.getEmail(), loginCommand.getPassword());
        logger.info("User logged in with email: {}", loginCommand.getEmail());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Initiate password reset process")
    public ResponseEntity<Void> forgotPassword(@RequestParam("email") String email) {
        passwordResetService.initiatePasswordReset(email);
        logger.info("Password reset initiated for email: {}", email);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/password-reset")
    @Operation(summary = "Validate password reset token")
    public ResponseEntity<String> resetPasswordForm(@RequestParam("token") String token) {
        passwordResetService.validateResetToken(token);
        logger.info("Password reset token validated");
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/password-reset")
    @Operation(summary = "Reset password")
    public ResponseEntity<Void> resetPassword(@RequestParam("token") String token,
                                              @RequestParam("newPassword") String newPassword) {
        passwordResetService.resetPassword(token, newPassword);
        URI uri = UriComponentsBuilder
                .fromHttpUrl("http://localhost:5173/auth")
                .build()
                .toUri();
        logger.info("Password reset for token: {}", token);
        return ResponseEntity.status(HttpStatus.SEE_OTHER).location(uri).build();
    }
}
