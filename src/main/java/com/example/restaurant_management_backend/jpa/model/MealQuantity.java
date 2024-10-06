package com.example.restaurant_management_backend.jpa.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;


@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class MealQuantity {

    @NotNull(message = "Identyfikator posiłku nie może być pusty")
    @Positive(message = "Identyfikator posiłku musi być dodatni")
    private Long mealId;

    @NotBlank(message = "Ilość nie może być pusta")
    @Positive(message = "Ilość musi być dodatnia")
    private int quantity;
}