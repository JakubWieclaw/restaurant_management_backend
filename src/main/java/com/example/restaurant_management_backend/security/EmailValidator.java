package com.example.restaurant_management_backend.security;

import java.io.*;
import java.net.Socket;
import org.xbill.DNS.*;
import jakarta.mail.Session;
import java.util.Properties;
import org.xbill.DNS.Record;

public class EmailValidator {

    public boolean isValidEmail(String email) {
        if (!isDomainValid(email)) {
            return false;
        }
        return verifyEmailUser(email);
    }

    private boolean isDomainValid(String email) {
        String domain = email.substring(email.indexOf('@') + 1);
        try {
            Lookup lookup = new Lookup(domain, Type.MX);
            Record[] records = lookup.run();
            if (records != null && records.length > 0) {
                MXRecord mxRecord = (MXRecord) records[0];
                return true; // Domain has a valid MX record
            }
        } catch (TextParseException e) {
            // Log or handle exception
            return false;
        }
        return false; // Domain does not have a valid MX record
    }

    private boolean verifyEmailUser(String email) {
        String domain = email.substring(email.indexOf('@') + 1);
        String user = email.substring(0, email.indexOf('@'));
        String sender = "restaurantmanagerbot@gmail.com";

        Properties properties = new Properties();
        properties.put("mail.smtp.host", domain);
        properties.put("mail.smtp.port", "25");  // Default SMTP port
        properties.put("mail.smtp.connectiontimeout", "5000"); // Timeout in case server is slow
        properties.put("mail.smtp.timeout", "5000");

        Session session = Session.getDefaultInstance(properties);

        try (Socket socket = new Socket(domain, 25);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            // Read initial server response
            readResponse(reader);

            // Send EHLO/HELO command
            sendCommand(writer, "EHLO example.com");
            readResponse(reader);

            // Send MAIL FROM command
            sendCommand(writer, "MAIL FROM: <" + sender + ">");
            readResponse(reader);

            // Send RCPT TO command with the recipient email address
            sendCommand(writer, "RCPT TO: <" + email + ">");
            String response = readResponse(reader);

            if (response.startsWith("250")) {
                return true;  // The email address exists
            }
        } catch (IOException e) {
            // Log or handle exception
            return false;
        }
        return false;  // The email address does not exist or can't be verified
    }

    private void sendCommand(BufferedWriter writer, String command) throws IOException {
        writer.write(command + "\r\n");
        writer.flush();
    }

    private String readResponse(BufferedReader reader) throws IOException {
        return reader.readLine();
    }
}
