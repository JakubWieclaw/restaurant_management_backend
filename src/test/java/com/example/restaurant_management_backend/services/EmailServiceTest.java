package com.example.restaurant_management_backend.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.MXRecord;
import org.xbill.DNS.Record;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @Mock
    private MimeMessageHelper helper;

    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        emailService = new EmailService(mailSender);
    }

    @Test
    void validateEmailDomain_validDomain() {
        // Arrange
        String email = "test@example.com";

        // Mock DNS lookup result
        Lookup mockLookup = mock(Lookup.class);
        when(mockLookup.run()).thenReturn(new Record[]{mock(MXRecord.class)});

        // Act
        boolean result = emailService.validateEmailDomain(email);

        // Assert
        assertTrue(result); // Should return true for valid domain
    }

    @Test
    void validateEmailDomain_invalidDomain() {
        // Arrange
        String email = "test@invalid.com";

        // Mock DNS lookup to return empty array (no MX records)
        Lookup mockLookup = mock(Lookup.class);
        when(mockLookup.run()).thenReturn(new Record[0]);

        // Act
        boolean result = emailService.validateEmailDomain(email);

        // Assert
        assertFalse(result); // Should return false for invalid domain
    }

    @Test
    void sendPasswordResetEmail_validEmail() throws MessagingException {
        // Arrange
        String to = "test@example.com";
        String resetLink = "https://example.com/reset?token=12345";

        // Mock MimeMessageHelper and MimeMessage
        MimeMessage mimeMessage = mock(MimeMessage.class);
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        emailService.sendPasswordResetEmail(to, resetLink);

        // Assert
        verify(mailSender).send(mimeMessage); // Verify that the email is sent
    }
}
