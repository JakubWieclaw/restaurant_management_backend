package com.example.restaurant_management_backend.services;

import com.example.restaurant_management_backend.exceptions.GlobalExceptionHandler;
import com.example.restaurant_management_backend.exceptions.NotFoundException;
import com.example.restaurant_management_backend.jpa.model.*;
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

    public static final String NOT_FOUND_ORDER = "Nie znaleziono zamówienia";
    public static final String INVALID_ID = "Niepoprawne ID klienta";
    private final MealService mealService;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ConfigService configService;
    private final TableReservationService tableReservationService;

    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isEmpty()) {
            throw new NotFoundException(NOT_FOUND_ORDER);
        }
        return order;
    }

    public List<Order> getAllOrdersOfCustomer(Long customerId) {
        if (customerId == null || customerId < 0) {
            throw new IllegalArgumentException(INVALID_ID);
        }
        if (!customerRepository.existsById(customerId)) {
            throw new NotFoundException("Klient o identyfikatorze " + customerId + " nie istnieje");
        }
        return orderRepository.findByCustomerId(customerId);
    }

    public Order addOrder(OrderAddCommand request) {
        validateOrderAddCommand(request);
        double orderPrice = calculateOrderPrice(request.getMealIds(), request.getDeliveryDistance());
        double deliveryPrice = countDeliveryPrice(request.getDeliveryDistance());
        LocalDateTime now = LocalDateTime.now();

        // Find or create TableReservation if a table ID is provided in the request
        TableReservation tableReservation = null;
        if (request.getTableId() != null && request.getPeople() != null) {
            int minutes = request.getMinutesForReservation() == null ? 120 : request.getMinutesForReservation();
            tableReservation = tableReservationService.findOrCreateReservation(
                    now.toLocalDate(), // date
                    now.toLocalTime(), // start time
                    now.toLocalTime().plusMinutes(minutes), // end time
                    request.getPeople(), // number of people
                    request.getCustomerId() // customer ID
            );
        }

        Order order = new Order(
                request.getMealIds(),
                orderPrice,
                deliveryPrice,
                request.getCustomerId(),
                request.getType(),
                request.getStatus(),
                now,
                request.getUnwantedIngredients(),
                request.getDeliveryAddress(),
                request.getDeliveryDistance(),
                tableReservation);
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
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_ORDER));

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
            throw new NotFoundException(NOT_FOUND_ORDER);
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

    public Order addOrderToReservation(Long orderId, Long reservationId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));
        TableReservation reservation = tableReservationService.getTableReservationById(reservationId);

        order.setTableReservation(reservation); // Link order to reservation
        return orderRepository.save(order); // Save updated order
    }
}
