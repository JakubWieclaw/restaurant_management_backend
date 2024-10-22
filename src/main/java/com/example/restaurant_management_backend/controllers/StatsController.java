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

    @Operation(summary = "Get n most or less popular meals")
    @GetMapping("/popular/{mostLeast}/{n}")
    public ResponseEntity<?> getMostPopularMeals(@PathVariable String mostLeast, @PathVariable int n) {
        final var popularMeals = statsService.getNMostPopularMeals(mostLeast, n);
        logger.info("Getting {} most popular meals", n);
        return ResponseEntity.ok(popularMeals);
    }

    @Operation(summary = "Get amout of orders by day and hour")
    @GetMapping("/orders-by-day-hour")
    public ResponseEntity<?> getOrdersByDayAndHour() {
        final var ordersByDayAndHour = statsService.getAmountOfOrdersByDayAndHour();
        logger.info("Getting amount of orders by day and hour");
        return ResponseEntity.ok(ordersByDayAndHour);
    }

    @Operation(summary = "Get earnings by year-month")
    @GetMapping("/earnings-by-year-month")
    public ResponseEntity<?> getEarningsByYearMonth() {
        final var earningsByYearMonth = statsService.getEarningsByYearMonth();
        logger.info("Getting earnings by year-month");
        return ResponseEntity.ok(earningsByYearMonth);
    }

}
