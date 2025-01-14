package com.example.restaurant_management_backend.services;

import com.example.restaurant_management_backend.jpa.model.command.ContactFormCommand;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class ContactFormServiceTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ContactFormService contactFormService;

    private ContactFormCommand contactFormCommand;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        contactFormCommand = new ContactFormCommand("John Doe", "john.doe@example.com", "Hello, this is a message.");
    }

    @Test
    void sendContactForm_ShouldCallEmailServiceMethods_WhenValidInput() throws MessagingException {
        // Arrange
        when(emailService.validateEmailDomain(contactFormCommand.getEmail())).thenReturn(true); // Mocking return value
        doNothing().when(emailService).sendContactFormEmail(contactFormCommand);  // Mocking void method
        doNothing().when(emailService).sentEmailConfirmingContactFormWasSent(
                contactFormCommand.getEmail(),
                contactFormCommand.getName(),
                contactFormCommand.getMessage()
        );  // Mocking void method

        // Act
        contactFormService.sendContactForm(contactFormCommand);

        // Assert
        verify(emailService, times(1)).validateEmailDomain(contactFormCommand.getEmail()); // Verifying method calls
        verify(emailService, times(1)).sendContactFormEmail(contactFormCommand);
        verify(emailService, times(1)).sentEmailConfirmingContactFormWasSent(
                contactFormCommand.getEmail(),
                contactFormCommand.getName(),
                contactFormCommand.getMessage()
        );
    }
}
