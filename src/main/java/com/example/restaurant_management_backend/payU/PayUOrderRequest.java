package com.example.restaurant_management_backend.payU;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PayUOrderRequest {
    private double amount;
    private Long orderId;
}
