package com.example.restaurant_management_backend.jpa.model.command;

import lombok.Getter;

@Getter
<<<<<<< HEAD
@Setter
public class RegisterUserCommand extends RegisterCommand {
=======
public class RegisterUserCommand extends SelfValidating<RegisterUserCommand> {

    @NotBlank(message = "Imię nie może być puste")
    private final String name;

    @NotBlank(message = "Nazwisko nie może być puste")
    private final String surname;

    @NotBlank(message = "Email nie może być pusty")
    private final String email;

    @NotBlank(message = "Numer telefonu nie może być pusty")
    private final String phone;

    @NotBlank(message = "Hasło nie może być puste")
    private final String password;

    private final boolean admin;
>>>>>>> main

    public RegisterUserCommand(String name, String surname, String email, String phone, String password, boolean admin) {
        super(name, surname, email, phone, password, admin);
    }
}
