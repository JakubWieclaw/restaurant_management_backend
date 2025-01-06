package com.example.restaurant_management_backend.jpa.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity(name = "ttable")
@jakarta.persistence.Table(name = "ttable")
@Data
@NoArgsConstructor
public class Table {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    @Positive(message = "Pojemność stolika musi być większa od 0")
    private int capacity;

    @OneToMany(mappedBy = "table")
    private List<TableReservation> tableReservations;

    public Table(String id, int capacity) {
        this.id = id;
        this.capacity = capacity;
    }
}
