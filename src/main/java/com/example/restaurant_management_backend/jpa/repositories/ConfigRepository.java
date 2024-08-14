package com.example.restaurant_management_backend.jpa.repositories;

import com.example.restaurant_management_backend.jpa.model.Config;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigRepository extends JpaRepository<Config, Long> {
}
