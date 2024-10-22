package com.example.restaurant_management_backend.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.example.restaurant_management_backend.jpa.model.command.ContactFormCommand;

@Service
@AllArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendPasswordResetEmail(String to, String resetLink) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        String htmlContent = "<div style=\"font-family: Arial, sans-serif; font-size: 16px; color: #333;\">" +
                "<h2 style=\"color: #0056b3;\">Prośba o zresetowanie hasła</h2>" +
                "<p>Witaj,</p>" +
                "<p>Otrzymaliśmy prośbę o zresetowanie Twojego hasła. Kliknij poniższy przycisk, aby je zresetować:</p>"
                +
                "<a href=\"" + resetLink
                + "\" style=\"display: inline-block; padding: 10px 20px; color: white; background-color: #0056b3; text-decoration: none; border-radius: 5px;\">Zresetuj hasło</a>"
                +
                "<p>Jeśli nie wysłałeś tej prośby, możesz zignorować tę wiadomość.</p>" +
                "<p>Dziękujemy, <br>Zespół zarządzania restauracją</p>" +
                "</div>";

        helper.setTo(to);
        helper.setSubject("Prośba o zresetowanie hasła");
        helper.setText(htmlContent, true); // Set the second parameter to true for HTML

        mailSender.send(mimeMessage);
    }

    public void sendRegistrationConfirmationEmail(String to, String customerName) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        String htmlContent = "<div style=\"font-family: Arial, sans-serif; font-size: 16px; color: #333; background-color: #f4f4f4; padding: 20px;\">"
                +
                "<div style=\"max-width: 600px; margin: auto; background-color: #ffffff; border: 1px solid #ddd; border-radius: 5px; padding: 20px;\">"
                +
                "<h2 style=\"color: #0056b3; text-align: center;\">Potwierdzenie rejestracji</h2>" +
                "<p style=\"font-size: 18px;\">Witaj, <strong>" + customerName + "</strong></p>" +
                "<p>Twoje konto zostało pomyślnie zarejestrowane w naszym systemie.</p>" +
                "<p>Możesz teraz zalogować się na swoje konto i korzystać z naszych usług.</p>" +
                "<p style=\"margin-top: 30px;\">Dziękujemy,<br><strong>Zespół zarządzania restauracją</strong></p>" +
                "</div></div>";

        helper.setTo(to);
        helper.setSubject("Potwierdzenie rejestracji");
        helper.setText(htmlContent, true); // Set the second parameter to true for HTML

        mailSender.send(mimeMessage);
    }

    public void sendContactFormEmail(ContactFormCommand contactFormCommand) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        String htmlContent = "<div style=\"font-family: Arial, sans-serif; font-size: 16px; color: #333; background-color: #f4f4f4; padding: 20px;\">"
                +
                "<div style=\"max-width: 600px; margin: auto; background-color: #ffffff; border: 1px solid #ddd; border-radius: 5px; padding: 20px;\">"
                +
                "<h2 style=\"color: #0056b3; text-align: center;\">Formularz kontaktowy</h2>" +
                "<p><strong>Imię:</strong> " + contactFormCommand.getName() + "</p>" +
                "<p><strong>Email:</strong> " + contactFormCommand.getEmail() + "</p>" +
                "<p><strong>Treść wiadomości:</strong></p>" +
                "<div style=\"border-left: 4px solid #0056b3; padding-left: 10px; margin: 10px 0;\">" +
                contactFormCommand.getMessage() + "</div>" +
                "</div></div>";

        helper.setSubject("Formularz kontaktowy");
        helper.setText(htmlContent, true); // Set the second parameter to true for HTML
        helper.setTo("restaurantmanagerbot@gmail.com");

        mailSender.send(mimeMessage);
    }

    public void sentEmailConfirmingContactFormWasSent(String to, String name, String message)
            throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        String htmlContent = "<div style=\"font-family: Arial, sans-serif; font-size: 16px; color: #333; background-color: #f4f4f4; padding: 20px;\">"
                +
                "<div style=\"max-width: 600px; margin: auto; background-color: #ffffff; border: 1px solid #ddd; border-radius: 5px; padding: 20px;\">"
                +
                "<h2 style=\"color: #0056b3; text-align: center;\">Potwierdzenie wysłania formularza kontaktowego</h2>"
                +
                "<p style=\"font-size: 18px;\">Witaj, <strong>" + name + "</strong></p>" +
                "<p>Twoja wiadomość została pomyślnie wysłana do naszego zespołu.</p>" +
                "<p><strong>Treść wiadomości:</strong></p>" +
                "<div style=\"border-left: 4px solid #0056b3; padding-left: 10px; margin: 10px 0;\">" + message
                + "</div>" +
                "<p>Postaramy się odpowiedzieć na nią tak szybko, jak to możliwe.</p>" +
                "<p style=\"margin-top: 30px;\">Dziękujemy,<br><strong>Zespół zarządzania restauracją</strong></p>" +
                "</div></div>";

        helper.setTo(to);
        helper.setSubject("Potwierdzenie wysłania formularza kontaktowego");
        helper.setText(htmlContent, true); // Set the second parameter to true for HTML

        mailSender.send(mimeMessage);
    }

}
