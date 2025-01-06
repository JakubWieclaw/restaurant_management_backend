package com.example.restaurant_management_backend.jpa.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
public class DeliveryPricing {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull(message = "Maksymalny zasięg dostawy nie może być pusty (km)")
    @Valid
    private Integer maximumRange;

    @NotNull(message = "Cena dostawy za dany maksymalny zasięg nie może być pusta")
    @Valid
    private Double price;
}
