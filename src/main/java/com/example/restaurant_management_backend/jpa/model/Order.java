package com.example.restaurant_management_backend.jpa.model;

import java.time.LocalDateTime;
import java.util.List;

import java.util.HashMap;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;


@Entity(name = "orders")
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ElementCollection
    @CollectionTable(name = "order_meal_quantities", joinColumns = @JoinColumn(name = "order_id"))
    @NotNull(message = "Lista identyfikatorów posiłków nie może być pusta")
    private List<MealQuantity> mealIds;

    @PositiveOrZero(message = "Cena nie może być ujemna")
    private double totalPrice;

    @PositiveOrZero(message = "Identifikator klienta musi być null, dodatni, lub zero")
    private Long customerId;

    @NotNull(message = "Typ zamówienia nie może być pusty")
    private OrderType type;

    @NotNull(message = "Status musi mieć jedną z wartości: WAITING, IN_PROGRESS, READY, IN_DELIVERY, DELIVERED, CANCELED")
    private OrderStatus status;

    @NotNull(message = "Data i czas zamówienia nie mogą być puste")
    private LocalDateTime dateTime;

    private HashMap<Integer, List<String>> unwantedIngredients; // Key corresponds to index of meal in mealIds

    @Size(max = 150, message = "Adres dostawy nie może być dłuższy niż 150 znaków")
    private String deliveryAddress;

    @PositiveOrZero(message = "Odległość dostawy nie może być ujemna")
    private int deliveryDistance;

    public Order(List<MealQuantity> mealIds, double totalPrice, Long customerId, OrderType type,
            OrderStatus status, LocalDateTime dateTime, HashMap<Integer, List<String>> unwantedIngredients,
            String deliveryAddress, int deliveryDistance) {
        this.mealIds = mealIds;
        this.totalPrice = totalPrice;
        this.customerId = customerId;
        this.type = type;
        this.status = status;
        this.dateTime = dateTime;
        this.unwantedIngredients = unwantedIngredients;
        this.deliveryAddress = deliveryAddress;
        this.deliveryDistance = deliveryDistance;
    }
}
