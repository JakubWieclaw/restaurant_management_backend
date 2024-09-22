package com.example.restaurant_management_backend.jpa.model.command;

import java.util.List;

import com.example.restaurant_management_backend.common.SelfValidating;
import com.example.restaurant_management_backend.jpa.model.OrderStatus;
import com.example.restaurant_management_backend.jpa.model.OrderType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NotNull
public class OrderAddCommand extends SelfValidating<OrderAddCommand> {

    @NotNull(message = "Lista identyfikatorów posiłków nie może być pusta")
    @JsonProperty("mealIds")
    private List<Long> mealIds;

    @PositiveOrZero(message = "Identifikator klienta musi być dodatni, lub zero dla niezalogowanego klienta")
    @JsonProperty("customerId")
    private Long customerId;

    @NotNull(message = "Typ zamówienia musi być jedną z wartości: DOSTAWA, NA_MIEJSCU")
    @JsonProperty("type")
    private OrderType type;

    @NotNull(message = "Status musi mieć jedną z wartości: OCZEKUJACE, W_TRAKCIE_REALIZACJI, GOTOWE, W_DOSTRACZENIU, DOSTARCZONE, ODRZUCONE")
    @JsonProperty("status")
    private OrderStatus status;

    @JsonCreator
    public OrderAddCommand(List<Long> mealIds,
            Long customerId,
            OrderType type,
            OrderStatus status) {
        this.mealIds = mealIds;
        this.customerId = customerId;
        this.type = type;
        this.status = status;
        validateSelf();
    }
}
