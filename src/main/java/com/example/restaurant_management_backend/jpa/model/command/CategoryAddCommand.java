package com.example.restaurant_management_backend.jpa.model.command;

import com.example.restaurant_management_backend.common.SelfValidating;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CategoryAddCommand extends SelfValidating<CategoryAddCommand> {

    @Schema(description = "Name of the category", example = "Dania główne")
    @NotBlank(message = "Nazwa kategorii nie może być pusta")
    private final String name;

    @Schema(description = "URL to photo of the category")
    private final String photographUrl; // URL to the photograph, not mandatory

    @JsonCreator
    public CategoryAddCommand(@JsonProperty("name") String name,
            @JsonProperty("photographUrl") String photographUrl) {
        this.name = name;
        this.photographUrl = photographUrl; // Can be null if not provided
        validateSelf();
    }
}