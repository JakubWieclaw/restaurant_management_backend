package com.example.restaurant_management_backend.exceptions;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Component
public class CustomErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, options);

        Throwable error = getError(webRequest);
        if (error instanceof ResponseStatusException) {
            // Add the custom message from the ResponseStatusException
            errorAttributes.put("message", error.getMessage());
        } else if (error != null) {
            // If there's another exception type, add a generic message or the actual exception message
            errorAttributes.put("message", error.getMessage());
        }

        return errorAttributes;
    }
}
