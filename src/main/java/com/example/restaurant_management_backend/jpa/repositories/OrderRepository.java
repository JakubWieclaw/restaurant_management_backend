package com.example.restaurant_management_backend.jpa.repositories;

import com.example.restaurant_management_backend.jpa.model.Order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Find orders by customer id
    List<Order> findByCustomerId(Long customerId);
}
