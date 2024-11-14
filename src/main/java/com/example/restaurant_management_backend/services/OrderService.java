package com.example.restaurant_management_backend.services;

import com.example.restaurant_management_backend.exceptions.InvalidReservationException;
import com.example.restaurant_management_backend.exceptions.NotFoundException;
import com.example.restaurant_management_backend.jpa.model.*;
import com.example.restaurant_management_backend.jpa.model.command.OrderAddCommand;
import com.example.restaurant_management_backend.jpa.repositories.CustomerRepository;
import com.example.restaurant_management_backend.jpa.repositories.OrderRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    public static final String NOT_FOUND_ORDER = "Nie znaleziono zamówienia";
    public static final String INVALID_ID = "Niepoprawne ID klienta";
    private final MealService mealService;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ConfigService configService;
    private final TableReservationService tableReservationService;
    private final CouponService couponService;

    public OrderService(MealService mealService, OrderRepository orderRepository, CustomerRepository customerRepository, ConfigService configService, TableReservationService tableReservationService, CouponService couponService) {
        this.mealService = mealService;
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.configService = configService;
        this.tableReservationService = tableReservationService;
        this.couponService = couponService;
        Stripe.apiKey = "sk_test_51Q4Qqx6w25OikflfKXNobStEmR6z73dBnYZ0LSPh6fIvjJUwcUbHIE2nLSx9NNrZfRrPvivlvXp4eVOpAqiSO8SV00pmOtUcMF";
    }

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

        // Calculate order and delivery prices
        double orderPrice = calculateOrderPrice(request.getMealIds(), request.getCouponCode(), request.getCustomerId());
        double deliveryPrice = countDeliveryPrice(request.getDeliveryDistance());

        LocalDateTime now = LocalDateTime.now();
        TableReservation tableReservation = null;
        if (request.getType().equals(OrderType.DO_STOLIKA)) {
            tableReservation = tableReservationService.findOrCreateReservation(
                    now.toLocalDate(),
                    now.toLocalTime(),
                    now.toLocalTime().plusMinutes(request.getMinutesForReservation()),
                    request.getPeople(),
                    request.getCustomerId(),
                    request.getTableId()
            );
            if (tableReservation == null) {
                throw new InvalidReservationException("Nie udało się utworzyć rezerwacji stolika");
            }
        }

        // Create the Order object without saving it yet
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

        // Calculate the total amount in the smallest currency unit (e.g., cents)
        var totalAmount = (orderPrice + deliveryPrice) * 100;

        // Create a Stripe Payment Intent
        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount((long) totalAmount)
                    .setCurrency("pln")
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder().setEnabled(true).build())
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);
            order.setPaymentIntentClientSecret(paymentIntent.getClientSecret());
        } catch (StripeException e) {
            throw new RuntimeException("Nie można utworzyć transakcji płatności", e);
        }

        // Save the Order with all details including the client secret
        return orderRepository.save(order);
    }

    public Order updateOrder(Long id, OrderAddCommand orderAddCommand) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_ORDER));

        validateOrderAddCommand(orderAddCommand);
        double newOrderPrice = calculateOrderPrice(orderAddCommand.getMealIds(), orderAddCommand.getCouponCode(), orderAddCommand.getCustomerId());
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
            throw new IllegalArgumentException("Zamówienie na miejscu nie może mieć odległości dostawy większej niż 0");
        }

        // If delivery distance is 0 and order type is DOSTAWA, throw an exception
        if (orderAddCommand.getDeliveryDistance() < 0.01 && orderAddCommand.getType().equals(OrderType.DOSTAWA)) {
            throw new IllegalArgumentException("Zamówienie na dostawę musi mieć odległość dostawy większą niż 0");
        }

        if (orderAddCommand.getType().equals(OrderType.DO_STOLIKA)) {
            if (orderAddCommand.getPeople() == null || orderAddCommand.getPeople() <= 0) {
                throw new InvalidReservationException("Należy podać liczbę osób na rezerwacji stolika");
            }
            if (orderAddCommand.getMinutesForReservation() == null || orderAddCommand.getMinutesForReservation() <= 0) {
                throw new InvalidReservationException("Należy podać liczbę minut preznaczoną na rezerwację stolika");
            }
            if (orderAddCommand.getTableId() == null) {
                throw new InvalidReservationException("Należy podać identyfikator stolika");
            }
            if (orderAddCommand.getDeliveryDistance() > 0.01) {
                throw new InvalidReservationException("Rezerwacja stolika nie może mieć dystansu dostawy większego niż 0");
            }
        }


        // Validate deliveryAddress: it should be empty if the order is not for delivery
        if (!orderAddCommand.getType().equals(OrderType.DOSTAWA) && orderAddCommand.getDeliveryAddress() != null
                && !orderAddCommand.getDeliveryAddress().isEmpty()) {
            throw new IllegalArgumentException("Adres dostawy może być podany tylko dla zamówienia typu DOSTAWA");
        }

        for (final MealQuantity mealQuantity : mealIds) {
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
            for (final UnwantedIngredient unwantedIngredient : unwantedIngredients) {
                final var mealIndex = unwantedIngredient.getMealIndex();
                final var ingredients = getIngredients(unwantedIngredient, mealIndex, mealIds);

                // iterate through mealIds (only through indexes mentioned in
                // unwantedIngredients)
                // and check if given meal consists of unwanted ingredients
                final var mealQuantity = mealIds.get(mealIndex);
                final var meal = mealService.getMealById(mealQuantity.getMealId());

                // check if all ingredients are present in the meal
                if (!new HashSet<>(meal.getIngredients()).containsAll(ingredients)) {
                    throw new IllegalArgumentException("Posiłek o indeksie " + mealIndex
                            + " nie zawiera wszystkich podanych składników, które chcesz usunąć");
                }
            }
        }
    }

    private List<String> getIngredients(UnwantedIngredient unwantedIngredient, int mealIndex, List<MealQuantity> mealIds) {
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
        return ingredients;
    }

    private double calculateOrderPrice(List<MealQuantity> mealQuantities, String couponCode, Long customerId) {
        return mealQuantities.stream()
                .mapToDouble(mealQuantity -> {
                    Long mealId = mealQuantity.getMealId();
                    int quantity = mealQuantity.getQuantity();
                    Meal meal = mealService.getMealById(mealId);
                    if (couponCode == null) {
                        return meal.getPrice() * quantity;
                    }
                    double discountedPrice = couponService.applyCoupon(couponCode, customerId, mealId, meal.getPrice());
                    return discountedPrice * quantity;
                })
                .sum();
    }

    private double countDeliveryPrice(double deliveryDistance) {
        double deliveryPrice = 0;
        if (deliveryDistance > 0) {
            final var deliveryPrices = configService.getDeliveryPrices();
            // Sort delivery prices by maximum range
            deliveryPrices.sort(Comparator.comparingInt(DeliveryPricing::getMaximumRange));
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
                .orElseThrow(() -> new NotFoundException("Nie znaleziono zamówienia"));
        TableReservation reservation = tableReservationService.getTableReservationById(reservationId);

        order.setTableReservation(reservation); // Link order to reservation
        return orderRepository.save(order); // Save updated order
    }
}
