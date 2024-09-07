package com.example.restaurant_management_backend.jpa.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Entity(name = "orders")
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull(message = "Lista identyfikatorów posiłków nie może być pusta")
    private List<Long> mealIds;

    @PositiveOrZero(message = "Cena nie może być ujemna")
    private double totalPrice;

    @PositiveOrZero(message = "Identifikator klienta musi być null, dodatni, lub zero")
    private Long customerId;

    // Type field can only be one of the following values: DELIVERY, ON_SITE
    @NotNull(message = "Typ zamówienia nie może być pusty")
    private OrderType type;

    @NotBlank(message = "Status musi mieć jedną z wartości: WAITING, IN_PROGRESS, READY, IN_DELIVERY, DELIVERED, CANCELED")
    private OrderStatus status;

    // Date and time of order creation
    @NotNull(message = "Data i czas zamówienia nie mogą być puste")
    private LocalDateTime dateTime;

    public Order(List<Long> mealIds, double totalPrice, Long customerId, OrderType type, OrderStatus status, LocalDateTime dateTime) {
        this.mealIds = mealIds;
        this.totalPrice = totalPrice;
        this.customerId = customerId;
        this.type = type;
        this.status = status;
        this.dateTime = dateTime;
    }
}
