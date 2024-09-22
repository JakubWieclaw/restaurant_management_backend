package com.example.restaurant_management_backend.services;

import com.example.restaurant_management_backend.dto.LoginResponseDTO;
import com.example.restaurant_management_backend.dto.RegisterResponseDTO;
import com.example.restaurant_management_backend.jpa.model.Customer;
import com.example.restaurant_management_backend.jpa.model.Privilege;
import com.example.restaurant_management_backend.jpa.model.command.RegisterUserCommand;
import com.example.restaurant_management_backend.jpa.repositories.CustomerRepository;
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

    private final AuthenticationManager authenticationManager;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public RegisterResponseDTO registerUser(RegisterUserCommand registerUserCommand) {
        if (customerRepository.findByEmail(registerUserCommand.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Użytkownik z tym adresem e-mail już istnieje");
        }

        Customer customer = createCustomerObject(registerUserCommand);
        Privilege privilege = new Privilege(registerUserCommand.isAdmin() ? "ADMIN_PRIVILEGE" : "USER_PRIVILEGE");
        customer.setPrivilege(privilege);
        customerRepository.save(customer);

        return new RegisterResponseDTO(customer.getId(), customer.getName(), customer.getSurname(), customer.getEmail(), customer.getPhone());
    }

    public LoginResponseDTO login(String email, String password) {
        Authentication authenticationRequest = new UsernamePasswordAuthenticationToken(email, password);
        Authentication authenticationResponse = this.authenticationManager.authenticate(authenticationRequest);
        SecurityContextHolder.getContext().setAuthentication(authenticationResponse);

        if (authenticationResponse.isAuthenticated()) {
            String token = jwtUtils.generateToken(authenticationResponse.getName());

            Customer customer = customerRepository.findByEmail(email)
                    .orElseThrow(() -> new BadCredentialsException("Nie znaleziono użytkownika"));

            boolean isAdmin = customer.getPrivilege().getPrivilegeName().equals("ADMIN_PRIVILEGE");

            return new LoginResponseDTO(
                    token,
                    customer.getId(),
                    customer.getName(),
                    customer.getSurname(),
                    customer.getEmail(),
                    isAdmin
            );
        }

        throw new BadCredentialsException("Logowanie nie powiodło się");
    }


    private Customer createCustomerObject(RegisterUserCommand registerUserCommand) {
        Customer customer = new Customer();
        customer.setName(registerUserCommand.getName());
        customer.setSurname(registerUserCommand.getSurname());
        customer.setEmail(registerUserCommand.getEmail());
        customer.setPhone(registerUserCommand.getPhone());
        customer.setPassword(passwordEncoder.encode(registerUserCommand.getPassword()));
        return customer;
    }
}
