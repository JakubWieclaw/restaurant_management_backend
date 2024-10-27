package com.example.restaurant_management_backend.services;

import com.example.restaurant_management_backend.exceptions.NotFoundException;
import com.example.restaurant_management_backend.jpa.model.Table;
import com.example.restaurant_management_backend.jpa.repositories.TableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TableService {
    public static final String NOT_FOUND_TABLE_WITH_ID = "Nie znaleziono stolika o id ";
    private final TableRepository tableRepository;

    public int countTablesWithGreaterOrEqualCapacity(int numberOfPeople) {
        return tableRepository.countAllByCapacityGreaterThanEqual(numberOfPeople);
    }

    public List<Table> findTablesWithGreaterOrEqualCapacity(int numberOfPeople) {
        return tableRepository.findAllByCapacityGreaterThanEqual(numberOfPeople);
    }

    public Table save(String id, int capacity) {
        Table table = new Table(id, capacity);
        return tableRepository.save(table);
    }

    public List<Table> getAllTables() {
        return tableRepository.findAll();
    }

    public Table getTableById(String id) {
        return tableRepository.findById(id).orElseThrow(() -> new NotFoundException(NOT_FOUND_TABLE_WITH_ID + id));
    }

    public Table updateTable(String id, int capacity) {
        Optional<Table> existingTable = tableRepository.findById(id);
        if (existingTable.isPresent()) {
            Table table = existingTable.get();
            table.setCapacity(capacity);
            return tableRepository.save(table);
        }
        throw new IllegalArgumentException("Table not found");
    }

    public void deleteTable(String id) {
        if (tableRepository.existsById(id)) {
            tableRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Table not found");
        }
    }
}

