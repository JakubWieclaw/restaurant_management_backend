package com.example.restaurant_management_backend.services;

import com.example.restaurant_management_backend.dto.LoginResponseDTO;
import com.example.restaurant_management_backend.dto.RegisterResponseDTO;
import com.example.restaurant_management_backend.exceptions.ResourceConflictException;
import com.example.restaurant_management_backend.jpa.model.Customer;
import com.example.restaurant_management_backend.jpa.model.Privilege;
import com.example.restaurant_management_backend.jpa.model.UserToken;
import com.example.restaurant_management_backend.jpa.model.command.RegisterCommand;
import com.example.restaurant_management_backend.security.JwtUtils;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

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
    private final UserTokenService tokenService;

    public RegisterResponseDTO registerUser(RegisterCommand registerCommand) {
        // if password is not provided, throw exception
        if (registerCommand.getPassword() == null) {
            throw new IllegalArgumentException("Hasło nie może być puste");
        }

        if (customerService.getCustomerByEmail(registerCommand.getEmail()).isPresent()) {
            throw new ResourceConflictException(USER_WITH_THIS_EMAIL_EXISTS);
        }

        Customer customer = createCustomerObject(registerCommand);
        Privilege privilege = new Privilege(customerService.countAll() == 0 ? "ADMIN_PRIVILEGE" : "USER_PRIVILEGE");
        customer.setPrivilege(privilege);
        Customer savedCustomer = customerService.save(customer);

        return buildRegisterResponse(savedCustomer);
    }

    public LoginResponseDTO login(String email, String password) {
        Authentication authenticationRequest = new UsernamePasswordAuthenticationToken(email, password);
        Authentication authenticationResponse = this.authenticationManager.authenticate(authenticationRequest);
        SecurityContextHolder.getContext().setAuthentication(authenticationResponse);

        if (authenticationResponse.isAuthenticated()) {
            UUID uuid1 = UUID.randomUUID();
            UUID uuid2 = UUID.randomUUID();
            // String uuidString = uuid1.toString() + uuid2.toString();
            String tokenString = uuid1.toString() + uuid2.toString();
            
            Customer customer = customerService.getCustomerByEmailOrThrowException(email);
            UserToken token = new UserToken();
            // TODO: set tokenHash aand salt in token
            String tokenHash = passwordEncoder.encode(tokenString);
            token.setTokenHash(tokenHash);
            token.setCreationDate(java.time.LocalDateTime.now());
            token.setExpiryDate(java.time.LocalDateTime.now().plusHours(12)); // Token valid for 12 hours
            token.setCustomer(customer);
            tokenService.addToken(token);
            // boolean isAdmin = customer.getPrivilege().getPrivilegeName().equals("ADMIN_PRIVILEGE");

            return buildLoginResponse(tokenString, token.getExpiryDate(), token.getCreationDate(), customer.getId());
        } else {
            throw new BadCredentialsException(WRONG_CREDENTIALS);
        }
    }

    private String generateSalt() {
        return UUID.randomUUID().toString();
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

    private LoginResponseDTO buildLoginResponse(String tokenString, LocalDateTime expiryDate, LocalDateTime creationDate, Long customerId) {
        return new LoginResponseDTO(
                tokenString,
                expiryDate,
                creationDate,
                customerId
        );
    }
}
