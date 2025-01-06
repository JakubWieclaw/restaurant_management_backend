package com.example.restaurant_management_backend.jpa.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    @ManyToOne
    @JoinColumn(name = "meal_id", referencedColumnName = "id")
    private Meal meal; // Reference to the Meal entity

    @Positive(message = "Ilość musi być dodatnia")
    private int quantity;
}