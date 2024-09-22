package com.example.restaurant_management_backend.jpa.model.command;

import com.example.restaurant_management_backend.common.SelfValidating;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterUserCommand extends SelfValidating<RegisterUserCommand> {
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

    public RegisterUserCommand(String name, String surname, String email, String phone, String password, boolean admin) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.admin = admin;
        this.validateSelf();
    }
}
