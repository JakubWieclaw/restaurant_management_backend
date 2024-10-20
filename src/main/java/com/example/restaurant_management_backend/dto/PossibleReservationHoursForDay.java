package com.example.restaurant_management_backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class PossibleReservationHoursForDay {
    private LocalDate date;
    private List<LocalTime> possibleStartTimes;
}
