package com.example.restaurant_management_backend.jpa.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "Imię nie może być puste")
    private String name;

    @NotBlank(message = "Nazwisko nie może być puste")
    private String surname;

    @NotBlank(message = "Email nie może być pusty")
    private String email;

    @NotBlank(message = "Numer telefonu nie może być pusty")
    private String phone;

    @NotBlank(message = "Hasło nie może być puste")
    private String password;

    private boolean admin;

}
