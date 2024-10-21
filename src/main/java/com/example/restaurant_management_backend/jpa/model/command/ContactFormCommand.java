package com.example.restaurant_management_backend.jpa.model.command;

import com.example.restaurant_management_backend.common.SelfValidating;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
public class ContactFormCommand extends SelfValidating<ContactFormCommand> {

    @NotBlank(message = "Imię nie może być puste")
    @Size(min = 2, max = 30, message = "Imię musi mieć od 2 do 30 znaków")
    private final String name;

    @NotBlank(message = "Email nie może być pusty")
    @Email(message = "Niepoprawny adres email")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Niepoprawny adres email — użyj poprawnego formatu")
    private final String email;

    @NotBlank(message = "Wiadomość nie może być pusta")
    @Size(min = 10, max = 500, message = "Wiadomość musi mieć od 10 do 500 znaków")
    private final String message;

    public ContactFormCommand(String name, String email, String message) {
        this.name = name;
        this.email = email;
        this.message = message;
        validateSelf();
    }

}
