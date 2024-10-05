package com.example.restaurant_management_backend.jpa.model.command;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponAddCommand {

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
}
