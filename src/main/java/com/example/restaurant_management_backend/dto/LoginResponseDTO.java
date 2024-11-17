package com.example.restaurant_management_backend.dto;

import java.time.LocalDateTime;

// public record LoginResponseDTO(
//         String token,
//         Long customerId,
//         String customerName,
//         String customerSurname,
//         String customerEmail,
//         boolean isAdmin
// ) {
// }


public record LoginResponseDTO(
        String tokenString,
        LocalDateTime expiryDate,
        LocalDateTime creationDate,
        Long customerId
) {
}