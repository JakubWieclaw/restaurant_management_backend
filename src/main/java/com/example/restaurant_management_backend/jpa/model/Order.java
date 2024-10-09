package com.example.restaurant_management_backend.jpa.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


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
    private double orderPrice;

    @PositiveOrZero(message = "Identifikator klienta musi być null, dodatni, lub zero")
    private Long customerId;

    @NotNull(message = "Typ zamówienia nie może być pusty")
    private OrderType type;

    @NotNull(message = "Status musi mieć jedną z wartości: WAITING, IN_PROGRESS, READY, IN_DELIVERY, DELIVERED, CANCELED")
    private OrderStatus status;

    @NotNull(message = "Data i czas zamówienia nie mogą być puste")
    private LocalDateTime dateTime;

    @ElementCollection
    @CollectionTable(name = "unwanted_ingredients", joinColumns = @JoinColumn(name = "order_id"))
    private List<UnwantedIngredient> unwantedIngredients;

    @Size(max = 150, message = "Adres dostawy nie może być dłuższy niż 150 znaków")
    private String deliveryAddress;

    @PositiveOrZero(message = "Odległość dostawy nie może być ujemna")
    private double deliveryDistance;

    @PositiveOrZero(message = "Cena dostawy nie może być ujemna")
    private double deliveryPrice;

    public Order(List<MealQuantity> mealIds, double orderPrice, double deliveryPrice, Long customerId, OrderType type,
                 OrderStatus status, LocalDateTime dateTime, List<UnwantedIngredient> unwantedIngredients,
                 String deliveryAddress, double deliveryDistance) {
        this.mealIds = mealIds;
        this.orderPrice = orderPrice;
        this.deliveryPrice = deliveryPrice;
        this.customerId = customerId;
        this.type = type;
        this.status = status;
        this.dateTime = dateTime;
        this.unwantedIngredients = unwantedIngredients;
        this.deliveryAddress = deliveryAddress;
        this.deliveryDistance = deliveryDistance;
    }
}
