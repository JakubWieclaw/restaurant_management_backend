package com.example.restaurant_management_backend.jpa.model.command;

import com.example.restaurant_management_backend.common.SelfValidating;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OpinionAddCommand extends SelfValidating<OpinionAddCommand> {
    @NotNull(message = "Id posiłku nie może być puste")
    private Long mealId;

    @NotNull(message = "Id klienta nie może być puste")
    private Long customerId;

    @Min(value = 1, message = "Minimalna wartość oceny to 1")
    @Max(value = 5, message = "Maksymalna wartość oceny to 5")
    private Integer rating;

    private String comment;

    public OpinionAddCommand(Long mealId, Long customerId, Integer rating, String comment) {
        this.mealId = mealId;
        this.customerId = customerId;
        this.rating = rating;
        this.comment = comment;
        this.validateSelf();
    }
}
