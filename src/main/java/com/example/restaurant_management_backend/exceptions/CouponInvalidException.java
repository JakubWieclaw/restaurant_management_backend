package com.example.restaurant_management_backend.exceptions;

public class CouponInvalidException extends RuntimeException {
    public CouponInvalidException(String message) {
        super(message);
    }
}
