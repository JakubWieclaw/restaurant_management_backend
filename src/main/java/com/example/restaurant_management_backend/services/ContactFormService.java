package com.example.restaurant_management_backend.services;

import org.springframework.stereotype.Service;

import com.example.restaurant_management_backend.jpa.model.command.ContactFormCommand;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContactFormService {

    private final CustomerCRUDService customerCRUDService;

    private final EmailService emailService;

    public void sendContactForm(ContactFormCommand contactFormCommand) {
        customerCRUDService.validateEmail(contactFormCommand.getEmail());
        try {
            emailService.sendContactFormEmail(contactFormCommand);
            emailService.sentEmailConfirmingContactFormWasSent(contactFormCommand.getEmail(), contactFormCommand.getName(), contactFormCommand.getMessage());
        } catch (Exception e) {
            throw new IllegalArgumentException("Nie udało się wysłać wiadomości");
        }
    }

}
