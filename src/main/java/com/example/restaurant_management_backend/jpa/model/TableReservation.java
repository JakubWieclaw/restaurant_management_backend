package com.example.restaurant_management_backend.jpa.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class TableReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String tableId;
    @NotNull
    private int people;
    @NotNull
    private LocalDate day;
    @NotNull
    private LocalTime startTime;
    @NotNull
    private LocalTime endTime;
    private long duration; // default 2 hours
    // allow null for not registered users
    private Long customerId;
}
