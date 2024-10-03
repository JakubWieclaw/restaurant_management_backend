package com.example.restaurant_management_backend.services;

import com.example.restaurant_management_backend.exceptions.NotFoundException;
import com.example.restaurant_management_backend.jpa.model.Meal;
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
        
        // Calculate the total price after validation
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
                orderAddCommand.getDeliveryAddress()
        );
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
        // Check for customer ID validity
        if (orderAddCommand.getCustomerId() == null) {
            throw new IllegalArgumentException("Nie podano ID klienta\nPodaj 0 jeśli klient jest niezarejestrowany");
        }

        // Validate meal IDs and unwanted ingredients
        if (orderAddCommand.getUnwantedIngredients() != null) {
            for (Long key : orderAddCommand.getUnwantedIngredients().keySet()) {
                if (key < 0 || key >= orderAddCommand.getMealIds().size()) {
                    throw new IllegalArgumentException("Niepoprawny indeks w mapie niechcianych składników");
                }

                Long mealId = orderAddCommand.getMealIds().get(key.intValue());
                Meal meal = mealService.getMealById(mealId); // This method will throw NotFoundException if meal doesn't exist

                List<String> unwantedIngredients = orderAddCommand.getUnwantedIngredients().get(key);
                List<String> mealIngredients = meal.getIngredients();

                for (String unwantedIngredient : unwantedIngredients) {
                    if (!mealIngredients.contains(unwantedIngredient)) {
                        throw new IllegalArgumentException("Niepoprawny składnik w zbiorze niechcianych składników dla dania: " + meal.getName() + " na pozycji " + key);
                    }
                }
            }
        }

        // Ensure all meal IDs are valid
        for (Long mealId : orderAddCommand.getMealIds()) {
            if (!mealService.mealExists(mealId)) {
                throw new IllegalArgumentException("Niepoprawny identyfikator posiłku: " + mealId);
            }
        }
    }

    private double calculateTotalPrice(List<Long> mealIds) {
        return mealIds.stream()
                .map(mealService::getMealById) // Get the Meal object
                .mapToDouble(Meal::getPrice)   // Extract the price from the Meal object
                .sum();
    }
}
