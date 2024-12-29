package com.example.restaurant_management_backend.jpa.model.command;

import com.example.restaurant_management_backend.jpa.model.command.common.SelfValidating;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class CheckReservationTimesCommand extends SelfValidating<CheckReservationTimesCommand> {

    @Schema(description = "Dates to search for possible reservation", example = """
            [
                "2024-10-28", "2024-10-29", "2024-10-30"
            ]
            """)
    @NotNull(message = "Lista dni nie może być pusta")
    private final List<LocalDate> days;

    @Schema(description = "Duration of the reservation in minutes", example = "120")
    @NotNull(message = "Czas trwania nie może być pusty")
    private final int duration;

    @Schema(description = """
            Minutes between each timestamp\s
            15 -> 10:00, 10:15..;\s
            10 -> 10:00, 10:10..""", example = "15")
    @Positive(message = "Minuty do dodania odstępów czasowych muszą być większe od zera")
    private final int minutesToAdd;

    @Schema(description = "How many people will be on the reservation", example = "4")
    @Positive(message = "Minimalnie jedna osoba musi być na rezerwacji")
    private final int people;

    public CheckReservationTimesCommand(List<LocalDate> days, int duration, int minutesToAdd, int people) {
        this.days = days;
        this.duration = duration;
        this.minutesToAdd = minutesToAdd;
        this.people = people;
        this.validateSelf();
    }
}
