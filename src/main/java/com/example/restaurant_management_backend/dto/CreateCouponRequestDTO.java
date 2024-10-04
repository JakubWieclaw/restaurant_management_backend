package com.example.restaurant_management_backend.dto;

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
public class CreateCouponRequestDTO {

    @NotBlank(message = "Coupon code cannot be blank")
    private String code;

    @NotNull(message = "Discount percentage must be provided")
    private Double discountPercentage;

    @NotNull(message = "Customer ID must be provided")
    private Long customerId;

    @NotNull(message = "Meal ID must be provided")
    private Long mealId;

    @Future(message = "Expiry date must be in the future")
    private LocalDateTime expiryDate;
}
