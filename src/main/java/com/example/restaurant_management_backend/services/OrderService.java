package com.example.restaurant_management_backend.services;

import com.example.restaurant_management_backend.jpa.model.Order;
import com.example.restaurant_management_backend.jpa.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public void addOrder(Order order) {
        orderRepository.save(order);
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
