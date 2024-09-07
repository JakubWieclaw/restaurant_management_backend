package com.example.restaurant_management_backend.controllers;

import com.example.restaurant_management_backend.jpa.model.Customer;
import com.example.restaurant_management_backend.jpa.model.Privilege;
import com.example.restaurant_management_backend.jpa.model.dto.RegisterRequest;
import com.example.restaurant_management_backend.jpa.repositories.CustomerRepository;
import com.example.restaurant_management_backend.security.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthController(AuthenticationManager authenticationManager, CustomerRepository customerRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        // Check if the email already exists
        if (customerRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Użytkownik z tym adresem e-mail już istnieje");
        }

        // Create a new Customer object
        Customer customer = new Customer();
        customer.setName(registerRequest.getName());
        customer.setSurname(registerRequest.getSurname());
        customer.setEmail(registerRequest.getEmail());
        customer.setPhone(registerRequest.getPhone());
        customer.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        // Assign privileges (ADMIN_PRIVILEGE or USER_PRIVILEGE)
        Privilege privilege = new Privilege(registerRequest.isAdmin() ? "ADMIN_PRIVILEGE" : "USER_PRIVILEGE");
        customer.setPrivilege(privilege);

        // Save the new user to the repository
        customerRepository.save(customer);

        return ResponseEntity.ok("Użytkownik został pomyślnie zarejestrowany");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authenticationRequest =
                    new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password());
            Authentication authenticationResponse =
                    this.authenticationManager.authenticate(authenticationRequest);
            SecurityContextHolder.getContext().setAuthentication(authenticationResponse);
            if (authenticationResponse.isAuthenticated()) {
                String token = jwtUtils.generateToken(authenticationResponse.getName());

                Customer customer = customerRepository.findByEmail(loginRequest.email())
                        .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika"));

                boolean isAdmin = customer.getPrivilege().getPrivilegeName().equals("ADMIN_PRIVILEGE");

                return ResponseEntity.ok(Map.of(
                        "token", token,
                        "isAdmin", isAdmin
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nieprawidłowa nazwa użytkownika lub hasło");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Logowanie nie powiodło się");
    }

    public record LoginRequest(String email, String password) {
    }
}
