package com.example.restaurant_management_backend.dto;

public record RegisterResponseDTO(
        Long customerId,
        String name,
        String surname,
        String email,
        String phone
) {
}
