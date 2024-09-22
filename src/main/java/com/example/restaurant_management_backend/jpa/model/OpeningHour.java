package com.example.restaurant_management_backend.jpa.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@Entity
public class OpeningHour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Dzień tygodnia nie może być pusty")
    private DayOfWeek day;

    @NotNull(message = "Godzina otwarcia nie może być pusta")
    private LocalTime openingTime;

    @NotNull(message = "Godzina zamknięcia nie może być pusta")
    private LocalTime closingTime;
}
