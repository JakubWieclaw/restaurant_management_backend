package com.example.restaurant_management_backend.jpa.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "ttable")
@Getter
@Setter
public class Table {
    @Id
    private String id;

    private int capacity;
}
