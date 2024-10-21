package com.example.restaurant_management_backend.services;

import com.example.restaurant_management_backend.exceptions.NotFoundException;
import com.example.restaurant_management_backend.jpa.model.Customer;
import com.example.restaurant_management_backend.jpa.model.Privilege;
import com.example.restaurant_management_backend.jpa.repositories.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerUserDetailsServiceTest {

    @InjectMocks
    private CustomerUserDetailsService customerUserDetailsService;

    @Mock
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test for loadUserByUsername method - valid case
    @Test
    void loadUserByUsername_whenCustomerExists_shouldReturnUserDetails() {
        // Given
        String email = "test@example.com";
        Customer customer = createMockCustomer(email);
        when(customerRepository.findByEmail(email)).thenReturn(Optional.of(customer));

        // When
        UserDetails userDetails = customerUserDetailsService.loadUserByUsername(email);

        // Then
        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        assertEquals(customer.getPasswordHash(), userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("USER_PRIVILEGE")));
    }

    // Test for loadUserByUsername method - user not found
    @Test
    void loadUserByUsername_whenCustomerDoesNotExist_shouldThrowUsernameNotFoundException() {
        // Given
        String email = "nonexistent@example.com";
        when(customerRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UsernameNotFoundException.class, () -> customerUserDetailsService.loadUserByUsername(email));
    }

    // Test for getCustomerById method - valid case
    @Test
    void getCustomerById_whenCustomerExists_shouldReturnCustomer() {
        // Given
        Long id = 1L;
        Customer customer = createMockCustomer("test@example.com");
        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));

        // When
        Optional<Customer> result = customerUserDetailsService.getCustomerById(id);

        // Then
        assertTrue(result.isPresent());
        assertEquals(customer, result.get());
    }

    // Test for getCustomerById method - customer not found
    @Test
    void getCustomerById_whenCustomerDoesNotExist_shouldReturnEmpty() {
        // Given
        Long id = 1L;
        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        // When
        Optional<Customer> result = customerUserDetailsService.getCustomerById(id);

        // Then
        assertTrue(result.isEmpty());
    }

    // Test for getCustomerByIdOrThrowException method - valid case
    @Test
    void getCustomerByIdOrThrowException_whenCustomerExists_shouldReturnCustomer() {
        // Given
        Long id = 1L;
        Customer customer = createMockCustomer("test@example.com");
        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));

        // When
        Customer result = customerUserDetailsService.getCustomerByIdOrThrowException(id);

        // Then
        assertEquals(customer, result);
    }

    // Test for getCustomerByIdOrThrowException method - customer not found
    @Test
    void getCustomerByIdOrThrowException_whenCustomerDoesNotExist_shouldThrowNotFoundException() {
        // Given
        Long id = 1L;
        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> customerUserDetailsService.getCustomerByIdOrThrowException(id));
    }

    // Test for getCustomerByEmailOrThrowException method - valid case
    @Test
    void getCustomerByEmailOrThrowException_whenCustomerExists_shouldReturnCustomer() {
        // Given
        String email = "test@example.com";
        Customer customer = createMockCustomer(email);
        when(customerRepository.findByEmail(email)).thenReturn(Optional.of(customer));

        // When
        Customer result = customerUserDetailsService.getCustomerByEmailOrThrowException(email);

        // Then
        assertEquals(customer, result);
    }

    // Test for getCustomerByEmailOrThrowException method - customer not found
    @Test
    void getCustomerByEmailOrThrowException_whenCustomerDoesNotExist_shouldThrowNotFoundException() {
        // Given
        String email = "nonexistent@example.com";
        when(customerRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> customerUserDetailsService.getCustomerByEmailOrThrowException(email));
    }

    // Test for getCustomerByResetToken method - valid case
    @Test
    void getCustomerByResetToken_whenCustomerExists_shouldReturnCustomer() {
        // Given
        String resetToken = "resetToken";
        Customer customer = createMockCustomer("test@example.com");
        when(customerRepository.findByResetToken(resetToken)).thenReturn(Optional.of(customer));

        // When
        Optional<Customer> result = customerUserDetailsService.getCustomerByResetToken(resetToken);

        // Then
        assertTrue(result.isPresent());
        assertEquals(customer, result.get());
    }

    // Test for getCustomerByResetToken method - token not found
    @Test
    void getCustomerByResetToken_whenTokenDoesNotExist_shouldReturnEmpty() {
        // Given
        String resetToken = "nonexistentToken";
        when(customerRepository.findByResetToken(resetToken)).thenReturn(Optional.empty());

        // When
        Optional<Customer> result = customerUserDetailsService.getCustomerByResetToken(resetToken);

        // Then
        assertTrue(result.isEmpty());
    }

    // Test for getCustomerByResetTokenOrThrowException method - valid case
    @Test
    void getCustomerByResetTokenOrThrowException_whenCustomerExists_shouldReturnCustomer() {
        // Given
        String resetToken = "resetToken";
        Customer customer = createMockCustomer("test@example.com");
        when(customerRepository.findByResetToken(resetToken)).thenReturn(Optional.of(customer));

        // When
        Customer result = customerUserDetailsService.getCustomerByResetTokenOrThrowException(resetToken);

        // Then
        assertEquals(customer, result);
    }

    // Test for getCustomerByResetTokenOrThrowException method - token not found
    @Test
    void getCustomerByResetTokenOrThrowException_whenTokenDoesNotExist_shouldThrowNotFoundException() {
        // Given
        String resetToken = "nonexistentToken";
        when(customerRepository.findByResetToken(resetToken)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> customerUserDetailsService.getCustomerByResetTokenOrThrowException(resetToken));
    }

    // Test for save method - saving a customer
    @Test
    void save_shouldSaveCustomer() {
        // Given
        Customer customer = createMockCustomer("test@example.com");
        when(customerRepository.save(customer)).thenReturn(customer);

        // When
        Customer result = customerUserDetailsService.save(customer);

        // Then
        assertEquals(customer, result);
        verify(customerRepository, times(1)).save(customer);
    }

    // Helper method to create a mock customer
    private Customer createMockCustomer(String email) {
        Privilege privilege = new Privilege("USER_PRIVILEGE");
        return Customer.builder()
                .id(1L)
                .name("Test")
                .surname("User")
                .email(email)
                .passwordHash("password")
                .privilege(privilege)
                .build();
    }
}
