package com.example.restaurant_management_backend.jpa.model;

import jakarta.persistence.*;

import java.time.ZonedDateTime;

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentMethod;

    private String status;

    private ZonedDateTime paymentTime;

    @OneToOne
    @JoinColumn(name = "orders_id")
    private Order order;

    public Payment(Long id, String paymentMethod, String status, ZonedDateTime paymentTime, Order order) {
        this.id = id;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.paymentTime = paymentTime;
        this.order = order;
    }

    public Payment() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ZonedDateTime getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(ZonedDateTime paymentTime) {
        this.paymentTime = paymentTime;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}