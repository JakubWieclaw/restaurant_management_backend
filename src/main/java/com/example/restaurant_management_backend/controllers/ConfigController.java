package com.example.restaurant_management_backend.controllers;

import com.example.restaurant_management_backend.jpa.model.Config;
import com.example.restaurant_management_backend.jpa.model.DeliveryPricing;
import com.example.restaurant_management_backend.jpa.model.OpeningHour;
import com.example.restaurant_management_backend.jpa.model.command.ConfigAddCommand;
import com.example.restaurant_management_backend.services.ConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/api/config")
@RequiredArgsConstructor
@Validated
public class ConfigController {

    private final ConfigService configService;

    @Operation(summary = "Initialize the system with configuration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "System successfully initialized"),
            @ApiResponse(responseCode = "409", description = "System already initialized", content = @Content)
    })
    @PostMapping("/initialize-system")
    public ResponseEntity<Void> initializeSystem(@Valid @RequestBody ConfigAddCommand configAddCommand) {
        configService.initialize(configAddCommand);
        return ResponseEntity.status(HttpStatus.CREATED).build(); // Return 201 Created
    }

    @Operation(summary = "Get system configuration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns the system configuration", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Config.class))}),
            @ApiResponse(responseCode = "404", description = "System not initialized", content = @Content)
    })
    @GetMapping
    public ResponseEntity<Config> getConfig() {
        Config config = configService.getConfig();
        return ResponseEntity.ok(config);
    }

    @Operation(summary = "Remove all configurations including delivery prices and opening hours (testing purposes only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Configuration successfully removed"),
            @ApiResponse(responseCode = "404", description = "No configuration to remove", content = @Content)
    })
    @DeleteMapping
    public ResponseEntity<Void> removeConfigs() {
        configService.removeAll();
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get delivery prices configuration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns the delivery pricing configuration", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = DeliveryPricing.class))}),
            @ApiResponse(responseCode = "404", description = "System not initialized", content = @Content)
    })
    @GetMapping("/delivery-prices")
    public ResponseEntity<List<DeliveryPricing>> getDeliveryPrices() {
        List<DeliveryPricing> deliveryPrices = configService.getDeliveryPrices();
        return ResponseEntity.ok(deliveryPrices);
    }

    @Operation(summary = "Get opening hours configuration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns the opening hours configuration", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = OpeningHour.class))}),
            @ApiResponse(responseCode = "404", description = "System not initialized", content = @Content)
    })
    @GetMapping("/opening-hours")
    public ResponseEntity<List<OpeningHour>> getOpeningHours() {
        List<OpeningHour> openingHours = configService.getOpeningHours();
        return ResponseEntity.ok(openingHours);
    }
}
