package com.example.restaurant_management_backend.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StandardErrorResponse {
    private int status;
    private String error;
    private String message;
    private String path;
}
