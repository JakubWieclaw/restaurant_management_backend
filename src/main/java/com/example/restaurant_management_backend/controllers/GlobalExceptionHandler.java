package com.example.restaurant_management_backend.controllers;

import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationExceptions(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach((violation) -> {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // @ExceptionHandler(HttpMessageNotReadableException.class)
    // public ResponseEntity<Map<String, String>>
    // handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
    // Map<String, String> response = new HashMap<>();
    // String message = ex.getMessage();
    // if (message.contains("JSON parse error")) {
    // response.put("error", "Invalid JSON format");
    // } else {
    // response.put("error", "Malformed JSON or missing required fields");
    // }
    // return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    // }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Map<String, String> errors = new HashMap<>();
        var logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
        logger.error("Invalid JSON format", ex);

        Throwable cause = ex.getCause();
        if (cause instanceof ValueInstantiationException valueInstantiationException) {
            // Extract the message from the ValueInstantiationException
            String fullMessage = valueInstantiationException.getMessage();

            int problemIndex = fullMessage.indexOf("problem:");
            int columnIndex = fullMessage.indexOf("column:");

            if (problemIndex != -1 && columnIndex != -1) {
                fullMessage = fullMessage.substring(problemIndex + 8, columnIndex).trim();
            }

            // Regular expression to match the field names and their messages
            // String regex = "([a-zA-Z]+): ([^,\\n]+)";
            String regex = "(\\b[a-zA-Z]+\\b):\\s([^,]+)(?:,|$)";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(fullMessage);

            // Iterate through all matches and add them to the errors map
            while (matcher.find()) {
                String fieldName = matcher.group(1).trim();
                String errorMessage = matcher.group(2).trim();
                errorMessage = errorMessage.replaceAll("\\n.*", "");
                errors.put(fieldName, errorMessage);
            }
        }

        // Return the errors map as the response
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<Map<String, String>> handleTransactionSystemException(TransactionSystemException ex) {
        Throwable cause = ex.getRootCause();
        if (cause instanceof ConstraintViolationException) {
            return handleConstraintViolationExceptions((ConstraintViolationException) cause);
        }
        return new ResponseEntity<>(Map.of("error", "Transaction error: " + ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleOtherExceptions(Exception ex) {
        var logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
        logger.error("Unexpected error", ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
