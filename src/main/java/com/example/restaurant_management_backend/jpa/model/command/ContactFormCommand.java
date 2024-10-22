package com.example.restaurant_management_backend.jpa.model.command;

import com.example.restaurant_management_backend.common.SelfValidating;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ContactFormCommand extends SelfValidating<ContactFormCommand> {

    @Schema(description = "Sender's name", example = "Brajanek")
    @Size(min = 2, max = 30, message = "Imię musi mieć od 2 do 30 znaków")
    private final String name;

    @Schema(description = "E-mail of the sender", example = "kazimierz.odnowiciek@wp.pl")
    @Email(message = "Niepoprawny adres email")
    private final String email;

    @Schema(description = "Body of the e-mail message", example = "Kiedyś to było, nie to co teraz...")
    @Size(min = 10, max = 500, message = "Wiadomość musi mieć od 10 do 500 znaków")
    private final String message;

    public ContactFormCommand(String name, String email, String message) {
        this.name = name;
        this.email = email;
        this.message = message;
        validateSelf();
    }

}
