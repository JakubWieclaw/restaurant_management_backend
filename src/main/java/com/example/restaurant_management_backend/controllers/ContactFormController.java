package com.example.restaurant_management_backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.restaurant_management_backend.jpa.model.command.ContactFormCommand;
import com.example.restaurant_management_backend.services.ContactFormService;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/contact-form")
@RequiredArgsConstructor
@Validated
public class ContactFormController {

    private final ContactFormService contactFormService;

    @Operation(summary = "Send contact email")
    @PostMapping("/send")
    public ResponseEntity<?> sendContactForm(@RequestBody ContactFormCommand contactFormCommand) {
        contactFormService.sendContactForm(contactFormCommand);
        return ResponseEntity.ok("Wiadomość została wysłana");
    }

}
