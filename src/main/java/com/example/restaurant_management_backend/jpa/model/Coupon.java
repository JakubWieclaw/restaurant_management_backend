package com.example.restaurant_management_backend.jpa.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.net.URI;
import java.util.List;

@Entity
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotBlank
    private String couponCode;

    @NotBlank
    private URI QRCode;

    @ManyToMany(mappedBy = "coupons")
    private List<Customer> customers;

    @OneToMany
    private List<Meal> meals;

    public Coupon(Long id, String couponCode, URI QRCode, List<Customer> customers, List<Meal> meals) {
        this.id = id;
        this.couponCode = couponCode;
        this.QRCode = QRCode;
        this.customers = customers;
        this.meals = meals;
    }

    public Coupon() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public URI getQRCode() {
        return QRCode;
    }

    public void setQRCode(URI QRCode) {
        this.QRCode = QRCode;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    public List<Meal> getMeals() {
        return meals;
    }

    public void setMeals(List<Meal> meals) {
        this.meals = meals;
    }
}