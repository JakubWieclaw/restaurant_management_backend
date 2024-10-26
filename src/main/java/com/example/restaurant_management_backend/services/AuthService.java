package com.example.restaurant_management_backend.services;

import com.example.restaurant_management_backend.dto.LoginResponseDTO;
import com.example.restaurant_management_backend.dto.RegisterResponseDTO;
import com.example.restaurant_management_backend.exceptions.ResourceConflictException;
import com.example.restaurant_management_backend.jpa.model.Customer;
import com.example.restaurant_management_backend.jpa.model.Privilege;
import com.example.restaurant_management_backend.jpa.model.command.RegisterCommand;
import com.example.restaurant_management_backend.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    public static final String USER_WITH_THIS_EMAIL_EXISTS = "Użytkownik o podanym adresie email już istnieje";
    public static final String WRONG_CREDENTIALS = "Złe dane logowania";
    private final AuthenticationManager authenticationManager;
    private final CustomerUserDetailsService customerService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public RegisterResponseDTO registerUser(RegisterCommand registerCommand) {
        // if password is not provided, throw exception
        if (registerCommand.getPassword() == null) {
            throw new IllegalArgumentException("Hasło nie może być puste");
        }

        if (customerService.getCustomerByEmail(registerCommand.getEmail()).isPresent()) {
            throw new ResourceConflictException(USER_WITH_THIS_EMAIL_EXISTS);
        }

        Customer customer = createCustomerObject(registerCommand);
        Privilege privilege = new Privilege(registerCommand.isAdmin() ? "ADMIN_PRIVILEGE" : "USER_PRIVILEGE");
        customer.setPrivilege(privilege);
        Customer savedCustomer = customerService.save(customer);

        return buildRegisterResponse(savedCustomer);
    }

    public LoginResponseDTO login(String email, String password) {
        Authentication authenticationRequest = new UsernamePasswordAuthenticationToken(email, password);
        Authentication authenticationResponse = this.authenticationManager.authenticate(authenticationRequest);
        SecurityContextHolder.getContext().setAuthentication(authenticationResponse);

        if (authenticationResponse.isAuthenticated()) {
            String token = jwtUtils.generateToken(authenticationResponse.getName());
            Customer customer = customerService.getCustomerByEmailOrThrowException(email);
            boolean isAdmin = customer.getPrivilege().getPrivilegeName().equals("ADMIN_PRIVILEGE");

            return buildLoginResponse(customer, token, isAdmin);
        } else {
            throw new BadCredentialsException(WRONG_CREDENTIALS);
        }
    }

    private Customer createCustomerObject(RegisterCommand registerCommand) {
        return Customer.builder()
                .name(registerCommand.getName())
                .surname(registerCommand.getSurname())
                .email(registerCommand.getEmail())
                .phone(registerCommand.getPhone())
                .passwordHash(passwordEncoder.encode(registerCommand.getPassword()))
                .build();
    }

    private RegisterResponseDTO buildRegisterResponse(Customer customer) {
        return new RegisterResponseDTO(
                customer.getId(),
                customer.getName(),
                customer.getSurname(),
                customer.getEmail(),
                customer.getPhone());
    }

    private LoginResponseDTO buildLoginResponse(Customer customer, String token, boolean isAdmin) {
        return new LoginResponseDTO(
                token,
                customer.getId(),
                customer.getName(),
                customer.getSurname(),
                customer.getEmail(),
                isAdmin);
    }
}
