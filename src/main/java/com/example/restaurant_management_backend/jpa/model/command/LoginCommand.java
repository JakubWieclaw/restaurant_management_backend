package com.example.restaurant_management_backend.jpa.model.command;

import com.example.restaurant_management_backend.common.SelfValidating;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class LoginCommand extends SelfValidating<LoginCommand> {

    @Schema(description = "Email", example = "kazimierz.odnowiciel@wp.pl")
    @Email(message = "To nie jest poprawny email")
    private final String email;

    @Schema(description = "Password", example = "!P0lsk@!")
    @NotNull(message = "Hasło nie może być puste")
    private final String password;

    public LoginCommand(String email, String password) {
        this.email = email;
        this.password = password;
        this.validateSelf();
    }
}
