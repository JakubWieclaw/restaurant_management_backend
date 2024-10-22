package com.example.restaurant_management_backend.jpa.model.command;

import com.example.restaurant_management_backend.common.SelfValidating;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class RegisterCommand extends SelfValidating<RegisterCommand> {
    @NotBlank(message = "Imię nie może być puste")
    @Size(min = 2, max = 50, message = "Imię musi mieć od 2 do 50 znaków")
    private String name;

    @NotBlank(message = "Nazwisko nie może być puste")
    @Size(min = 2, max = 50, message = "Nazwisko musi mieć od 2 do 50 znaków")
    private String surname;

    @NotBlank(message = "Email nie może być pusty")
    @Email(message = "Niepoprawny adres email")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", 
             message = "Niepoprawny adres email — użyj poprawnego formatu")
    private String email;

    @NotBlank(message = "Numer telefonu nie może być pusty")
    @Size(min = 9, max = 15, message = "Numer telefonu musi mieć od 9 do 15 znaków")
    private String phone;

    @NotBlank(message = "Hasło nie może być puste")
    @Size(min = 8, max = 50, message = "Hasło musi mieć od 8 do 50 znaków")
    private String password;

    private boolean admin;

    public RegisterCommand(String name, String surname, String email, String phone, String password, boolean admin) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.admin = admin;
        this.validateSelf();
    }
}
