package com.example.restaurant_management_backend.jpa.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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

    private long duration;

    private Long customerId;

    @OneToMany(mappedBy = "tableReservation", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Order> orders;
}
