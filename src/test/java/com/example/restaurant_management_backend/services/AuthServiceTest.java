package com.example.restaurant_management_backend.services;

import com.example.restaurant_management_backend.dto.LoginResponseDTO;
import com.example.restaurant_management_backend.dto.RegisterResponseDTO;
import com.example.restaurant_management_backend.exceptions.ResourceConflictException;
import com.example.restaurant_management_backend.jpa.model.Customer;
import com.example.restaurant_management_backend.jpa.model.Privilege;
import com.example.restaurant_management_backend.jpa.model.command.RegisterUserCommand;
import com.example.restaurant_management_backend.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private CustomerUserDetailsService customerService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void registerUser_whenEmailExists_shouldThrowResourceConflictException() {
        // Given
        RegisterUserCommand command = new RegisterUserCommand("John", "Doe", "john.doe@example.com", "123456789", "555-1234", false);
        when(customerService.getCustomerByEmail(command.getEmail())).thenReturn(Optional.of(new Customer()));

        // When / Then
        assertThrows(ResourceConflictException.class, () -> authService.registerUser(command));
        verify(customerService, times(1)).getCustomerByEmail(command.getEmail());
    }

    @Test
    public void registerUser_whenEmailDoesNotExist_shouldReturnRegisterResponseDTO() {
        // Given
        RegisterUserCommand command = new RegisterUserCommand("John", "Doe", "john.doe@example.com", "123456789", "555-1234", false);
        when(customerService.getCustomerByEmail(command.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(command.getPassword())).thenReturn("encodedPassword");
        Customer savedCustomer = Customer.builder()
                .id(1L)
                .name(command.getName())
                .surname(command.getSurname())
                .email(command.getEmail())
                .phone(command.getPhone())
                .passwordHash("encodedPassword")
                .privilege(new Privilege("USER_PRIVILEGE"))
                .build();
        when(customerService.save(any(Customer.class))).thenReturn(savedCustomer);

        // When
        RegisterResponseDTO response = authService.registerUser(command);

        // Then
        assertNotNull(response);
        assertEquals("John", response.name());
        assertEquals("Doe", response.surname());
        assertEquals("john.doe@example.com", response.email());
        verify(customerService, times(1)).getCustomerByEmail(command.getEmail());
        verify(customerService, times(1)).save(any(Customer.class));
    }

    @Test
    public void login_whenCredentialsAreValid_shouldReturnLoginResponseDTO() {
        // Given
        String email = "john.doe@example.com";
        String password = "123456";
        Authentication authentication = mock(Authentication.class);
        Customer customer = Customer.builder()
                .id(1L)
                .name("John")
                .surname("Doe")
                .email(email)
                .privilege(new Privilege("USER_PRIVILEGE"))
                .build();

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(email);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtUtils.generateToken(anyString())).thenReturn("jwtToken");
        when(customerService.getCustomerByEmailOrThrowException(email)).thenReturn(customer);

        // When
        LoginResponseDTO response = authService.login(email, password);

        // Then
        assertNotNull(response);
        assertEquals("jwtToken", response.token());
        assertEquals(email, response.customerEmail());
        assertFalse(response.isAdmin());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils, times(1)).generateToken(anyString());
    }

    @Test
    public void login_whenCredentialsAreInvalid_shouldThrowBadCredentialsException() {
        // Given
        String email = "john.doe@example.com";
        String password = "wrongPassword";
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // When / Then
        assertThrows(BadCredentialsException.class, () -> authService.login(email, password));
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}
