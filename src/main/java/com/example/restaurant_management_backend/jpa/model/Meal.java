package com.example.restaurant_management_backend.jpa.model;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Entity
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
@Table(name = "meal")
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

    @NotNull(message = "Kategoria nie może być pusta")
    private Long categoryId;

    @ElementCollection
    private List<String> allergens = new ArrayList<>(); // List of allergens

    @Positive(message = "Kalorie muszą być dodatnie")
    @Valid
    private int calories; // Amount of calories

    public Meal(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public Meal(String name, double price, String photographUrl, List<String> ingredients, Double weightOrVolume, UnitType unitType, Long categoryId, List<String> allergens, int calories) {
        this.name = name;
        this.price = price;
        this.photographUrl = photographUrl;
        this.ingredients = ingredients;
        this.weightOrVolume = weightOrVolume;
        this.unitType = unitType;
        this.categoryId = categoryId;
        this.allergens = allergens;
        this.calories = calories;
    }

    public boolean hasWeightOrVolume() {
        return weightOrVolume != null;
    }

    public boolean isUnitTypeMandatory() {
        return weightOrVolume != null && unitType == null;
    }
}