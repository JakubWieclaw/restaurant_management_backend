package com.example.restaurant_management_backend.jpa.model.command;

import java.time.LocalDateTime;
import java.util.List;

import com.example.restaurant_management_backend.jpa.model.OrderStatus;
import com.example.restaurant_management_backend.jpa.model.OrderType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;


@Getter
@NotNull
public class OrderAddCommand {
    
    @NotNull(message = "Lista identyfikatorów posiłków nie może być pusta")
    private List<Long> mealIds;

    @PositiveOrZero(message = "Identifikator klienta musi być dodatni, lub zero dla niezalogowanego klienta")
    private Long customerId;

    // Type field can only be one of the following values: DELIVERY, ON_SITE
    @NotNull(message = "Typ zamówienia nie może być pusty")
    @Pattern(regexp = "DOSTAWA|NA_MIEJSCU", message = "Typ zamówienia musi być jednym z dwóch: DOSTAWA, NA_MIEJSCU")
    private OrderType type;

    @NotBlank(message = "Status musi mieć jedną z wartości: OCZEKUJACE, W_TRAKCIE_REALIZACJI, GOTOWE, W_DOSTRACZENIU, DOSTARCZONE, ODRZUCONE")
    private OrderStatus status;

    public OrderAddCommand(List<Long> mealIds, Long customerId, OrderType type, OrderStatus status) {
        this.mealIds = mealIds;
        this.customerId = customerId;
        this.type = type;
        this.status = status;
    }
}
