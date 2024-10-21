package com.example.restaurant_management_backend.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.restaurant_management_backend.services.StatsService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/stats")
@Validated
@RequiredArgsConstructor
public class StatsController {

    private static final Logger logger = LoggerFactory.getLogger(StatsController.class);
    private final StatsService statsService;

    @Operation(summary = "Get n most popular meals")
    @GetMapping("/popular/{n}")
    public ResponseEntity<?> getMostPopularMeals(@PathVariable int n) {
        final var popularMeals = statsService.getNMostPopularMeals(n);
        logger.info("Getting {} most popular meals", n);
        return ResponseEntity.ok(popularMeals);
    }

}
