package com.example.restaurant_management_backend.services;

import com.example.restaurant_management_backend.exceptions.NotFoundException;
import com.example.restaurant_management_backend.jpa.model.Customer;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PasswordResetServiceTest {

    @InjectMocks
    private PasswordResetService passwordResetService;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CustomerUserDetailsService customerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test for initiatePasswordReset method - valid case
    @Test
    void initiatePasswordReset_whenEmailExists_shouldSendResetLink() throws MessagingException {
        // Given
        String email = "test@example.com";
        Customer customer = createMockCustomer(email);
        when(customerService.getCustomerByEmailOrThrowException(email)).thenReturn(customer);
        when(customerService.save(any(Customer.class))).thenReturn(customer);

        // When
        passwordResetService.initiatePasswordReset(email);

        // Then
        verify(emailService, times(1)).sendPasswordResetEmail(eq(email), anyString());
        assertNotNull(customer.getResetToken());
        assertNotNull(customer.getResetTokenExpiry());
        verify(customerService, times(1)).save(customer);
    }

    // Test for initiatePasswordReset method - email not found
    @Test
    void initiatePasswordReset_whenEmailDoesNotExist_shouldThrowNotFoundException() throws MessagingException {
        // Given
        String email = "nonexistent@example.com";
        when(customerService.getCustomerByEmailOrThrowException(email)).thenThrow(new NotFoundException("Nie znaleziono klienta o adresie e-mail " + email));

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class, () -> passwordResetService.initiatePasswordReset(email));
        assertEquals("Nie znaleziono klienta o adresie e-mail " + email, exception.getMessage());
        verify(emailService, never()).sendPasswordResetEmail(anyString(), anyString());
    }

    // Test for resetPassword method - valid token and password
    @Test
    void resetPassword_whenTokenIsValid_shouldUpdatePassword() {
        // Given
        String token = UUID.randomUUID().toString();
        String newPassword = "newPassword123";
        Customer customer = createMockCustomer("test@example.com");
        customer.setResetToken(token);
        customer.setResetTokenExpiry(LocalDateTime.now().plusMinutes(10)); // Valid token

        when(customerService.getCustomerByResetTokenOrThrowException(token)).thenReturn(customer);
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedPassword");

        // When
        passwordResetService.resetPassword(token, newPassword);

        // Then
        verify(passwordEncoder, times(1)).encode(newPassword);
        assertEquals("encodedPassword", customer.getPassword());
        assertNull(customer.getResetToken());
        assertNull(customer.getResetTokenExpiry());
        verify(customerService, times(1)).save(customer);
    }

    // Test for resetPassword method - expired token
    @Test
    void resetPassword_whenTokenIsExpired_shouldThrowCredentialsExpiredException() {
        // Given
        String token = UUID.randomUUID().toString();
        String newPassword = "newPassword123";
        Customer customer = createMockCustomer("test@example.com");
        customer.setResetToken(token);
        customer.setResetTokenExpiry(LocalDateTime.now().minusMinutes(10)); // Expired token

        when(customerService.getCustomerByResetTokenOrThrowException(token)).thenReturn(customer);

        // When & Then
        CredentialsExpiredException exception = assertThrows(CredentialsExpiredException.class, () -> passwordResetService.resetPassword(token, newPassword));
        assertEquals("Token resetujący stracił ważność.", exception.getMessage());
        verify(passwordEncoder, never()).encode(anyString());
        verify(customerService, never()).save(customer);
    }

    // Test for validateResetToken method - valid token
    @Test
    void validateResetToken_whenTokenIsValid_shouldNotThrowException() {
        // Given
        String token = UUID.randomUUID().toString();
        Customer customer = createMockCustomer("test@example.com");
        customer.setResetToken(token);
        customer.setResetTokenExpiry(LocalDateTime.now().plusMinutes(10)); // Valid token

        when(customerService.getCustomerByResetTokenOrThrowException(token)).thenReturn(customer);

        // When
        assertDoesNotThrow(() -> passwordResetService.validateResetToken(token));

        // Then
        verify(customerService, times(1)).getCustomerByResetTokenOrThrowException(token);
    }

    // Test for validateResetToken method - expired token
    @Test
    void validateResetToken_whenTokenIsExpired_shouldThrowCredentialsExpiredException() {
        // Given
        String token = UUID.randomUUID().toString();
        Customer customer = createMockCustomer("test@example.com");
        customer.setResetToken(token);
        customer.setResetTokenExpiry(LocalDateTime.now().minusMinutes(10)); // Expired token

        when(customerService.getCustomerByResetTokenOrThrowException(token)).thenReturn(customer);

        // When & Then
        CredentialsExpiredException exception = assertThrows(CredentialsExpiredException.class, () -> passwordResetService.validateResetToken(token));
        assertEquals("Token resetujący stracił ważność.", exception.getMessage());
        verify(customerService, times(1)).getCustomerByResetTokenOrThrowException(token);
    }

    // Helper method to create a mock Customer
    private Customer createMockCustomer(String email) {
        return Customer.builder()
                .id(1L)
                .name("John")
                .surname("Doe")
                .email(email)
                .password("oldPassword")
                .resetToken(null)
                .resetTokenExpiry(null)
                .build();
    }
}
