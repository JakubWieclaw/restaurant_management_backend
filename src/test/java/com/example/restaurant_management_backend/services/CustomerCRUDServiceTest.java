package com.example.restaurant_management_backend.services;

import com.example.restaurant_management_backend.exceptions.NotFoundException;
import com.example.restaurant_management_backend.jpa.model.Customer;
import com.example.restaurant_management_backend.jpa.model.command.RegisterCustomerCommand;
import com.example.restaurant_management_backend.jpa.repositories.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerCRUDServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService mockEmailService;

    @Mock
    private CustomerUserDetailsService customerService;

    @InjectMocks
    private CustomerCRUDService customerCRUDService;

    private Customer existingCustomer;

    @BeforeEach
    void setUp() {
        existingCustomer = Customer.builder()
                .id(1L)
                .name("John")
                .surname("Doe")
                .email("john.doe@example.com")
                .phone("123456789")
                .passwordHash("encodedPassword")
                .build();
    }

    @Test
    void shouldDeleteCustomerByIdWhenExists() {
        // Arrange
        when(customerRepository.existsById(1L)).thenReturn(true);

        // Act
        customerCRUDService.deleteCustomerById(1L);

        // Assert
        verify(customerRepository, times(1)).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentCustomer() {
        // Arrange
        when(customerRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> customerCRUDService.deleteCustomerById(1L));
        assertEquals("Nie znaleziono klienta o id 1", exception.getMessage());
        verify(customerRepository, never()).deleteById(1L);
    }

    @Test
    void shouldUpdateCustomerSuccessfully() {

        when(mockEmailService.validateEmailDomain(anyString())).thenReturn(true);
        // Arrange
        RegisterCustomerCommand registerCustomerCommand = new RegisterCustomerCommand("Jane", "Doe",
                "testemail@testingtesttest.com", "987654321", "newPassword");
        when(passwordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");

        // Mock the save method to return the updated customer
        when(customerRepository.save(existingCustomer)).thenReturn(existingCustomer);
        when(customerService.getCustomerByIdOrThrowException(1L)).thenReturn(existingCustomer);

        // Act
        Customer updatedCustomer = customerCRUDService.updateCustomer(1L, registerCustomerCommand);

        // Assert
        assertNotNull(updatedCustomer, "The updated customer should not be null");
        assertEquals("Jane", updatedCustomer.getName());
        assertEquals("Doe", updatedCustomer.getSurname());
        assertEquals("testemail@testingtesttest.com", updatedCustomer.getEmail());
        assertEquals("987654321", updatedCustomer.getPhone());
        assertEquals("newEncodedPassword", updatedCustomer.getPasswordHash());
        verify(customerRepository, times(1)).save(existingCustomer);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentCustomer() {
        // Arrange
        RegisterCustomerCommand registerUserCommand = new RegisterCustomerCommand("Jane", "Doe", "jane.doe@example.com",
                "987654321", "newPassword");
        when(customerService.getCustomerByIdOrThrowException(1L)).thenThrow(new NotFoundException("Nie znaleziono klienta o id 1"));

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> customerCRUDService.updateCustomer(1L, registerUserCommand));
        assertEquals("Nie znaleziono klienta o id 1", exception.getMessage());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    // THESE SHOULD GO TO EMAIL SERVICE TESTS

    // @Test
    // void shouldValidateEmailSuccessfully() {
    // String email = "test@example.com";
    // assertDoesNotThrow(() -> customerCRUDService.validateEmail(email));
    // }

    // @Test
    // void shouldFailEmailValidationForInvalidDomain() {
    // String invalidEmail = "test@invalid_domain";
    // assertThrows(IllegalArgumentException.class, () ->
    // mockEmailService.validateEmailDomain(invalidEmail));
    // }
}
