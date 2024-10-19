package com.example.restaurant_management_backend.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

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
                "<p>Otrzymaliśmy prośbę o zresetowanie Twojego hasła. Kliknij poniższy przycisk, aby je zresetować:</p>" +
                "<a href=\"" + resetLink + "\" style=\"display: inline-block; padding: 10px 20px; color: white; background-color: #0056b3; text-decoration: none; border-radius: 5px;\">Zresetuj hasło</a>" +
                "<p>Jeśli nie wysłałeś tej prośby, możesz zignorować tę wiadomość.</p>" +
                "<p>Dziękujemy, <br>Zespół zarządzania restauracją</p>" +
                "</div>";

        helper.setTo(to);
        helper.setSubject("Prośba o zresetowanie hasła");
        helper.setText(htmlContent, true);  // Set the second parameter to true for HTML

        mailSender.send(mimeMessage);
    }

    public void sendRegistrationConfirmationEmail(String to, String customerName) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        String htmlContent = "<div style=\"font-family: Arial, sans-serif; font-size: 16px; color: #333;\">" +
                "<h2 style=\"color: #0056b3;\">Potwierdzenie rejestracji</h2>" +
                "<p>Witaj, " + customerName + "</p>" +
                "<p>Twoje konto zostało pomyślnie zarejestrowane w naszym systemie.</p>" +
                "<p>Możesz teraz zalogować się na swoje konto i korzystać z naszych usług.</p>" +
                "<p>Dziękujemy, <br>Zespół zarządzania restauracją</p>" +
                "</div>";

        helper.setTo(to);
        helper.setSubject("Potwierdzenie rejestracji");
        helper.setText(htmlContent, true);  // Set the second parameter to true for HTML

        mailSender.send(mimeMessage);
    }
}
