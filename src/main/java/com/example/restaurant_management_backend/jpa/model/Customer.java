package com.example.restaurant_management_backend.jpa.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Imię nie może być puste")
    private String name;

    @NotBlank(message = "Nazwisko nie może być puste")
    private String surname;

    @NotBlank(message = "Email nie może być pusty")
    private String email;

    @NotBlank(message = "Numer telefonu nie może być pusty")
    private String phone;

    @NotBlank(message = "Hasło nie może być puste")
    private String password;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "privilege_id", referencedColumnName = "id")
    private Privilege privilege;

    // Fields for password reset
    private String resetToken;
    private LocalDateTime resetTokenExpiry;
}
