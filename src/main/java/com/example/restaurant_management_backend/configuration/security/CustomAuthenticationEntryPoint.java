package com.example.restaurant_management_backend.configuration.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, org.springframework.security.core.AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN); // HTTP 403 Forbidden
        response.setContentType("text/plain; charset=UTF-8"); // Set content type as plain text
        response.getWriter().write("Nie masz uprawnień do wykonania tej operacji"); // Plain text response
        response.flushBuffer(); // Ensure the response is sent immediately
    }
}
