package com.example.restaurant_management_backend.payU;

import java.net.URI;

public record PayUOrderResponse(
        URI redirectUri,
        String orderId,
        String extOrderId
) {
}
