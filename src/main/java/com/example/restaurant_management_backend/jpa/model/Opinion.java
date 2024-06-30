package com.example.restaurant_management_backend.jpa.model;

import jakarta.persistence.*;

import java.util.List;


@Entity
public class Opinion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(mappedBy = "opinions")
    private List<Meal> meals;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private Integer rating;

    public Opinion(Long id, List<Meal> meals, Customer customer, Integer rating) {
        this.id = id;
        this.meals = meals;
        this.customer = customer;
        this.rating = rating;
    }

    public Opinion() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Meal> getMeals() {
        return meals;
    }

    public void setMeals(List<Meal> meals) {
        this.meals = meals;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }
}