package com.example.restaurant_management_backend.services;

import com.example.restaurant_management_backend.exceptions.InvalidReservationException;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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


    @Test
    void makeReservation_noAvailableTables_throwsException() {
        LocalDate date = LocalDate.of(2024, 10, 18);
        LocalTime startTime = LocalTime.of(12, 0);
        LocalTime endTime = LocalTime.of(14, 0);
        int numberOfPeople = 4;

        when(tableService.countTablesWithGreaterOrEqualCapacity(numberOfPeople)).thenReturn(0); // No available tables

        assertThrows(InvalidReservationException.class, () ->
                tableReservationService.makeReservation(date, startTime, endTime, numberOfPeople, 1L)
        );
    }

    @Test
    void makeReservation_inThePast_throwsException() {
        LocalDate pastDate = LocalDate.of(2020, 10, 18);
        LocalTime startTime = LocalTime.of(12, 0);
        LocalTime endTime = LocalTime.of(14, 0);

        assertThrows(InvalidReservationException.class, () ->
                tableReservationService.makeReservation(pastDate, startTime, endTime, 2, 1L)
        );
    }

    @Test
    void makeReservation_withOverlappingReservation_throwsException() {
        LocalDate date = LocalDate.of(2024, 10, 18);
        LocalTime newStartTime = LocalTime.of(12, 0);
        LocalTime newEndTime = LocalTime.of(14, 0);
        int numberOfPeople = 3;

        TableReservation existingReservation = new TableReservation();
        existingReservation.setStartTime(LocalTime.of(11, 30));
        existingReservation.setEndTime(LocalTime.of(13, 30));
        when(tableReservationRepository.findAllByDay(date)).thenReturn(List.of(existingReservation));
        when(tableService.countTablesWithGreaterOrEqualCapacity(numberOfPeople)).thenReturn(1); // Only one table

        assertThrows(InvalidReservationException.class, () ->
                tableReservationService.makeReservation(date, newStartTime, newEndTime, numberOfPeople, 1L)
        );
    }

    @Test
    void makeReservation_successWithNoConflicts() {
        LocalDate date = LocalDate.of(2024, 10, 18);
        LocalTime startTime = LocalTime.of(14, 0);
        LocalTime endTime = LocalTime.of(16, 0);
        int numberOfPeople = 2;

        when(tableService.countTablesWithGreaterOrEqualCapacity(numberOfPeople)).thenReturn(2); // Enough tables
        when(tableReservationRepository.findAllByDay(date)).thenReturn(Collections.emptyList()); // No existing reservations

        tableReservationService.makeReservation(date, startTime, endTime, numberOfPeople, 1L);

        verify(tableReservationRepository, times(1)).save(any(TableReservation.class));
    }

    @Test
    void checkPossibleHoursForDay_withExactCapacityMatch() {
        LocalDate date = LocalDate.of(2024, 10, 18);
        int reservationDuration = 120;
        int minutesToAdd = 15;
        int numberOfPeople = 2;

        when(tableService.countTablesWithGreaterOrEqualCapacity(numberOfPeople)).thenReturn(1); // One exact match table
        List<LocalTime> possibleTimes = tableReservationService.checkPossibleHoursForDay(date, reservationDuration, minutesToAdd, numberOfPeople);

        // Assuming the opening time is set to 09:45 and closing time to 16:00 (from the opening hours setUp)
        assertEquals(List.of(
                LocalTime.of(9, 45),
                LocalTime.of(10, 0),
                LocalTime.of(10, 15),
                LocalTime.of(10, 30),
                LocalTime.of(10, 45),
                LocalTime.of(11, 0),
                LocalTime.of(11, 15),
                LocalTime.of(11, 30),
                LocalTime.of(11, 45),
                LocalTime.of(12, 0),
                LocalTime.of(12, 15),
                LocalTime.of(12, 30),
                LocalTime.of(12, 45),
                LocalTime.of(13, 0),
                LocalTime.of(13, 15),
                LocalTime.of(13, 30),
                LocalTime.of(13, 45),
                LocalTime.of(14, 0)
        ), possibleTimes);
    }
}