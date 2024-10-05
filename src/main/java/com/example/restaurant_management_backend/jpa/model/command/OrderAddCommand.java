package com.example.restaurant_management_backend.jpa.model.command;

import java.util.HashMap;
import java.util.List;


import com.example.restaurant_management_backend.common.SelfValidating;
import com.example.restaurant_management_backend.jpa.model.MealQuantity;
import com.example.restaurant_management_backend.jpa.model.OrderStatus;
import com.example.restaurant_management_backend.jpa.model.OrderType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
@NotNull
public class OrderAddCommand extends SelfValidating<OrderAddCommand> {

    @NotNull(message = "Lista identyfikatorów posiłków nie może być pusta")
    private List<MealQuantity> mealIds;

    @PositiveOrZero(message = "Identifikator klienta musi być dodatni, lub zero dla niezalogowanego klienta")
    private Long customerId;

    @NotNull(message = "Typ zamówienia musi być jedną z wartości: DOSTAWA, NA_MIEJSCU")
    private OrderType type;

    @NotNull(message = "Status musi mieć jedną z wartości: OCZEKUJACE, W_TRAKCIE_REALIZACJI, GOTOWE, W_DOSTRACZENIU, DOSTARCZONE, ODRZUCONE")
    private OrderStatus status;

    private HashMap<Integer, List<String>> unwantedIngredients;  // Key changed to meal position in list

    @Size(max = 150, message = "Adres dostawy nie może być dłuższy niż 150 znaków")
    private String deliveryAddress;

    public OrderAddCommand(List<MealQuantity> mealIds,
                           Long customerId,
                           OrderType type,
                           OrderStatus status,
                           HashMap<Integer, List<String>> unwantedIngredients,
                           String deliveryAddress) {
        this.mealIds = mealIds;
        this.customerId = customerId;
        this.type = type;
        this.status = status;
        this.unwantedIngredients = unwantedIngredients;
        this.deliveryAddress = deliveryAddress;
        validateSelf();
    }
}
