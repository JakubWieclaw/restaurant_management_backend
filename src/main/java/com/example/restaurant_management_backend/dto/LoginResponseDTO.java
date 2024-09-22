package com.example.restaurant_management_backend.dto;

public record LoginResponseDTO(
        String token,
        Long customerId,
        String customerName,
        String customerSurname,
        String customerEmail,
        boolean isAdmin
) {
}