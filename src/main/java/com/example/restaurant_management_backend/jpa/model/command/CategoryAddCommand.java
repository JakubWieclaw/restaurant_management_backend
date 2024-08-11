package com.example.restaurant_management_backend.jpa.model.command;

import com.example.restaurant_management_backend.common.SelfValidating;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
// @NotNull
public class CategoryAddCommand extends SelfValidating<CategoryAddCommand> {

    @NotBlank(message = "Nazwa kategorii nie może być pusta")
    private String name;

    @JsonCreator
    public CategoryAddCommand(@JsonProperty("name") String name) {
        this.name = name;
        validateSelf();
    }
}