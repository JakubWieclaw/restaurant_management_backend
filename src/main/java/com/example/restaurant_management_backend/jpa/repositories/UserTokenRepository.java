package com.example.restaurant_management_backend.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.restaurant_management_backend.jpa.model.Customer;
import com.example.restaurant_management_backend.jpa.model.UserToken;

@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, Long> {

    Customer findByTokenHash(String tokenHash);

}
