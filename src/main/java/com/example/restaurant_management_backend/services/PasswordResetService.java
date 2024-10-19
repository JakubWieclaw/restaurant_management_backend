package com.example.restaurant_management_backend.services;

import com.example.restaurant_management_backend.jpa.model.Customer;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PasswordResetService {

    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final CustomerUserDetailsService customerService;

    public void initiatePasswordReset(String email) {
        Customer customer = customerService.getCustomerByEmailOrThrowException(email);

        String token = UUID.randomUUID().toString();
        customer.setResetToken(token);
        customer.setResetTokenExpiry(LocalDateTime.now().plusMinutes(30));  // Token valid for 30 minutes
        Customer savedCustomer = customerService.save(customer);

        // Send the reset token to the user's email
        String resetLink = "http://localhost:5173/auth/password-reset?token=" + token;
        try {
            emailService.sendPasswordResetEmail(savedCustomer.getEmail(), resetLink);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void resetPassword(String token, String newPassword) {
        validateResetToken(token);
        Customer customer = customerService.getCustomerByResetTokenOrThrowException(token);

        // Reset password
        customer.setPasswordHash(passwordEncoder.encode(newPassword));
        customer.setResetToken(null);  // Invalidate the token after use
        customer.setResetTokenExpiry(null);
        customerService.save(customer);
    }

    public void validateResetToken(String token) {
        Customer customer = customerService.getCustomerByResetTokenOrThrowException(token);

        // Check if the token is expired
        if (customer.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new CredentialsExpiredException("Token resetujący stracił ważność.");
        }
    }
}

