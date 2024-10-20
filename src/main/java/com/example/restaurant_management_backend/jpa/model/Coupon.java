package com.example.restaurant_management_backend.jpa.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Kod kuponu nie może być pusty")
    private String code; // e.g. POZNAN20

    @NotNull(message = "Procent zniżki nie może być pusty")
    private Double discountPercentage; // e.g., 20.0 for 20% discount

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private Customer customer; // The customer to whom the coupon applies

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "meal_id", referencedColumnName = "id")
    private Meal meal; // The meal that the coupon can be applied to

    @Future(message = "Data wygaśnięcia musi być w przyszłości")
    private LocalDateTime expiryDate; // Expiry date for the coupon

    @NotNull(message = "Kupon musi być aktywny lub nieaktywny")
    private Boolean active; // Indicates if the coupon is currently active

    private boolean availableToAllCustomers; // Indicates if the coupon is available to all customers
}
