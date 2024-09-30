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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<RegisterResponseDTO> registerUser(@Valid @RequestBody RegisterUserCommand registerUserCommand) {
        RegisterResponseDTO response = authService.registerUser(registerUserCommand);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Log in")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginCommand loginCommand) {
        LoginResponseDTO response = authService.login(loginCommand.getEmail(), loginCommand.getPassword());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Initiate password reset process")
    public ResponseEntity<Void> forgotPassword(@RequestParam("email") String email) {
        passwordResetService.initiatePasswordReset(email);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/password-reset")
    @Operation(summary = "Validate password reset token")
    public ResponseEntity<String> resetPasswordForm(@RequestParam("token") String token) {
        passwordResetService.validateResetToken(token);
        return ResponseEntity.ok("Token jest ważny. Postępuj zgodnie z instrukcjami na stronie.");
    }

    @PostMapping("/password-reset")
    @Operation(summary = "Reset password")
    public ResponseEntity<Void> resetPassword(@RequestParam("token") String token,
                                              @RequestParam("newPassword") String newPassword) {
        passwordResetService.resetPassword(token, newPassword);
        return ResponseEntity.noContent().build();
    }
}
