package com.example.restaurant_management_backend.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
@Slf4j
public class ResourceConflictException extends RuntimeException {
    public ResourceConflictException(String message) {
        super(message);
        log.error("Resource conflict: {}", message);
    }
}
