package com.example.restaurant_management_backend.jpa.model.command;

import jakarta.validation.Valid;
import lombok.Getter;
import jakarta.validation.constraints.NotNull;

import com.example.restaurant_management_backend.common.SelfValidating;

@Getter
@Valid
@NotNull
public class MealAddCommand extends SelfValidating<MealAddCommand> {

    @Valid
    @NotNull
    private String name;

    @Valid
    @NotNull
    private double price;

    public MealAddCommand(String name, double price) {
        this.name = name;
        this.price = price;
        validateSelf();
    }
}
