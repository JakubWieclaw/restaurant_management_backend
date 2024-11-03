package com.example.restaurant_management_backend.jpa.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Model of a meal")
public class Meal {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Nazwa nie może być pusta")
    @Valid
    private String name;

    @Positive(message = "Cena nie może być ujemna")
    @Valid
    private double price;

    private String photographUrl; // URL to the photograph, not mandatory

    @ElementCollection
    private List<String> ingredients = new ArrayList<>(); // List of ingredients

    @Positive(message = "Waga/objętość nie może być ujemna")
    @Valid
    private Double weightOrVolume; // Weight or volume

    @Enumerated(EnumType.STRING)
    private UnitType unitType; // Unit type, mandatory if weightOrVolume is provided

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @NotNull(message = "Category cannot be null")
    private Category category;

    @ElementCollection
    private List<String> allergens = new ArrayList<>(); // List of allergens

    @Positive(message = "Kalorie muszą być dodatnie")
    @Valid
    private int calories; // Amount of calories

    public Meal(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public Meal(String name, double price, String photographUrl, List<String> ingredients, Double weightOrVolume, UnitType unitType, Category category, List<String> allergens, int calories) {
        this.name = name;
        this.price = price;
        this.photographUrl = photographUrl;
        this.ingredients = ingredients;
        this.weightOrVolume = weightOrVolume;
        this.unitType = unitType;
        this.category = category;
        this.allergens = allergens;
        this.calories = calories;
    }
}