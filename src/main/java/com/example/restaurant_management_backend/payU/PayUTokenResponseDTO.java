package com.example.restaurant_management_backend.payU;

public record PayUTokenResponseDTO(
        String access_token,
        String token_type,
        int expires_in,
        String grant_type
) {
}
