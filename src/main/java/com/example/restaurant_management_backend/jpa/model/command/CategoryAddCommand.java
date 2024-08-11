package com.example.restaurant_management_backend.jpa.model.command;

import com.example.restaurant_management_backend.common.SelfValidating;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@NotNull
public class CategoryAddCommand extends SelfValidating<CategoryAddCommand> {

    @NotBlank(message = "Nazwa kategorii nie może być pusta")
    private String name;

    public CategoryAddCommand(String name) {
        this.name = name;
        validateSelf();
    }
}