package com.example.restaurant_management_backend.jpa.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class Config {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Schema(description = "Name of the restaurant", example = "Restauracja pod niebem")
    @NotBlank(message = "Nazwa restauracji nie może być pusta")
    private String restaurantName;

    @Schema(description = "Postal code of the restaurant", example = "60-123")
    @NotBlank(message = "Kod pocztowy nie może być pusty")
    private String postalCode;

    @Schema(description = "City of the restaurant", example = "Warszawa")
    @NotBlank(message = "Miasto nie może być puste")
    private String city;

    @Schema(description = "Street of the restaurant", example = "ul. Marszałkowska 1")
    @NotBlank(message = "Ulica nie może być pusta")
    private String street;

    @Schema(description = "Phone number of the restaurant", example = "123456789")
    @NotBlank(message = "Numer telefonu nie może być pusty")
    private String phoneNumber;

    @Schema(description = "Email of the restaurant", example = "abc@wp.pl")
    @NotBlank(message = "Email nie może być pusty")
    private String email;

    @Schema(description = "URL for the logo", example = "https://www.creativefabrica.com/wp-content/uploads/2018/10/Chef-restaurant-logo-by-DEEMKA-STUDIO-4.jpg")
    @NotBlank(message = "Odnośnik do logo nie może być pusty")
    private String logoUrl;
}
