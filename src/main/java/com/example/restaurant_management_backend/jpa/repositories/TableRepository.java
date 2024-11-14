package com.example.restaurant_management_backend.jpa.repositories;

import com.example.restaurant_management_backend.jpa.model.Table;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TableRepository extends JpaRepository<Table, String> {
    int countAllByCapacityGreaterThanEqual(int capacity);

    List<Table> findAllByCapacityGreaterThanEqual(int capacity);
}
