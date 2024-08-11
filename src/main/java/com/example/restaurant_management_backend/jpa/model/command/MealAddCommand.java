package com.example.restaurant_management_backend.jpa.model.command;

import lombok.Getter;
import jakarta.persistence.ElementCollection;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.example.restaurant_management_backend.common.SelfValidating;
import com.example.restaurant_management_backend.jpa.model.UnitType;

@Getter
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

    public MealAddCommand(String name, Double price, String photographUrl, List<String> ingredients, Double weightOrVolume, UnitType unitType, Long categoryId) {
        this.name = name;
        this.price = price;
        this.photographUrl = photographUrl;
        this.ingredients = ingredients;
        this.weightOrVolume = weightOrVolume;
        this.unitType = unitType;
        this.categoryId = categoryId;
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
