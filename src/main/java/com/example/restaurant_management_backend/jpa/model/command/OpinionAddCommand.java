package com.example.restaurant_management_backend.jpa.model.command;

import com.example.restaurant_management_backend.jpa.model.command.common.SelfValidating;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class OpinionAddCommand extends SelfValidating<OpinionAddCommand> {

    @Schema(description = "Id of the meal", example = "1")
    @NotNull(message = "Id posiłku nie może być puste")
    private final Long mealId;

    @Schema(description = "Client id", example = "1")
    @NotNull(message = "Id klienta nie może być puste")
    private final Long customerId;

    @Schema(description = "Rating for the meal", example = "4")
    @Min(value = 1, message = "Minimalna wartość oceny to 1")
    @Max(value = 5, message = "Maksymalna wartość oceny to 5")
    private final Integer rating;

    @Schema(description = "Comment for the opinion", example = "Meh")
    private final String comment;

    public OpinionAddCommand(Long mealId, Long customerId, Integer rating, String comment) {
        this.mealId = mealId;
        this.customerId = customerId;
        this.rating = rating;
        this.comment = comment;
        this.validateSelf();
    }
}
