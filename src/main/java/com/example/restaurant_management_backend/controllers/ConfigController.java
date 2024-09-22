package com.example.restaurant_management_backend.controllers;

import com.example.restaurant_management_backend.jpa.model.Config;
import com.example.restaurant_management_backend.jpa.model.command.ConfigAddCommand;
import com.example.restaurant_management_backend.services.ConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
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
    public ResponseEntity<String> initializeSystem(@Valid @RequestBody ConfigAddCommand configAddCommand) {
        configService.initialize(configAddCommand);
        return ResponseEntity.ok("Poprawnie zainicjalizowano system dla restauracji " + configAddCommand.getRestaurantName());
    }

    @Operation(summary = "Get config")
    @ApiResponse(description = "Returns the configuration of the system", responseCode = "200", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = Config.class))})
    @GetMapping
    public ResponseEntity<Config> getConfig() {
        Config config = configService.getConfig();
        return ResponseEntity.ok(config);
    }

    @DeleteMapping
    @Operation(summary = "Remove config, delivery prices and opening hours, only for testing purposes")
    public ResponseEntity<String> removeConfigs() {
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
