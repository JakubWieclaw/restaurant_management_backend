package com.example.restaurant_management_backend.jpa.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Seating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "seating")
    private List<Order> orders;

    private Integer seatingNumber;

    public Seating(Long id, List<Order> orders, Integer seatingNumber) {
        this.id = id;
        this.orders = orders;
        this.seatingNumber = seatingNumber;
    }

    public Seating() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public Integer getSeatingNumber() {
        return seatingNumber;
    }

    public void setSeatingNumber(Integer seatingNumber) {
        this.seatingNumber = seatingNumber;
    }
}