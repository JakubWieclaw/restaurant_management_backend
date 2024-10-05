package com.example.restaurant_management_backend.services;

import com.example.restaurant_management_backend.exceptions.NotFoundException;
import com.example.restaurant_management_backend.jpa.model.Meal;
import com.example.restaurant_management_backend.jpa.model.MealQuantity;
import com.example.restaurant_management_backend.jpa.model.Order;
import com.example.restaurant_management_backend.jpa.model.command.OrderAddCommand;
import com.example.restaurant_management_backend.jpa.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final MealService mealService;
    private final OrderRepository orderRepository;

    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public Order addOrder(OrderAddCommand orderAddCommand) {
        // Validate the order command before processing
        validateOrderAddCommand(orderAddCommand);

        // Calculate the total price considering the quantities
        double totalPrice = calculateTotalPrice(orderAddCommand.getMealIds());

        // Create and save the order
        Order order = new Order(
                orderAddCommand.getMealIds(),
                totalPrice,
                orderAddCommand.getCustomerId(),
                orderAddCommand.getType(),
                orderAddCommand.getStatus(),
                LocalDateTime.now(),
                orderAddCommand.getUnwantedIngredients(),
                orderAddCommand.getDeliveryAddress());
        return orderRepository.save(order);
    }

    public Order updateOrder(Long id, Order order) {
        // Ensure the order exists before updating
        if (!orderRepository.existsById(id)) {
            throw new NotFoundException("Nie znaleziono zamówienia");
        }
        // Set the ID for the update
        order.setId(id);
        return orderRepository.save(order);
    }

    public void deleteOrder(Long id) {
        // Check if the order exists before deletion
        if (!orderRepository.existsById(id)) {
            throw new NotFoundException("Nie znaleziono zamówienia");
        }
        orderRepository.deleteById(id);
    }

    private void validateOrderAddCommand(OrderAddCommand orderAddCommand) {
        // Check if customer ID is valid
        if (orderAddCommand.getCustomerId() == null) {
            throw new IllegalArgumentException("Nie podano ID klienta\nPodaj 0 jeśli klient jest niezarejestrowany");
        }

        // Validate the list of mealId and quantity pairs
        final var mealIds = orderAddCommand.getMealIds();
        if (mealIds == null || mealIds.isEmpty()) {
            throw new IllegalArgumentException("Lista posiłków nie może być pusta");
        }

        for (int i = 0; i < mealIds.size(); i++) {
            final var mealQuantity = mealIds.get(i);

            final var mealId = mealQuantity.getMealId();
            final var quantity = mealQuantity.getQuantity();
            // Validate mealId
            if (!mealService.mealExists(mealId)) {
                throw new NotFoundException("Posiłek o identyfikatorze " + mealId + " nie istnieje");
            }

            // Validate quantity (should be positive)
            if (quantity <= 0) {
                throw new IllegalArgumentException(
                        "Ilość posiłku dla identyfikatora " + mealId + " musi być większa niż 0");
            }

            // Validate unwanted ingredients for the meal at index i
            if (orderAddCommand.getUnwantedIngredients() != null
                    && orderAddCommand.getUnwantedIngredients().containsKey(i)) {
                List<String> unwantedIngredients = orderAddCommand.getUnwantedIngredients().get(i);
                Meal meal = mealService.getMealById(mealId); // Get the meal

                // Check if the unwanted ingredients exist in the meal
                List<String> mealIngredients = meal.getIngredients();
                for (String unwantedIngredient : unwantedIngredients) {
                    if (!mealIngredients.contains(unwantedIngredient)) {
                        throw new IllegalArgumentException("Niepoprawny składnik '" + unwantedIngredient
                                + "' dla posiłku: " + meal.getName() + " (indeks: " + i + ")");
                    }
                }
            }
        }
    }

    private double calculateTotalPrice(List<MealQuantity> mealQuantities) {
        return mealQuantities.stream()
                .mapToDouble(mealQuantity -> {
                    Long mealId = mealQuantity.getMealId();
                    int quantity = mealQuantity.getQuantity();
                    Meal meal = mealService.getMealById(mealId);
                    return meal.getPrice() * quantity;
                })
                .sum();
    }

}
