package com.example.restaurant_management_backend.jpa.model.command;

import com.example.restaurant_management_backend.common.SelfValidating;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Builder
public class CouponAddCommand extends SelfValidating<CouponAddCommand> {

    @NotBlank(message = "Kod kuponu nie może być pusty")
    private String code;

    @NotNull(message = "Należy podać procent zniżki")
    private Double discountPercentage;

    @NotNull(message = "Należy podać ID klienta")
    private Long customerId;

    @NotNull(message = "Należy podać ID posiłku")
    private Long mealId;

    @Future(message = "Data wygaśnięcia musi być w przyszłości")
    private LocalDateTime expiryDate;

    public CouponAddCommand(String code, Double discountPercentage, Long customerId, Long mealId, LocalDateTime expiryDate) {
        this.code = code;
        this.discountPercentage = discountPercentage;
        this.customerId = customerId;
        this.mealId = mealId;
        this.expiryDate = expiryDate;
        this.validateSelf();
    }
}
