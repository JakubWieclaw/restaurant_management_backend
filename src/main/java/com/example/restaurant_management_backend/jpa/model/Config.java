package com.example.restaurant_management_backend.jpa.model;

import com.example.restaurant_management_backend.jpa.additionalModel.DayOfWeek;
import com.example.restaurant_management_backend.jpa.additionalModel.DayTime;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class Config {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Name of the restaurant", example = "Restauracja pod niebem")
    private String restaurantName;
    @Schema(description = "Postal code of the restaurant", example = "00-000")
    private String postalCode;
    @Schema(description = "City of the restaurant", example = "Warszawa")
    private String city;
    @Schema(description = "Street of the restaurant", example = "ul. Marszałkowska 1")
    private String street;
    @Schema(description = "Phone number of the restaurant", example = "123456789")
    private String phoneNumber;
    @Schema(description = "Email of the restaurant", example = "abc@wp.pl")
    private String email;

    @Schema(description = "Opening hours of the restaurant",
            example =
                    "{MONDAY: {openingTime: 10:00, closingTime: 22:00}, TUESDAY: {openingTime: 10:00, closingTime: 22:00}, WEDNESDAY: {openingTime: 10:00, closingTime: 22:00}, THURSDAY: {openingTime: 10:00, closingTime: 22:00}, FRIDAY: {openingTime: 10:00, closingTime: 22:00}, SATURDAY: {openingTime: 10:00, closingTime: 22:00}, SUNDAY: {openingTime: 10:00, closingTime: 22:00}}")
    @ElementCollection
    private Map<DayOfWeek, DayTime> openingHours;

    @Schema(description = "Delivery prices of the restaurant", example = "{0: 0, 10: 5, 20: 10}")
    @ElementCollection
    private Map<Integer, BigDecimal> deliveryPrices;
}
