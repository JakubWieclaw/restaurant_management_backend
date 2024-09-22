package com.example.restaurant_management_backend.controllers;

import com.example.restaurant_management_backend.dto.LoginResponseDTO;
import com.example.restaurant_management_backend.dto.RegisterResponseDTO;
import com.example.restaurant_management_backend.jpa.model.command.LoginCommand;
import com.example.restaurant_management_backend.jpa.model.command.RegisterUserCommand;
import com.example.restaurant_management_backend.services.AuthService;
import com.example.restaurant_management_backend.services.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private AuthService authService;
    private PasswordResetService passwordResetService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<RegisterResponseDTO> registerUser(@Valid @RequestBody RegisterUserCommand registerUserCommand) {
        RegisterResponseDTO response = authService.registerUser(registerUserCommand);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Log in")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginCommand loginCommand) {
        LoginResponseDTO response = authService.login(loginCommand.getEmail(), loginCommand.getPassword());
        return ResponseEntity.ok(response);
    }

    // Forgot Password: Handle forgot password request to initiate the process
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam("email") String email) {
        passwordResetService.initiatePasswordReset(email);
        return ResponseEntity.ok("A password reset link has been sent to your email.");
    }

    @GetMapping("/reset")
    public ResponseEntity<String> resetPasswordForm(@RequestParam("token") String token) {
        passwordResetService.validateResetToken(token);
        return ResponseEntity.ok("Valid token. Proceed to reset password.");
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@RequestParam("token") String token,
                                                @RequestParam("newPassword") String newPassword) {
        passwordResetService.resetPassword(token, newPassword);
        return ResponseEntity.ok("Password reset successful.");
    }
}
