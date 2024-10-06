package com.example.restaurant_management_backend.exceptions;

public class SystemNotInitializedException extends RuntimeException {
    public SystemNotInitializedException(String message) {
        super(message);
    }
}
