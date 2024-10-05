package com.example.restaurant_management_backend.jpa.model;

import lombok.Getter;
import lombok.Setter;
import lombok.RequiredArgsConstructor;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;


@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@RequiredArgsConstructor
@Embeddable
public class MealQuantity {

    @Positive(message = "Identyfikator posiłku musi być dodatni")
    private Long mealId;

    @Positive(message = "Ilość musi być dodatnia")
    private int quantity;
}