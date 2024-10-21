package com.example.restaurant_management_backend.jpa.model.command;

import com.example.restaurant_management_backend.common.SelfValidating;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class MakeReservationCommand extends SelfValidating<MakeReservationCommand> {
    @FutureOrPresent(message = "Data musi być w przyszłości")
    private final LocalDate day;
    @NotNull(message = "Godzina rozpoczęcia nie może być pusta")
    private final LocalTime startTime;
    @NotNull(message = "Godzina zakończenia nie może być pusta")
    private final LocalTime endTime;
    @NotNull(message = "Liczba osób nie może być pusta")
    private final int numberOfPeople;

    private final Long customerId;

    public MakeReservationCommand(LocalDate day, LocalTime startTime, LocalTime endTime, int numberOfPeople, Long customerId) {
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.numberOfPeople = numberOfPeople;
        this.customerId = customerId;
        this.validateSelf();
    }
}