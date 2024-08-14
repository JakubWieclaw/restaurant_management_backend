package com.example.restaurant_management_backend.controllers;

import com.example.restaurant_management_backend.jpa.model.Config;
import com.example.restaurant_management_backend.service.ConfigService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
@Validated
public class ConfigController {
    private final ConfigService configService;

    @Operation(summary = "Initialize system")
    @PostMapping("/initialize-system")
    public String initializeSystem(Config config) {
        configService.initialize(config);
        return "System initialized";
    }
}
