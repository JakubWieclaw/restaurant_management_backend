package com.example.restaurant_management_backend.services;

import com.example.restaurant_management_backend.jpa.model.OpeningHour;
import com.example.restaurant_management_backend.jpa.model.TableReservation;
import com.example.restaurant_management_backend.jpa.repositories.TableReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class TableReservationServiceTest {
    @InjectMocks
    private TableReservationService tableReservationService;
    @Mock
    private ConfigService configService;
    @Mock
    private TableService tableService;
    @Mock
    private TableReservationRepository tableReservationRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        List<OpeningHour> openingHours = new ArrayList<>();
        for (DayOfWeek value : DayOfWeek.values()) {
            OpeningHour openingHour = new OpeningHour();
            openingHour.setDay(value);
            openingHour.setOpeningTime(LocalTime.of(9, 45, 0));
            openingHour.setClosingTime(LocalTime.of(16, 0, 0));
            openingHours.add(openingHour);
        }
        when(configService.getOpeningHours()).thenReturn(openingHours);
        when(tableReservationRepository.findAllByDay(any())).thenReturn(Collections.emptyList());
    }


    @Test
    void checkPossibleHoursForDay() {
        LocalDate date = LocalDate.of(2024, 10, 18);
        int reservationDuration = 120;
        int minutesToAdd = 15;
        when(tableService.countTablesWithGreaterOrEqualCapacity(4)).thenReturn(0);
        when(tableService.countTablesWithGreaterOrEqualCapacity(3)).thenReturn(1);
        when(tableService.countTablesWithGreaterOrEqualCapacity(2)).thenReturn(2);
        when(tableService.countTablesWithGreaterOrEqualCapacity(1)).thenReturn(2);

        List<LocalTime> timesFor4People = tableReservationService.checkPossibleHoursForDay(date, reservationDuration, minutesToAdd, 4);
        List<LocalTime> timesFor3People = tableReservationService.checkPossibleHoursForDay(date, reservationDuration, minutesToAdd, 3);
        List<LocalTime> timesFor2People = tableReservationService.checkPossibleHoursForDay(date, reservationDuration, minutesToAdd, 2);
        List<LocalTime> timesFor1People = tableReservationService.checkPossibleHoursForDay(date, reservationDuration, minutesToAdd, 1);

        assertEquals(Collections.emptyList(), timesFor4People);
        assertNotEquals(Collections.emptyList(), timesFor3People);
        assertNotEquals(Collections.emptyList(), timesFor2People);
        assertNotEquals(Collections.emptyList(), timesFor1People);

        TableReservation tableReservation = new TableReservation();
        tableReservation.setDay(date);
        tableReservation.setStartTime(LocalTime.of(12, 0, 0));
        tableReservation.setEndTime(LocalTime.of(14, 0, 0));
        when(tableReservationRepository.findAllByDay(any())).thenReturn(List.of(tableReservation));

        timesFor4People = tableReservationService.checkPossibleHoursForDay(date, reservationDuration, minutesToAdd, 4);
        timesFor3People = tableReservationService.checkPossibleHoursForDay(date, reservationDuration, minutesToAdd, 3);
        timesFor2People = tableReservationService.checkPossibleHoursForDay(date, reservationDuration, minutesToAdd, 2);
        timesFor1People = tableReservationService.checkPossibleHoursForDay(date, reservationDuration, minutesToAdd, 1);

        assertEquals(Collections.emptyList(), timesFor4People);
        assertEquals(List.of(LocalTime.of(9, 45, 0)), timesFor3People);
        assertNotEquals(Collections.emptyList(), timesFor2People);
        assertNotEquals(Collections.emptyList(), timesFor1People);
    }
}