package com.example.restaurant_management_backend.jpa.model.command;

import com.example.restaurant_management_backend.common.SelfValidating;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class MakeReservationCommand extends SelfValidating<MakeReservationCommand> {

    @Schema(description = "Reservation day", example = "2024-11-01")
    @FutureOrPresent(message = "Data musi być w przyszłości")
    private final LocalDate day;

    @Schema(description = "Start time of the reservation", example = "18:00")
    @NotNull(message = "Godzina rozpoczęcia nie może być pusta")
    private final LocalTime startTime;

    @Schema(description = "End time of the reservation", example = "19:30")
    @NotNull(message = "Godzina zakończenia nie może być pusta")
    private final LocalTime endTime;

    @Schema(description = "Amount of people on the reservation", example = "4")
    @NotNull(message = "Liczba osób nie może być pusta")
    private final int numberOfPeople;

    @Schema(description = "Client id responsible for reservation", example = "1")
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