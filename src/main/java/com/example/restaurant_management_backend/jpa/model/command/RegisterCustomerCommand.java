package com.example.restaurant_management_backend.jpa.model.command;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterCustomerCommand extends RegisterCommand {
    
    @JsonIgnore // Prevent 'admin' field from being serialized/deserialized
    private final boolean admin = false; // Fixed value for customers

    public RegisterCustomerCommand(String name, String surname, String email, String phone, String password) {
        super(name, surname, email, phone, password); // Fixed value for admin: false
    }
}
