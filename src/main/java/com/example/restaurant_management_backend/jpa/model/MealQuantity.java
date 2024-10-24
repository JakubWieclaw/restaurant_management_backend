package com.example.restaurant_management_backend.jpa.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class MealQuantity {

    @Positive(message = "Identyfikator posiłku musi być dodatni")
    private Long mealId;

    @Positive(message = "Ilość musi być dodatnia")
    private int quantity;
}