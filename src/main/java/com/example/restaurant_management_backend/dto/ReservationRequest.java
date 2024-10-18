package com.example.restaurant_management_backend.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ReservationRequest {
    private LocalDate day;
    private LocalTime startTime;
    private LocalTime endTime;
    private int numberOfPeople;
    private Long customerId;
}