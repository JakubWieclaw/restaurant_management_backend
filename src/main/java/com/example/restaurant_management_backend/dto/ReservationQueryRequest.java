package com.example.restaurant_management_backend.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ReservationQueryRequest {
    private List<LocalDate> days;
    private int duration;
    private int minutesToAdd;
    private int people;
}
