package com.example.restaurant_management_backend.jpa.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "ttable")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Table {
    @Id
    private String id;

    @Positive(message = "Pojemność stolika musi być większa od 0")
    private int capacity;
}
