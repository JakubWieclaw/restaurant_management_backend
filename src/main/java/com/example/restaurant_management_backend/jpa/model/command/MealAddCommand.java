package com.example.restaurant_management_backend.jpa.model.command;

import lombok.Getter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import com.example.restaurant_management_backend.common.SelfValidating;

@Getter
@NotNull
public class MealAddCommand extends SelfValidating<MealAddCommand> {

    @NotBlank(message = "Nazwa nie może być pusta")
    private String name;

    @NotNull(message = "Cena jest wymagana")
    @Positive(message = "Cena musi być dodatnia")
    private Double price;

    public MealAddCommand(String name, Double price) {
        this.name = name;
        this.price = price;
        validateSelf();
    }
}

