package com.example.restaurant_management_backend.controllers;

import com.example.restaurant_management_backend.services.TableService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/tables")
public class TableController {
    private final TableService tableService;

    @PostMapping("/save")
    public void save(@RequestBody test te1) {
        tableService.save(te1.id(), te1.capacity());
    }

    public record test(String id, int capacity) {
    }
}
