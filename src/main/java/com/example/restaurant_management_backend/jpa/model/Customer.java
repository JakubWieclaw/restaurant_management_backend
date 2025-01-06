package com.example.restaurant_management_backend.jpa.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "customer")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Imię nie może być puste")
    private String name;

    @NotBlank(message = "Nazwisko nie może być puste")
    private String surname;

    @NotBlank(message = "Email nie może być pusty")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Numer telefonu nie może być pusty")
    private String phone;

    @JsonIgnore
    @NotBlank(message = "Hasło nie może być puste")
    private String passwordHash;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "privilege_id", referencedColumnName = "id")
    private Privilege privilege;

    // Fields for password reset
    private String resetToken;
    private LocalDateTime resetTokenExpiry;

    // Updated relationship: One customer can have many reservations
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("customer")
    private List<TableReservation> tableReservations;
}
