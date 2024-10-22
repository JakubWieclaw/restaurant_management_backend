package com.example.restaurant_management_backend.services;

import com.example.restaurant_management_backend.exceptions.NotFoundException;
import com.example.restaurant_management_backend.jpa.model.Table;
import com.example.restaurant_management_backend.jpa.repositories.TableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TableServiceTest {

    @InjectMocks
    private TableService tableService;

    @Mock
    private TableRepository tableRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void save_shouldCreateNewTable() {
        // Given
        String id = "table1";
        int capacity = 4;
        Table newTable = new Table(id, capacity);

        // When
        when(tableRepository.save(any(Table.class))).thenReturn(newTable);
        Table savedTable = tableService.save(id, capacity);

        // Then
        assertNotNull(savedTable);
        assertEquals(id, savedTable.getId());
        assertEquals(capacity, savedTable.getCapacity());
        verify(tableRepository, times(1)).save(any(Table.class));
    }

    @Test
    void getAllTables_shouldReturnListOfTables() {
        // Given
        List<Table> tables = Arrays.asList(
                new Table("table1", 4),
                new Table("table2", 6)
        );

        // When
        when(tableRepository.findAll()).thenReturn(tables);
        List<Table> result = tableService.getAllTables();

        // Then
        assertEquals(2, result.size());
        assertEquals("table1", result.getFirst().getId());
        assertEquals(4, result.getFirst().getCapacity());
        verify(tableRepository, times(1)).findAll();
    }

    @Test
    void getTableById_shouldReturnTableWhenExists() {
        // Given
        String id = "table1";
        Table table = new Table(id, 4);

        // When
        when(tableRepository.findById(id)).thenReturn(Optional.of(table));
        Table result = tableService.getTableById(id);

        // Then
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(4, result.getCapacity());
        verify(tableRepository, times(1)).findById(id);
    }

    @Test
    void getTableById_shouldReturnEmptyWhenTableDoesNotExist() {
        // Given
        String id = "table1";

        // When
        when(tableRepository.findById(id)).thenReturn(Optional.empty());

        // Then
        assertThrows(NotFoundException.class, () -> tableService.getTableById(id));
    }

    @Test
    void updateTable_shouldUpdateExistingTable() {
        // Given
        String id = "table1";
        int newCapacity = 6;
        Table existingTable = new Table(id, 4);

        // When
        when(tableRepository.findById(id)).thenReturn(Optional.of(existingTable));
        when(tableRepository.save(any(Table.class))).thenReturn(new Table(id, newCapacity));
        Table updatedTable = tableService.updateTable(id, newCapacity);

        // Then
        assertNotNull(updatedTable);
        assertEquals(id, updatedTable.getId());
        assertEquals(newCapacity, updatedTable.getCapacity());
        verify(tableRepository, times(1)).findById(id);
        verify(tableRepository, times(1)).save(any(Table.class));
    }

    @Test
    void updateTable_shouldThrowExceptionWhenTableDoesNotExist() {
        // Given
        String id = "table1";
        int newCapacity = 6;

        // When
        when(tableRepository.findById(id)).thenReturn(Optional.empty());

        // Then
        assertThrows(IllegalArgumentException.class, () -> tableService.updateTable(id, newCapacity));
        verify(tableRepository, times(1)).findById(id);
        verify(tableRepository, times(0)).save(any(Table.class));
    }

    @Test
    void deleteTable_shouldDeleteExistingTable() {
        // Given
        String id = "table1";

        // When
        when(tableRepository.existsById(id)).thenReturn(true);
        doNothing().when(tableRepository).deleteById(id);

        // Then
        tableService.deleteTable(id);
        verify(tableRepository, times(1)).existsById(id);
        verify(tableRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteTable_shouldThrowExceptionWhenTableDoesNotExist() {
        // Given
        String id = "table1";

        // When
        when(tableRepository.existsById(id)).thenReturn(false);

        // Then
        assertThrows(IllegalArgumentException.class, () -> tableService.deleteTable(id));
        verify(tableRepository, times(1)).existsById(id);
        verify(tableRepository, times(0)).deleteById(id);
    }
}
