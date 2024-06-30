package com.example.restaurant_management_backend.jpa.model;

import jakarta.persistence.*;

import java.time.ZonedDateTime;
import java.util.List;

@Entity(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private ZonedDateTime orderDate;

    @ManyToMany
    @JoinTable(
            name = "order_meal",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "meal_id")
    )
    private List<Meal> meals;

    @OneToOne
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @ManyToOne
    @JoinColumn(name = "seating_id")
    private Seating seating;

    public Order(Long id, ZonedDateTime orderDate, List<Meal> meals, Delivery delivery, Customer customer, Payment payment, Seating seating) {
        this.id = id;
        this.orderDate = orderDate;
        this.meals = meals;
        this.delivery = delivery;
        this.customer = customer;
        this.payment = payment;
        this.seating = seating;
    }

    public Order() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(ZonedDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public List<Meal> getMeals() {
        return meals;
    }

    public void setMeals(List<Meal> meals) {
        this.meals = meals;
    }

    public Delivery getDelivery() {
        return delivery;
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Seating getTable() {
        return seating;
    }

    public void setTable(Seating seating) {
        this.seating = seating;
    }
}

