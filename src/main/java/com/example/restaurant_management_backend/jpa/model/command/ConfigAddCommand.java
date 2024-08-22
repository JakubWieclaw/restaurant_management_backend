package com.example.restaurant_management_backend.jpa.model.command;

import com.example.restaurant_management_backend.common.SelfValidating;
import com.example.restaurant_management_backend.jpa.model.DeliveryPricing;
import com.example.restaurant_management_backend.jpa.model.OpeningHour;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
@NotNull
public class ConfigAddCommand extends SelfValidating<ConfigAddCommand> {
    @Schema(description = "Name of the restaurant", example = "Restauracja pod niebem")
    @NotBlank(message = "Nazwa restauracji nie może być pusta")
    @Valid
    private final String restaurantName;
    @Schema(description = "Postal code of the restaurant", example = "00-000")
    @NotBlank(message = "Kod pocztowy nie może być pusty")
    @Valid
    private final String postalCode;
    @Schema(description = "City of the restaurant", example = "Warszawa")
    @NotBlank(message = "Miasto nie może być puste")
    @Valid
    private final String city;
    @Schema(description = "Street of the restaurant", example = "ul. Marszałkowska 1")
    @NotBlank(message = "Ulica nie może być pusta")
    @Valid
    private final String street;
    @Schema(description = "Phone number of the restaurant", example = "123456789")
    @NotBlank(message = "Numer telefonu nie może być pusty")
    @Valid
    private final String phoneNumber;
    @Schema(description = "Email of the restaurant", example = "abc@wp.pl")
    @NotBlank(message = "Email nie może być pusty")
    @Valid
    private final String email;

    @Schema(description = "Opening hours of the restaurant")
    @NotBlank(message = "Godziny otwarcia nie mogą być puste")
    private final List<OpeningHour> openingHours;

    @Schema(description = "Delivery prices of the restaurant")
    @NotBlank(message = "Cennik dostaw nie mogą być puste")
    private final List<DeliveryPricing> deliveryPricings;

    public ConfigAddCommand(String restaurantName, String postalCode, String city, String street, String phoneNumber, String email, List<OpeningHour> openingHours, List<DeliveryPricing> deliveryPricings) {
        this.restaurantName = restaurantName;
        this.postalCode = postalCode;
        this.city = city;
        this.street = street;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.openingHours = openingHours;
        this.deliveryPricings = deliveryPricings;
        validateSelf();
    }
}
