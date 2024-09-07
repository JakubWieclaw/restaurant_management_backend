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

    @PositiveOrZero(message = "Identifikator klienta musi być null, dodatni, lub zero")
    private Long customerId;

    // Type field can only be one of the following values: DELIVERY, ON_SITE
    @NotNull(message = "Typ zamówienia nie może być pusty")
    @Pattern(regexp = "DELIVERY|ON_SITE", message = "Typ zamówienia musi być jednym z: DELIVERY, ON_SITE")
    private OrderType type;

    @NotBlank(message = "Status musi mieć jedną z wartości: WAITING, IN_PROGRESS, READY, IN_DELIVERY, DELIVERED, CANCELED")
    private OrderStatus status;

    // Date and time of order creation
    @NotNull(message = "Data i czas zamówienia nie mogą być puste")
    private LocalDateTime dateTime;

    public OrderAddCommand(List<Long> mealIds, Long customerId, OrderType type, OrderStatus status, LocalDateTime dateTime) {
        this.mealIds = mealIds;
        this.customerId = customerId;
        this.type = type;
        this.status = status;
        this.dateTime = dateTime;
    }
}
