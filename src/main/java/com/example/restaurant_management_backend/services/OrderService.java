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
        if (order.isEmpty()) {
            throw new NotFoundException(NOT_FOUND_ORDER);
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
            throw new IllegalArgumentException(INVALID_ID);
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
     * @param request the data required to add a new order
     * @return the newly created and saved order
     * @throws IllegalArgumentException if the order data is invalid
     * @throws NotFoundException        if a referenced meal does not exist
     */
    public Order addOrder(OrderAddCommand request) {
        validateOrderAddCommand(request);
        double orderPrice = calculateOrderPrice(request.getMealIds(), request.getDeliveryDistance());
        double deliveryPrice = countDeliveryPrice(request.getDeliveryDistance());
        LocalDateTime now = LocalDateTime.now();

        if (request.getTableId() != null && request.getType() == OrderType.NA_MIEJSCU) {
            tableReservationService.makeReservation(
                    now.toLocalDate(), // date
                    now.toLocalTime(), // start time of reservation
                    now.toLocalTime().plusMinutes(120), // end time of reservation
                    4, // number of people
                    request.getCustomerId() // customer id
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
                request.getDeliveryDistance());
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

        // 0.01 is used to avoid issues regarding double precision
        // If delivery distance is greater than 0 and order type is NA_MIEJSCU, throw an
        // exception
        if (orderAddCommand.getDeliveryDistance() > 0.01 && orderAddCommand.getType().equals(OrderType.NA_MIEJSCU)) {
            throw new IllegalArgumentException("Zamówenie na miejscu nie może mieć odległości dostawy większej niż 0");
        }

        // If delivery distance is 0 and order type is DOSTAWA, throw an exception
        if (orderAddCommand.getDeliveryDistance() < 0.01 && orderAddCommand.getType().equals(OrderType.DOSTAWA)) {
            throw new IllegalArgumentException("Zamówienie na dostawę musi mieć odległość dostawy większą niż 0");
        }

        // If order type is DO_STOLIKA and delivery distance is greater than 0, throw an
        if (orderAddCommand.getDeliveryDistance() > 0.01 && orderAddCommand.getType().equals(OrderType.DO_STOLIKA)) {
            throw new IllegalArgumentException("Zamówienie DO_STOLIKA nie może mieć odległości dostawy większej niż 0");
        }

        // If order type is DO_STOLIKA and table ID is missing, throw an exception
        if (orderAddCommand.getType().equals(OrderType.DO_STOLIKA)
                && (orderAddCommand.getTableId() == null || orderAddCommand.getTableId().isEmpty())) {
            throw new IllegalArgumentException("Zamówienie DO_STOLIKA wymaga podania identyfikatora stolika");
        }

        // Validate tableId should not be provided if the order type is not DO_STOLIKA
        if (!orderAddCommand.getType().equals(OrderType.DO_STOLIKA) && orderAddCommand.getTableId() != null) {
            throw new IllegalArgumentException(
                    "Identyfikator stolika może być podany tylko dla zamówienia typu DO_STOLIKA");
        }

        // Validate deliveryAddress: it should be empty if the order is not for delivery
        if (!orderAddCommand.getType().equals(OrderType.DOSTAWA) && orderAddCommand.getDeliveryAddress() != null
                && !orderAddCommand.getDeliveryAddress().isEmpty()) {
            throw new IllegalArgumentException("Adres dostawy może być podany tylko dla zamówienia typu DOSTAWA");
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

        // Validate unwanted ingredients for the meal
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

                // iterate through mealIds (only through indexes mentioned in
                // unwantedIngredients)
                // and check if given meal consists of unwanted ingredients
                final var mealQuantity = mealIds.get(mealIndex);
                final var meal = mealService.getMealById(mealQuantity.getMealId());

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
