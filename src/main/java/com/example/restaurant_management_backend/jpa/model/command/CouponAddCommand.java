package com.example.restaurant_management_backend.jpa.model.command;

import com.example.restaurant_management_backend.common.SelfValidating;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CouponAddCommand extends SelfValidating<CouponAddCommand> {

    @Schema(description = "Kod kuponu", example = "POZNAN20")
    @NotBlank(message = "Kod kuponu nie może być pusty")
    private final String code;

    @Schema(description = "Percentage discount for meal", example = "20")
    @NotNull(message = "Należy podać procent zniżki")
    private final Double discountPercentage;

    @Schema(description = "Client id", example = "1")
    @NotNull(message = "Należy podać ID klienta")
    private final Long customerId;

    @Schema(description = "Id of the discounted meal", example = "1")
    @NotNull(message = "Należy podać ID posiłku")
    private final Long mealId;

    @Schema(description = "Expiry date of the coupon", example = "2024-12-31T23:59:59")
    @Future(message = "Data wygaśnięcia musi być w przyszłości")
    private final LocalDateTime expiryDate;

    public CouponAddCommand(String code, Double discountPercentage, Long customerId, Long mealId, LocalDateTime expiryDate) {
        this.code = code;
        this.discountPercentage = discountPercentage;
        this.customerId = customerId;
        this.mealId = mealId;
        this.expiryDate = expiryDate;
        this.validateSelf();
    }
}
