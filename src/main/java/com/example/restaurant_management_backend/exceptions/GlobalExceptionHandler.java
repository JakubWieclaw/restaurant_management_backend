package com.example.restaurant_management_backend.exceptions;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

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

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<Map<String, String>> handleInvalidFormatException(InvalidFormatException ex) {
        Map<String, String> errors = new HashMap<>();
        logger.error("Invalid format", ex);

        ex.getPath().forEach(reference -> {
            String fieldName = reference.getFieldName();

            // Check if the target type is an enum
            if (ex.getTargetType().isEnum()) {
                // Get the allowed values of the enum as a list of string values
                String allowedValues = Stream.of(ex.getTargetType().getEnumConstants())
                        .map(Object::toString)
                        .collect(Collectors.joining(", "));

                String errorMessage = "Niewłaściwa wartość: " + ex.getValue() + " dla pola " + fieldName
                        + ". Dozwolone wartości: " + allowedValues;
                errors.put(fieldName, errorMessage);
            } else {
                // Handle other cases where the target type is not an enum
                String errorMessage = "Niewłaściwy format dla pola: " + fieldName;
                errors.put(fieldName, errorMessage);
            }
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Map<String, String> errors = new HashMap<>();
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
        if (cause instanceof InvalidFormatException) {
            return handleInvalidFormatException((InvalidFormatException) cause);
        }

        if (cause instanceof JsonMappingException) {
            // if cause of the cause is JsonParseException, handle it
            Throwable causeOfCause = cause.getCause();
            if (causeOfCause instanceof JsonParseException) {
                return handleJsonParseException((JsonParseException) causeOfCause);
            }
        }
        if (cause instanceof JsonParseException) {
            return handleJsonParseException((JsonParseException) cause);
        }

        // If no errors were found, add a generic error message
        if (errors.isEmpty()) {
            errors.put("error", ex.getMessage());
        }
        // Return the errors map as the response
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JsonParseException.class)
    public ResponseEntity<Map<String, String>> handleJsonParseException(JsonParseException ex) {
        logger.error("JSON parsing error", ex);
        Map<String, String> errors = new HashMap<>();

        // Extract the full message from the exception
        String fullMessage = ex.getOriginalMessage(); // Get the original error message from the exception

        // Add the extracted error message to the response
        errors.put("error", fullMessage);

        // Return the error response with a BAD_REQUEST status
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<StandardErrorResponse> handleTransactionSystemException(TransactionSystemException ex, HttpServletRequest request) {
        Throwable cause = ex.getRootCause();
        String message = cause != null ? cause.getMessage() : "Wystąpił błąd transakcji";

        StandardErrorResponse response = new StandardErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Błąd Transakcji",
                message,
                request.getRequestURI()
        );

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<StandardErrorResponse> handleBadCredentialsException(BadCredentialsException ex, HttpServletRequest request) {
        logger.error("Nieprawidłowe dane logowania", ex);

        StandardErrorResponse response = new StandardErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Brak Autoryzacji",
                "Nieprawidłowe dane logowania",
                request.getRequestURI()
        );

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(CredentialsExpiredException.class)
    public ResponseEntity<StandardErrorResponse> handleCredentialsExpiredException(CredentialsExpiredException ex, HttpServletRequest request) {
        logger.error("Poświadczenia wygasły", ex);

        StandardErrorResponse response = new StandardErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Wygasłe Poświadczenia",
                "Poświadczenia wygasły",
                request.getRequestURI()
        );

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<StandardErrorResponse> handleGeneralException(Exception ex, HttpServletRequest request) {
//        logger.error("Unhandled exception", ex);
//
//        StandardErrorResponse response = new StandardErrorResponse(
//                HttpStatus.INTERNAL_SERVER_ERROR.value(),
//                "Internal Server Error",
//                ex.getMessage(),
//                request.getRequestURI()
//        );
//
//        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
//    }
}
