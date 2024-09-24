package com.example.restaurant_management_backend.jpa.model.command;

import com.example.restaurant_management_backend.common.SelfValidating;
import com.example.restaurant_management_backend.jpa.model.DeliveryPricing;
import com.example.restaurant_management_backend.jpa.model.OpeningHour;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.util.List;

@Getter
public class ConfigAddCommand extends SelfValidating<ConfigAddCommand> {
    @Schema(description = "Name of the restaurant", example = "Restauracja pod niebem")
    @NotBlank(message = "Nazwa restauracji nie może być pusta")
    private final String restaurantName;

    @Schema(description = "Postal code of the restaurant", example = "00-000")
    @NotBlank(message = "Kod pocztowy nie może być pusty")
    private final String postalCode;

    @Schema(description = "City of the restaurant", example = "Warszawa")
    @NotBlank(message = "Miasto nie może być puste")
    private final String city;

    @Schema(description = "Street of the restaurant", example = "ul. Marszałkowska 1")
    @NotBlank(message = "Ulica nie może być pusta")
    private final String street;

    @Schema(description = "Phone number of the restaurant", example = "123456789")
    @NotBlank(message = "Numer telefonu nie może być pusty")
    private final String phoneNumber;

    @Schema(description = "Email of the restaurant", example = "abc@wp.pl")
    @NotBlank(message = "Email nie może być pusty")
    private final String email;

    @Schema(description = "URL for the logo", example = "https://www.creativefabrica.com/wp-content/uploads/2018/10/Chef-restaurant-logo-by-DEEMKA-STUDIO-4.jpg")
    @NotBlank(message = "Email nie może być pusty")
    private final String logoUrl;

    @Schema(description = "Opening hours of the restaurant", example = """
            [
                {"day":"MONDAY", "openingTime": "10:00", "closingTime": "22:00"},
                {"day":"TUESDAY", "openingTime": "11:00", "closingTime": "22:00"},
                {"day":"WEDNESDAY", "openingTime": "12:00", "closingTime": "22:00"},
                {"day":"THURSDAY", "openingTime": "13:00", "closingTime": "22:00"},
                {"day":"FRIDAY", "openingTime": "14:00", "closingTime": "22:00"},
                {"day":"SATURDAY", "openingTime": "15:00", "closingTime": "22:00"},
                {"day":"SUNDAY", "openingTime": "16:00", "closingTime": "22:00"}
              ]""")
    private final List<OpeningHour> openingHours;

    @Schema(description = "Delivery prices of the restaurant", example = """
            [
                {"maximumRange": 1, "price": 5.99},
                {"maximumRange": 5, "price": 5.99},
                {"maximumRange": 10, "price": 5.99}
              ]""")
    private final List<DeliveryPricing> deliveryPricings;

    public ConfigAddCommand(String restaurantName, String postalCode, String city, String street, String phoneNumber, String email, String logoUrl, List<OpeningHour> openingHours, List<DeliveryPricing> deliveryPricings) {
        this.restaurantName = restaurantName;
        this.postalCode = postalCode;
        this.city = city;
        this.street = street;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.logoUrl = logoUrl;
        this.openingHours = openingHours;
        this.deliveryPricings = deliveryPricings;
        validateSelf();
    }
}
