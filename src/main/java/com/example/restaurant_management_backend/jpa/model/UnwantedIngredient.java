package com.example.restaurant_management_backend.jpa.model;

import java.util.List;
import java.util.Set;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class UnwantedIngredient {

    @PositiveOrZero(message = "Indeks posiłku musi być liczbą nieujemną")
    private int mealIndex;

    // should contain only of unique strings

    // could be a set, but JS is stupid
    private List<String> ingredients;

    public UnwantedIngredient(int mealIndex, List<String> ingredients) {
        this.mealIndex = mealIndex;

        // make ingredients unique
        this.ingredients = List.copyOf(Set.copyOf(ingredients));
    }
}

