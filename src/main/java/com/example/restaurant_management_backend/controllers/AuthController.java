package com.example.restaurant_management_backend.controllers;

import com.example.restaurant_management_backend.dto.LoginResponseDTO;
import com.example.restaurant_management_backend.dto.RegisterResponseDTO;
import com.example.restaurant_management_backend.jpa.model.command.LoginCommand;
import com.example.restaurant_management_backend.jpa.model.command.RegisterUserCommand;
import com.example.restaurant_management_backend.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

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
}
