package com.example.restaurant_management_backend.services;

import com.example.restaurant_management_backend.exceptions.NotFoundException;
import com.example.restaurant_management_backend.jpa.model.Meal;
import com.example.restaurant_management_backend.jpa.model.MealQuantity;
import com.example.restaurant_management_backend.jpa.model.Order;
import com.example.restaurant_management_backend.jpa.model.UnwantedIngredient;
import com.example.restaurant_management_backend.jpa.model.command.OrderAddCommand;
import com.example.restaurant_management_backend.jpa.repositories.CustomerRepository;
import com.example.restaurant_management_backend.jpa.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final MealService mealService;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ConfigService configService;

    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public List<Order> getAllOrdersOfCustomer(Long customerId) {
        // if customerID is null or it does not exist, thrown NotFoundException
        if (customerId == null) {
            throw new NotFoundException("Podany identyfikator klienta jest niepoprawny");
        }
        if (!customerRepository.existsById(customerId)) {
            throw new NotFoundException("Klient o identyfikatorze " + customerId + " nie istnieje");
        }
        return orderRepository.findByCustomerId(customerId);
    }

    public Order addOrder(OrderAddCommand orderAddCommand) {
        // Validate the order command before processing
        validateOrderAddCommand(orderAddCommand);

        // Calculate the total price considering the quantities
        double totalPrice = calculateTotalPrice(orderAddCommand.getMealIds(), orderAddCommand.getDeliveryDistance());

        // Create and save the order
        Order order = new Order(
                orderAddCommand.getMealIds(),
                totalPrice,
                orderAddCommand.getCustomerId(),
                orderAddCommand.getType(),
                orderAddCommand.getStatus(),
                LocalDateTime.now(),
                orderAddCommand.getUnwantedIngredients(),
                orderAddCommand.getDeliveryAddress(),
                orderAddCommand.getDeliveryDistance());
        return orderRepository.save(order);
    }

    public Order updateOrder(Long id, OrderAddCommand orderAddCommand) {
        // Ensure the order exists
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Nie znaleziono zamówienia"));

        // Validate the new order data
        validateOrderAddCommand(orderAddCommand);

        // Recalculate the total price considering the new mealIds and delivery distance
        double newTotalPrice = calculateTotalPrice(orderAddCommand.getMealIds(), orderAddCommand.getDeliveryDistance());

        // Update the existing order with new details from OrderAddCommand
        existingOrder.setMealIds(orderAddCommand.getMealIds());
        existingOrder.setCustomerId(orderAddCommand.getCustomerId());
        existingOrder.setType(orderAddCommand.getType());
        existingOrder.setStatus(orderAddCommand.getStatus());
        existingOrder.setUnwantedIngredients(orderAddCommand.getUnwantedIngredients());
        existingOrder.setDeliveryAddress(orderAddCommand.getDeliveryAddress());
        existingOrder.setDeliveryDistance(orderAddCommand.getDeliveryDistance());
        existingOrder.setTotalPrice(newTotalPrice);
        existingOrder.setDateTime(LocalDateTime.now());

        // Save and return the updated order
        return orderRepository.save(existingOrder);
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

            // getMealID provides integer, cast it to Long
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
            if (orderAddCommand.getUnwantedIngredients() != null) {
                List<String> unwantedIngredients = orderAddCommand.getUnwantedIngredients().stream()
                        .map(UnwantedIngredient::getIngredient).toList();
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

    private double calculateTotalPrice(List<MealQuantity> mealQuantities, int deliveryDistance) {
        double deliveryPrice = 0;
        // Get all delivery pricing from config service, browse through them and find
        // the one that matches the distance, if the distance exceeds the maximum
        // distance, throw IllegalArgumentException
        if (deliveryDistance > 0) {
            final var deliveryPrices = configService.getDeliveryPrices();
            for (var deliveryPriceEntry : deliveryPrices) {
                final var maxRange = deliveryPriceEntry.getMaximumRange();
                if (maxRange >= deliveryDistance) {
                    deliveryPrice = deliveryPriceEntry.getPrice();
                    break;
                }
            }
        }
        if (deliveryPrice == 0 && deliveryDistance > 0) {
            throw new IllegalArgumentException("Nie znaleziono ceny dostawy dla odległości: " + deliveryDistance);
        }
        return mealQuantities.stream()
                .mapToDouble(mealQuantity -> {
                    Long mealId = mealQuantity.getMealId();
                    int quantity = mealQuantity.getQuantity();
                    Meal meal = mealService.getMealById(mealId);
                    return meal.getPrice() * quantity;
                })
                .sum() + deliveryPrice;
    }

}
