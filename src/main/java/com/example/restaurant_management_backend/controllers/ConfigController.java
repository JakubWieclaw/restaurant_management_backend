package com.example.restaurant_management_backend.controllers;

import com.example.restaurant_management_backend.jpa.model.command.ConfigAddCommand;
import com.example.restaurant_management_backend.services.ConfigService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/api/config")
@RequiredArgsConstructor
@Validated
public class ConfigController {
    private final ConfigService configService;

    @Operation(summary = "Initialize system")
    @PostMapping("/initialize-system")
    public ResponseEntity<?> initializeSystem(@RequestBody ConfigAddCommand configAddCommand) {
        if (configService.isSystemInitialized()) {
            return ResponseEntity.badRequest().body("System został już zainicjalizowany");
        }
        configService.initialize(configAddCommand);

        return ResponseEntity.ok("Poprawnie zainicjalizowano system dla restauracji " + configAddCommand.getRestaurantName());
    }

    @Operation(summary = "Get config")
    @GetMapping
    public ResponseEntity<?> getConfig() {
        if (!configService.isSystemInitialized()) {
            return ResponseEntity.badRequest().body("System nie został zainicjalizowany");
        }
        return ResponseEntity.ok(configService.getConfig());
    }

    @DeleteMapping
    @Operation(summary = "Remove config, delivery prices and opening hours, only for testing purposes")
    public ResponseEntity<?> removeConfigs() {
        if (!configService.isSystemInitialized()) {
            return ResponseEntity.badRequest().body("Nie ma wgranej żadnej konfiguracji");
        }
        configService.removeAll();
        return ResponseEntity.ok("Pomyślnie usunięto konfigurację");
    }

    @GetMapping("/delivery-prices")
    @Operation(summary = "Get delivery prices")
    public ResponseEntity<?> getDeliveryPrices() {
        return ResponseEntity.ok(configService.getDeliveryPrices());
    }

    @GetMapping("/opening-hours")
    @Operation(summary = "Get opening hours")
    public ResponseEntity<?> getOpeningHours() {
        return ResponseEntity.ok(configService.openingHours());
    }
}
