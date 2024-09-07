package com.example.restaurant_management_backend.services;

import java.util.List;
import java.util.Optional;

import com.example.restaurant_management_backend.jpa.model.Order;
import com.example.restaurant_management_backend.jpa.repositories.OrderRepository;

public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public Order addOrder(Order order) {
        return orderRepository.save(order);
    }

    public Order updateOrder(Long id, Order order) {
        Order existingOrder = orderRepository.findById(id).orElse(null);
        if (existingOrder == null) {
            return null;
        }
        existingOrder.setMealIds(order.getMealIds());
        existingOrder.setCustomerId(order.getCustomerId());
        existingOrder.setType(order.getType());
        existingOrder.setStatus(order.getStatus());
        existingOrder.setDateTime(order.getDateTime());
        return orderRepository.save(existingOrder);
    }
    
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
}
