package com.example.restaurant_management_backend.exceptions;

public class SystemAlreadyInitializedException extends RuntimeException {
    public SystemAlreadyInitializedException(String message) {
        super(message);
    }
}