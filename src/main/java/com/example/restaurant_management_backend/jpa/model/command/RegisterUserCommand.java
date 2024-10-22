package com.example.restaurant_management_backend.jpa.model.command;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterUserCommand extends RegisterCommand {

    public RegisterUserCommand(String name, String surname, String email, String phone, String password, boolean admin) {
        super(name, surname, email, phone, password, admin);
    }
}
