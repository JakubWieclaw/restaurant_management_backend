package com.example.restaurant_management_backend.jpa.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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

    @JsonProperty
    private boolean isAdmin;

}
