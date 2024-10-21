package com.example.restaurant_management_backend.services;

import com.example.restaurant_management_backend.jpa.model.Table;
import com.example.restaurant_management_backend.jpa.repositories.TableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TableService {
    private final TableRepository tableRepository;

    public int countTablesWithGreaterOrEqualCapacity(int numberOfPeople) {
        return tableRepository.countAllByCapacityGreaterThanEqual(numberOfPeople);
    }

    public void save(String id, int capacity) {
        Table table = new Table();
        table.setId(id);
        table.setCapacity(capacity);
        tableRepository.save(table);
    }
}
