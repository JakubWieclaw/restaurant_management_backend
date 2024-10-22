package com.example.restaurant_management_backend.jpa.model.command;

<<<<<<< HEAD
import com.fasterxml.jackson.annotation.JsonIgnore;

=======
import com.example.restaurant_management_backend.common.SelfValidating;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
>>>>>>> main
import lombok.Getter;

@Getter
<<<<<<< HEAD
@Setter
public class RegisterCustomerCommand extends RegisterCommand {
    
    @JsonIgnore // Prevent 'admin' field from being serialized/deserialized
    private final boolean admin = false; // Fixed value for customers

    public RegisterCustomerCommand(String name, String surname, String email, String phone, String password) {
        super(name, surname, email, phone, password, false); // Fixed value for admin: false
=======
public class RegisterCustomerCommand extends SelfValidating<RegisterUserCommand> {

    @Size(min = 2, max = 50, message = "Imię musi mieć od 2 do 50 znaków")
    private final String name;

    @Size(min = 2, max = 50, message = "Nazwisko musi mieć od 2 do 50 znaków")
    private final String surname;

    @Email(message = "Niepoprawny adres email")
    private final String email;

    @Size(min = 9, max = 15, message = "Numer telefonu musi mieć od 9 do 15 znaków")
    private final String phone;

    @Size(min = 8, max = 50, message = "Hasło musi mieć od 8 do 50 znaków")
    private final String password;

    public RegisterCustomerCommand(String name, String surname, String email, String phone, String password) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.validateSelf();
>>>>>>> main
    }
}
