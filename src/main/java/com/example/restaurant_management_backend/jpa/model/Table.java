package com.example.restaurant_management_backend.jpa.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "ttable")
@jakarta.persistence.Table(name = "ttable")
@Data
@NoArgsConstructor
public class Table {
    @Id
    private String id;

    @Positive(message = "Pojemność stolika musi być większa od 0")
    private int capacity;

    @OneToMany(mappedBy = "table")
    @JsonIgnore
    private List<TableReservation> tableReservations = new ArrayList<>();

    public Table(String id, int capacity) {
        this.id = id;
        this.capacity = capacity;
    }
}
