package com.example.restaurant_management_backend.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
@Slf4j
public class SystemNotInitializedException extends RuntimeException {
    public SystemNotInitializedException(String message) {
        super(message);
        log.error("System not initialized yet: {}", message);
    }
}
