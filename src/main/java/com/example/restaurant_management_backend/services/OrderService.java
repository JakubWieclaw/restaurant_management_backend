package com.example.restaurant_management_backend.services;

import com.example.restaurant_management_backend.exceptions.GlobalExceptionHandler;
import com.example.restaurant_management_backend.exceptions.NotFoundException;
import com.example.restaurant_management_backend.jpa.model.Meal;
import com.example.restaurant_management_backend.jpa.model.MealQuantity;
import com.example.restaurant_management_backend.jpa.model.Order;
import com.example.restaurant_management_backend.jpa.model.OrderType;
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

    /**
     * Retrieves all orders from the repository.
     *
     * @return a list of all orders
     */
    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    /**
     * Retrieves an order by its ID.
     * <p>
     * If the order does not exist, this method throws a {@link NotFoundException},
     * which is handled by the {@link GlobalExceptionHandler} to return a 404
     * response.
     * </p>
     *
     * @param id the ID of the order to retrieve
     * @return an {@link Optional} containing the order if found
     * @throws NotFoundException if the order with the specified ID does not exist
     * @see GlobalExceptionHandler#handleNotFoundException(NotFoundException)
     */
    public Optional<Order> getOrderById(Long id) {
        Optional<Order> order = orderRepository.findById(id);
        if (!order.isPresent()) {
            throw new NotFoundException("Nie znaleziono zamówienia");
        }
        return order;
    }

    /**
     * Retrieves all orders for a specific customer.
     * <p>
     * This method checks if the customer ID is valid and exists in the repository.
     * If the customer ID is null, negative, or does not exist, it throws an
     * exception.
     * </p>
     *
     * @param customerId the ID of the customer
     * @return a list of orders associated with the specified customer
     * @throws IllegalArgumentException if the customer ID is invalid
     * @throws NotFoundException        if the customer with the specified ID does
     *                                  not exist
     */
    public List<Order> getAllOrdersOfCustomer(Long customerId) {
        if (customerId == null || customerId < 0) {
            throw new IllegalArgumentException("Niepoprawne ID klienta");
        }
        if (!customerRepository.existsById(customerId)) {
            throw new NotFoundException("Klient o identyfikatorze " + customerId + " nie istnieje");
        }
        return orderRepository.findByCustomerId(customerId);
    }

    /**
     * Adds a new order to the repository.
     * <p>
     * This method validates the order details, calculates the total price, creates
     * a new order object, and saves it to the repository.
     * </p>
     *
     * @param orderAddCommand the data required to add a new order
     * @return the newly created and saved order
     * @throws IllegalArgumentException if the order data is invalid
     * @throws NotFoundException        if a referenced meal does not exist
     */
    public Order addOrder(OrderAddCommand orderAddCommand) {
        validateOrderAddCommand(orderAddCommand);
        double orderPrice = calculateOrderPrice(orderAddCommand.getMealIds(), orderAddCommand.getDeliveryDistance());
        double deliveryPrice = countDeliveryPrice(orderAddCommand.getDeliveryDistance());

        Order order = new Order(
                orderAddCommand.getMealIds(),
                orderPrice,
                deliveryPrice,
                orderAddCommand.getCustomerId(),
                orderAddCommand.getType(),
                orderAddCommand.getStatus(),
                LocalDateTime.now(),
                orderAddCommand.getUnwantedIngredients(),
                orderAddCommand.getDeliveryAddress(),
                orderAddCommand.getDeliveryDistance());
        return orderRepository.save(order);
    }

    /**
     * Updates an existing order with new details.
     * <p>
     * This method ensures the order exists, validates the new order data,
     * recalculates
     * the total price, and updates the order with the new information.
     * </p>
     *
     * @param id              the ID of the order to be updated
     * @param orderAddCommand the new order data to update
     * @return the updated order
     * @throws NotFoundException        if the order with the specified ID does not
     *                                  exist
     * @throws IllegalArgumentException if the new order data is invalid
     */
    public Order updateOrder(Long id, OrderAddCommand orderAddCommand) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Nie znaleziono zamówienia"));

        validateOrderAddCommand(orderAddCommand);
        double newOrderPrice = calculateOrderPrice(orderAddCommand.getMealIds(), orderAddCommand.getDeliveryDistance());
        double newDeliveryPrice = countDeliveryPrice(orderAddCommand.getDeliveryDistance());

        existingOrder.setMealIds(orderAddCommand.getMealIds());
        existingOrder.setCustomerId(orderAddCommand.getCustomerId());
        existingOrder.setType(orderAddCommand.getType());
        existingOrder.setStatus(orderAddCommand.getStatus());
        existingOrder.setUnwantedIngredients(orderAddCommand.getUnwantedIngredients());
        existingOrder.setDeliveryAddress(orderAddCommand.getDeliveryAddress());
        existingOrder.setDeliveryDistance(orderAddCommand.getDeliveryDistance());
        existingOrder.setOrderPrice(newOrderPrice);
        existingOrder.setDeliveryPrice(newDeliveryPrice);
        existingOrder.setDateTime(LocalDateTime.now());

        return orderRepository.save(existingOrder);
    }

    /**
     * Deletes an order by its ID.
     * <p>
     * This method checks if an order with the given ID exists in the repository.
     * If the order does not exist, it throws a {@link NotFoundException}.
     * The {@link NotFoundException} is handled by the
     * {@link GlobalExceptionHandler},
     * which returns a response with a 404 status code to the client.
     * </p>
     *
     * @param id the ID of the order to be deleted
     * @throws NotFoundException if the order with the specified ID does not exist
     * @see GlobalExceptionHandler#handleNotFoundException(NotFoundException)
     */
    public void deleteOrder(Long id) {
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

        // 0.01 is used t oavoid issues regarding double precision
        // If deliverydistance is greater than 0 and order type is NA_MIEJSCU, throw an exception
        if (orderAddCommand.getDeliveryDistance() > 0.01 && orderAddCommand.getType().equals(OrderType.NA_MIEJSCU)) {
            throw new IllegalArgumentException("Zamówenie na miejscu nie może mieć odległości dostawy większej niż 0");
        }

        // If deliverydistance is 0 and order type is DOSTAWA, throw an exception
        if (orderAddCommand.getDeliveryDistance() < 0.01 && orderAddCommand.getType().equals(OrderType.DOSTAWA)) {
            throw new IllegalArgumentException("Zamówienie na dostawę musi mieć odległość dostawy większą niż 0");
        }

        for (int i = 0; i < mealIds.size(); i++) {
            final var mealQuantity = mealIds.get(i);

            // getMealID provides integer, cast it to Long
            final var mealId = mealQuantity.getMealId();
            final var quantity = mealQuantity.getQuantity();

            if (mealId == null) {
                throw new IllegalArgumentException("Identyfikator posiłku i ilość nie mogą być puste");
            }

            // Validate mealId
            if (!mealService.mealExists(mealId)) {
                throw new NotFoundException("Posiłek o identyfikatorze " + mealId + " nie istnieje");
            }

            // Validate quantity (should be positive)
            if (quantity <= 0) {
                throw new IllegalArgumentException(
                        "Ilość posiłku dla identyfikatora " + mealId + " musi być większa niż 0");
            }
        }

        // Validate unwanted ingredients for the meal at index i
        List<UnwantedIngredient> unwantedIngredients = orderAddCommand.getUnwantedIngredients();
        if (unwantedIngredients != null) {
            for (int i = 0; i < unwantedIngredients.size(); i++) {
                final var unwantedIngredient = unwantedIngredients.get(i);
                final var mealIndex = unwantedIngredient.getMealIndex();
                final var ingredients = unwantedIngredient.getIngredients();

                // Validate mealIndex
                if (mealIndex < 0 || mealIndex >= mealIds.size()) {
                    throw new IllegalArgumentException(
                            "Indeks posiłku musi być liczbą nieujemną bądź większy niż rozmiar listy posiłków");
                }

                // Validate ingredients
                if (ingredients == null || ingredients.isEmpty()) {
                    throw new IllegalArgumentException("Lista niechcianych składników nie może być pusta");
                }

                // iterate through mealIds (onlty through indexes mentioned in
                // unwantedIngredients) and check if given meal consists of unwanted ingredients
                final var mealQuanity = mealIds.get(mealIndex);
                final var meal = mealService.getMealById(mealQuanity.getMealId());

                // check if all ingredients are present in the meal
                if (!meal.getIngredients().containsAll(ingredients)) {
                    throw new IllegalArgumentException("Posiłek o indeksie " + mealIndex
                            + " nie zawiera wszystkich podanych składników, które chcesz usunąć");
                }

            }
        }
    }

    private double calculateOrderPrice(List<MealQuantity> mealQuantities, double deliveryDistance) {
        return mealQuantities.stream()
                .mapToDouble(mealQuantity -> {
                    Long mealId = mealQuantity.getMealId();
                    int quantity = mealQuantity.getQuantity();
                    Meal meal = mealService.getMealById(mealId);
                    return meal.getPrice() * quantity;
                })
                .sum();
    }

    private double countDeliveryPrice(double deliveryDistance) {
        double deliveryPrice = 0;
        if (deliveryDistance > 0) {
            final var deliveryPrices = configService.getDeliveryPrices();
            // Sort delivery prices by maximum range
            deliveryPrices.sort((a, b) -> (int) (a.getMaximumRange() - b.getMaximumRange()));
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
        return deliveryPrice;
    }

}
