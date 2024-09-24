package com.example.restaurant_management_backend.controllers;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.restaurant_management_backend.jpa.model.Order;
import com.example.restaurant_management_backend.jpa.model.command.OrderAddCommand;
import com.example.restaurant_management_backend.services.MealService;
import com.example.restaurant_management_backend.services.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
// import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Validated
public class OrderController {

    private final OrderService orderService;
    private final MealService mealService;


    @Operation(summary = "Get all orders")
    @GetMapping("/all")
    public ResponseEntity<?> getAllOrders() {
        try {
            return ResponseEntity.ok(orderService.getOrders());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Błąd podczas pobierania zamówień");
        }
    }

    @Operation(summary = "Get order by id")
    @ApiResponse(description = "Returns an order with a given id", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = Order.class)) })
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        try {
            Optional<Order> order = orderService.getOrderById(id);
            if (order.isPresent()) {
                return ResponseEntity.ok(order.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nie znaleziono zamówienia");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Błąd podczas pobierania zamówienia");
        }
    }

    @Operation(summary = "Add new order")
    @PostMapping("/add")
    public ResponseEntity<?> addOrder(@RequestBody OrderAddCommand orderAddCommand) {
        var logger = LoggerFactory.getLogger(OrderController.class);
        logger.info("Received OrderAddCommand: {}", orderAddCommand);
        try {
            // check if customer ID is valid
            if (orderAddCommand.getCustomerId() == null) {
                return ResponseEntity.badRequest().body("Nie podano ID klienta\nPodaj 0 jeśli klient jest niezarejestrowany");
            }

            // check if all meal IDs are valid
            // for (Long mealId : orderAddCommand.getMealIds()) {
            //     // if meal throws illegal argument exception, return bad request
            //     // if (mealService.getMealById(mealId).isEmpty()) {
            //     //     return ResponseEntity.badRequest().body("Nie znaleziono dania o id " + mealId);
            //     // }
            // }
            // count total price
            double totalPrice = 0;
            for (Long mealId : orderAddCommand.getMealIds()) {
                totalPrice += mealService.getMealById(mealId).getPrice();
            }
            // set order with counted total price and current date
            var order = new Order(orderAddCommand.getMealIds(), totalPrice, orderAddCommand.getCustomerId(),
                    orderAddCommand.getType(), orderAddCommand.getStatus(), LocalDateTime.now());
            orderService.addOrder(order);
            logger.info("Added new order: {}", order);
            return ResponseEntity.ok(order);
        } catch (TransactionSystemException e) {
            Throwable cause = e.getRootCause();
            logger.error("Error while adding order", e);
            if (cause instanceof ConstraintViolationException) {
                logger.error("Validation error: {}", cause.getMessage());
                return ResponseEntity.badRequest().body("Niepoprawne dane zamówienia");
            }
        } catch (IllegalArgumentException e) {
            logger.error("Error while adding order", e);
            return ResponseEntity.badRequest().body("Nie znaleziono dania o podanym id");
        } 
        catch (Exception e) {
            logger.error("Error while adding order", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Błąd podczas dodawania zamówienia");
        }
        return ResponseEntity.badRequest().body("Unknown error");
    }

    @Operation(summary = "Update order")
    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable Long id, @RequestBody Order order) {
        var logger = LoggerFactory.getLogger(OrderController.class);
        try {
            // Get order to check if it exists
            Optional<Order> existingOrder = orderService.getOrderById(id);
            if (existingOrder.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nie znaleziono zamówienia");
            }
            // update order
            Order updatedOrder = orderService.updateOrder(id, order);
            logger.info("Updated order: {}", updatedOrder);
            return ResponseEntity.ok(updatedOrder);
        } catch (TransactionSystemException e) {
            Throwable cause = e.getRootCause();
            if (cause instanceof ConstraintViolationException) {
                logger.error("Validation error: {}", cause.getMessage());
                return ResponseEntity.badRequest().body("Niepoprawne dane zamówienia");
            }
        } catch (Exception e) {
            logger.error("Error while updating order", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Błąd podczas aktualizacji zamówienia");
        }
        return ResponseEntity.badRequest().body("Unknown error");
    }

    @Operation(summary = "Delete order")
    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
        var logger = LoggerFactory.getLogger(OrderController.class);
        try {
            // Get order to check if it exists
            Optional<Order> existingOrder = orderService.getOrderById(id);
            if (existingOrder.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nie znaleziono zamówienia");
            }
            orderService.deleteOrder(id);
            logger.info("Deleted order with id: {}", id);
            return ResponseEntity.ok("Usunięto zamówienie");
        } catch (Exception e) {
            logger.error("Error while deleting order", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Błąd podczas usuwania zamówienia");
        }
    }
}
