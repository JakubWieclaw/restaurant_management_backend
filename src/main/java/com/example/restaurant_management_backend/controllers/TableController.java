package com.example.restaurant_management_backend.controllers;

import com.example.restaurant_management_backend.jpa.model.Table;
import com.example.restaurant_management_backend.jpa.model.command.TableAddCommand;
import com.example.restaurant_management_backend.services.TableService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/tables")
@Validated
public class TableController {

    private final TableService tableService;
    private static final Logger logger = LoggerFactory.getLogger(TableController.class);

    @Operation(summary = "Save table")
    @PostMapping("/save")
    public ResponseEntity<Table> save(@RequestBody TableAddCommand request) {
        Table savedTable = tableService.save(request.getId(), request.getCapacity());
        logger.info("Table with id: {} saved", request.getId());
        return ResponseEntity.ok(savedTable);
    }

    @Operation(summary = "Get all tables")
    @GetMapping("/all")
    public ResponseEntity<List<Table>> getAllTables() {
        List<Table> tables = tableService.getAllTables();
        logger.info("Getting all tables");
        return ResponseEntity.ok(tables);
    }

    @Operation(summary = "Get table by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Table> getTableById(@PathVariable String id) {
        Table table = tableService.getTableById(id);
        logger.info("Getting table with id: {}", id);
        return ResponseEntity.ok(table);
    }

    @Operation(summary = "Update table")
    @PutMapping
    public ResponseEntity<Table> updateTable(@RequestBody TableAddCommand request) {
        Table updatedTable = tableService.updateTable(request.getId(), request.getCapacity());
        logger.info("Table with id: {} updated", request.getId());
        return ResponseEntity.ok(updatedTable);
    }

    @Operation(summary = "Delete table")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTable(@PathVariable String id) {
        tableService.deleteTable(id);
        logger.info("Table with id: {} deleted", id);
        return ResponseEntity.noContent().build();
    }

}
