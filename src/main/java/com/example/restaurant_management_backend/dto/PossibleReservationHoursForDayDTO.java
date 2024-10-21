package com.example.restaurant_management_backend.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record PossibleReservationHoursForDayDTO(LocalDate date, List<LocalTime> possibleStartTimes) {
}
