package com.example.restaurant_management_backend.jpa.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.restaurant_management_backend.jpa.model.Customer;
import com.example.restaurant_management_backend.jpa.model.UserToken;

@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, Long> {

    Customer findCustomerByTokenHash(String tokenHash);

    UserToken findByTokenHash(String tokenHash);

    @Query("SELECT ut FROM UserToken ut WHERE ut.salt = :salt")
    List<UserToken> findTokensBySalt(@Param("salt") String salt);
}
