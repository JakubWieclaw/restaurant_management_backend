package com.example.restaurant_management_backend.jpa.model.command;

import com.example.restaurant_management_backend.common.SelfValidating;
import com.example.restaurant_management_backend.jpa.model.UnitType;
import jakarta.persistence.ElementCollection;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NotNull
public class MealAddCommand extends SelfValidating<MealAddCommand> {

    @NotBlank(message = "Nazwa nie może być pusta")
    private String name;

    @NotNull(message = "Cena jest wymagana")
    @Positive(message = "Cena musi być dodatnia")
    private Double price;

    private String photographUrl; // Optional field

    @ElementCollection
    private List<String> ingredients = new ArrayList<>(); // Default empty list

    @Positive(message = "Waga/objętość nie może być ujemna")
    private Double weightOrVolume; // Optional field

    @Valid
    private UnitType unitType; // Mandatory if weightOrVolume is provided

    @NotNull(message = "Kategoria jest wymagana")
    private Long categoryId;

    @ElementCollection
    private List<String> allergens = new ArrayList<>(); // List of allergens

    @Positive(message = "Kalorie muszą być dodatnie")
    @Valid
    private int calories; // Amount of calories

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

    // Ensure unitType is provided if weightOrVolume is present
    // @Override
    // protected void validateSelf() {
    //     super.validateSelf();
    //     if (weightOrVolume != null && unitType == null) {
    //         throw new ConstraintViolationException(Set.of(
    //             new ConstraintViolationImpl<>(
    //                 "Unit type is required when weight or volume is provided",
    //                 MealAddCommand.class,
    //                 "unitType",
    //                 "unitType",
    //                 null
    //             )
    //         ));
    //     }
    // }
}
