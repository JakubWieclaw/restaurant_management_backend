package com.example.restaurant_management_backend.jpa.model.command;

import com.example.restaurant_management_backend.common.SelfValidating;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class LoginCommand extends SelfValidating<LoginCommand> {
    @NotNull(message = "Email nie może być pusty")
    private final String email;
    @NotNull(message = "Hasło nie może być puste")
    private final String password;

    public LoginCommand(String email, String password) {
        this.email = email;
        this.password = password;
        this.validateSelf();
    }
}
