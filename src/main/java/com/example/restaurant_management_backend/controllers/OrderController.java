package com.example.restaurant_management_backend.controllers;

import com.example.restaurant_management_backend.exceptions.NotFoundException;
import com.example.restaurant_management_backend.jpa.model.Order;
import com.example.restaurant_management_backend.jpa.model.command.OrderAddCommand;
import com.example.restaurant_management_backend.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Validated
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;

    @Operation(summary = "Get all orders")
    @GetMapping("/all")
    public ResponseEntity<?> getAllOrders() {
        orderService.getOrders();
        logger.info("Getting all orders");
        return ResponseEntity.ok(orderService.getOrders());
    }

    @Operation(summary = "Get order by id")
    @ApiResponse(description = "Returns an order with a given id", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = Order.class)) })
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        final var order = orderService.getOrderById(id);
        return ResponseEntity.ok(order.get());
    }

    @Operation(summary = "Get all orders of a customer")
    @GetMapping("/get/customer/{customerId}")
    public ResponseEntity<?> getAllOrdersOfCustomer(@PathVariable Long customerId) {
        final var ordersList = orderService.getAllOrdersOfCustomer(customerId);
        return ResponseEntity.ok(ordersList);
    }

    @Operation(summary = "Add new order")
    @PostMapping("/add")
    public ResponseEntity<?> addOrder(@RequestBody OrderAddCommand orderAddCommand) {
        logger.info("Received OrderAddCommand: {}", orderAddCommand);
        try {
            Order createdOrder = orderService.addOrder(orderAddCommand);
            logger.info("Added new order: {}", createdOrder);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (TransactionSystemException e) {
            Throwable cause = e.getRootCause();
            if (cause instanceof ConstraintViolationException) {
                logger.error("Validation error: {}", cause.getMessage());
                return ResponseEntity.badRequest().body("Niepoprawne dane zamówienia");
            }
        } catch (Exception e) {
            logger.error("Error while adding order", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Błąd podczas dodawania zamówienia");
        }
        return ResponseEntity.badRequest().body("Unknown error");
    }

    @Operation(summary = "Update order")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable Long id, @RequestBody OrderAddCommand orderAddCommand) {
        logger.info("Received OrderUpdateCommand: {}", orderAddCommand);

        try {
            Order updatedOrder = orderService.updateOrder(id, orderAddCommand);
            logger.info("Updated order: {}", updatedOrder);
            return ResponseEntity.ok(updatedOrder);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
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
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        logger.info("Deleted order with id: {}", id);
        return ResponseEntity.ok("Usunięto zamówienie");
    }
}
