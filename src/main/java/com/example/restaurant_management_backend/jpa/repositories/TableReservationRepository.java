package com.example.restaurant_management_backend.jpa.repositories;

import com.example.restaurant_management_backend.jpa.model.TableReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TableReservationRepository extends JpaRepository<TableReservation, Long> {
    List<TableReservation> findAllByReservationDate(LocalDate date);
}
