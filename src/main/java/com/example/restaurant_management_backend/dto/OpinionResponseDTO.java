package com.example.restaurant_management_backend.dto;

public record OpinionResponseDTO(
        Long customerId,
        Integer rating,
        String comment
) {
}
