package com.example.restaurant_management_backend.exceptions;

public class IllegalStateException extends RuntimeException {

    public IllegalStateException(String message, Throwable cause) {
        super(message);
    }
}
