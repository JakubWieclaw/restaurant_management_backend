package com.example.restaurant_management_backend.jpa.model.command;

import com.example.restaurant_management_backend.common.SelfValidating;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class CheckReservationTimesCommand extends SelfValidating<CheckReservationTimesCommand> {
    @NotNull(message = "Lista dni nie może być pusta")
    private final List<LocalDate> days;
    @NotNull(message = "Czas trwania nie może być pusty")
    private final int duration;
    @NotNull(message = "Minuty do dodania odstępów czasowych nie mogą być puste")
    private final int minutesToAdd;
    @NotNull(message = "Liczba osób nie może być pusta")
    private final int people;

    public CheckReservationTimesCommand(List<LocalDate> days, int duration, int minutesToAdd, int people) {
        this.days = days;
        this.duration = duration;
        this.minutesToAdd = minutesToAdd;
        this.people = people;
        this.validateSelf();
    }
}
