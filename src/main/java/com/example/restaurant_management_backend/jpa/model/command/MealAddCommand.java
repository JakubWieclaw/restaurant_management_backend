package com.example.restaurant_management_backend.jpa.model.command;

import com.example.restaurant_management_backend.common.SelfValidating;
import com.example.restaurant_management_backend.jpa.model.UnitType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.ElementCollection;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

import java.util.List;

@Getter
public class MealAddCommand extends SelfValidating<MealAddCommand> {

    @Schema(description = "Name of the meal", example = "Spaghetti carbonara")
    @NotBlank(message = "Nazwa nie może być pusta")
    private final String name;

    @Schema(description = "Price of the meal", example = "45.99")
    @Positive(message = "Cena musi być dodatnia")
    private final Double price;

    @Schema(description = "URL to the photo of the meal")
    private final String photographUrl; // Optional field

    @Schema(description = "Ingredients used for the meal", example = """
            ["pasta","cheese"]
            """)
    @ElementCollection
    private final List<String> ingredients; // Default empty list

    @Schema(description = "Weight or volume of the meal", example = "2.99")
    @Positive(message = "Waga/objętość nie może być ujemna")
    private final Double weightOrVolume; // Optional field

    @Schema(description = "Weight or volume", example = "GRAMY")
    private final UnitType unitType; // Mandatory if weightOrVolume is provided

    @Schema(description = "Category of the meal", example = "Dania główne")
    @NotNull(message = "Kategoria jest wymagana")
    private final Long categoryId;

    @Schema(description = "Possible allergens for the meal", example = """
            ["gluten","lactose"]
            """)
    @ElementCollection
    private final List<String> allergens; // List of allergens

    @Schema(description = "Calories for the meal in kcal", example = "865")
    @Positive(message = "Kalorie muszą być dodatnie")
    private final int calories; // Amount of calories

    public MealAddCommand(String name, Double price, String photographUrl, List<String> ingredients, Double weightOrVolume, UnitType unitType, Long categoryId, List<String> allergens, int calories) {
        this.name = name;
        this.price = price;
        this.photographUrl = photographUrl;
        this.ingredients = ingredients;
        this.weightOrVolume = weightOrVolume;
        this.unitType = unitType;
        this.categoryId = categoryId;
        this.allergens = allergens;
        this.calories = calories;
        validateSelf();
    }
}
