package com.example.restaurant_management_backend.jpa.model.command;

import com.example.restaurant_management_backend.jpa.model.OrderStatus;
import com.example.restaurant_management_backend.jpa.model.OrderType;
import com.example.restaurant_management_backend.jpa.model.UnwantedIngredient;
import com.example.restaurant_management_backend.jpa.model.command.common.SelfValidating;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.List;

@Getter
public class OrderAddCommand extends SelfValidating<OrderAddCommand> {

    @Schema(description = "Quantities and ids of the meal", example = """
            [
                {"mealId":1, "quantity":2},
                {"mealId":2, "quantity":1}
            ]
            """)
    @NotNull(message = "Lista identyfikatorów posiłków nie może być pusta")
    private final List<MealWithQuantityCommand> mealIds; // We use a new DTO here for mealId and quantity

    @Schema(description = "Client id for the order", example = "1")
    @PositiveOrZero(message = "Identyfikator klienta musi być dodatni, lub zero dla niezalogowanego klienta")
    private final Long customerId;

    @Schema(description = "Type of the order", example = "NA_MIEJSCU")
    @NotNull(message = "Typ zamówienia musi być jedną z wartości: DOSTAWA, NA_MIEJSCU, DO_STOLIKA")
    private final OrderType type;

    @Schema(description = "Status of the order", example = "GOTOWE")
    @NotNull(message = "Status musi mieć jedną z wartości: OCZEKUJACE, W_TRAKCIE_REALIZACJI, GOTOWE, W_DOSTRACZENIU, DOSTARCZONE, ODRZUCONE")
    private final OrderStatus status;

    @Schema(description = "Unwanted ingredients in the order")
    private final List<UnwantedIngredient> unwantedIngredients;

    @Schema(description = "Delivery address, empty if order is in restarurant")
    @Size(max = 150, message = "Adres dostawy nie może być dłuższy niż 150 znaków")
    private final String deliveryAddress;

    @Schema(description = "Delivery distance, zero if it is not related")
    @PositiveOrZero(message = "Odległość dostawy nie może być ujemna")
    private final double deliveryDistance;

    @Schema(description = "If type DO_STOLIKA then add table id")
    private final String tableId;

    @Schema(description = "Number of people on the reservation")
    private final Integer people;

    @Schema(description = "Duration of the reservation in minutes", example = "120")
    private final Integer minutesForReservation;

    @Schema(description = "Coupon id for the order. Empty if not used")
    private final String couponCode;

    public OrderAddCommand(List<MealWithQuantityCommand> mealIds, Long customerId, OrderType type, OrderStatus status, List<UnwantedIngredient> unwantedIngredients, String deliveryAddress, double deliveryDistance, String tableId, Integer people, Integer minutesForReservation, String couponCode) {
        this.mealIds = mealIds;
        this.customerId = customerId;
        this.type = type;
        this.status = status;
        this.unwantedIngredients = unwantedIngredients;
        this.deliveryAddress = deliveryAddress;
        this.deliveryDistance = deliveryDistance;
        this.tableId = tableId;
        this.people = people;
        this.minutesForReservation = minutesForReservation;
        this.couponCode = couponCode;
        validateSelf();
    }

    @Getter
    public static class MealWithQuantityCommand {
        @Schema(description = "Meal ID", example = "1")
        @NotNull
        private final Long mealId;

        @Schema(description = "Quantity of the meal", example = "2")
        @NotNull
        private final Integer quantity;

        public MealWithQuantityCommand(Long mealId, Integer quantity) {
            this.mealId = mealId;
            this.quantity = quantity;
        }
    }
}
