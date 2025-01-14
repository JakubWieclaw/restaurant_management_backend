package com.example.restaurant_management_backend.services;

import com.example.restaurant_management_backend.dto.OpinionResponseDTO;
import com.example.restaurant_management_backend.exceptions.NoDataException;
import com.example.restaurant_management_backend.jpa.model.Meal;
import com.example.restaurant_management_backend.jpa.model.MealQuantity;
import com.example.restaurant_management_backend.jpa.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class StatsServiceTest {

    @Mock
    private MealService mealService;

    @Mock
    private OrderService orderService;

    @Mock
    private OpinionService opinionService;

    private StatsService statsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        statsService = new StatsService(mealService, orderService, opinionService);
    }

    @Test
    void testGetNMostPopularMeals_Most() {
        // Mock data
        Meal meal1 = new Meal();
        meal1.setName("Pizza");

        Meal meal2 = new Meal();
        meal2.setName("Pasta");

        MealQuantity mealQuantity1 = new MealQuantity();
        mealQuantity1.setMeal(meal1);
        mealQuantity1.setQuantity(2);

        MealQuantity mealQuantity2 = new MealQuantity();
        mealQuantity2.setMeal(meal2);
        mealQuantity2.setQuantity(3);

        Order order = new Order();
        order.setMealIds(List.of(mealQuantity1, mealQuantity2));
        order.setDateTime(LocalDateTime.now());

        when(orderService.getOrders()).thenReturn(List.of(order));
        when(mealService.getAllMeals()).thenReturn(List.of(meal1, meal2));

        LinkedHashMap<String, Integer> result = statsService.getNMostPopularMeals("most", 2);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(3, result.get("Pasta"));
        assertEquals(2, result.get("Pizza"));
    }

    @Test
    void testGetNMostPopularMeals_Least() {
        // Mock data
        Meal meal1 = new Meal();
        meal1.setName("Pizza");

        Meal meal2 = new Meal();
        meal2.setName("Pasta");

        MealQuantity mealQuantity1 = new MealQuantity();
        mealQuantity1.setMeal(meal1);
        mealQuantity1.setQuantity(2);

        MealQuantity mealQuantity2 = new MealQuantity();
        mealQuantity2.setMeal(meal2);
        mealQuantity2.setQuantity(3);

        Order order = new Order();
        order.setMealIds(List.of(mealQuantity1, mealQuantity2));
        order.setDateTime(LocalDateTime.now());

        when(orderService.getOrders()).thenReturn(List.of(order));
        when(mealService.getAllMeals()).thenReturn(List.of(meal1, meal2));

        LinkedHashMap<String, Integer> result = statsService.getNMostPopularMeals("least", 1);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(2, result.get("Pizza"));
    }

    @Test
    void testGetNMostPopularMeals_NoOrders() {
        // Mock orders to return an empty list
        when(orderService.getOrders()).thenReturn(Collections.emptyList());

        // Mock meals to avoid triggering IllegalArgumentException
        Meal mockMeal = new Meal();
        mockMeal.setName("MockMeal");
        when(mealService.getAllMeals()).thenReturn(Collections.singletonList(mockMeal));

        // Expect a NoDataException
        NoDataException exception = assertThrows(NoDataException.class, () -> {
            statsService.getNMostPopularMeals("most", 1);
        });

        assertEquals("Nie znaleziono żadnych zamówień", exception.getMessage());
    }

    @Test
    void testGetAmountOfOrdersByDayAndHour() {
        // Mock data
        Order order1 = new Order();
        order1.setDateTime(LocalDateTime.of(2023, 1, 1, 10, 0));

        Order order2 = new Order();
        order2.setDateTime(LocalDateTime.of(2023, 1, 1, 10, 0));

        Order order3 = new Order();
        order3.setDateTime(LocalDateTime.of(2023, 1, 1, 11, 0));

        when(orderService.getOrders()).thenReturn(List.of(order1, order2, order3));

        HashMap<String, Integer> result = statsService.getAmountOfOrdersByDayAndHour();

        assertNotNull(result);
        assertEquals(2, result.get("Niedziela 10:00"));
        assertEquals(1, result.get("Niedziela 11:00"));
    }

    @Test
    void testGetEarningsByYearMonth() {
        // Mock data
        Order order1 = new Order();
        order1.setDateTime(LocalDateTime.of(2023, 1, 1, 10, 0));
        order1.setOrderPrice(100.0);

        Order order2 = new Order();
        order2.setDateTime(LocalDateTime.of(2023, 1, 1, 11, 0));
        order2.setOrderPrice(150.0);

        Order order3 = new Order();
        order3.setDateTime(LocalDateTime.of(2023, 2, 1, 10, 0));
        order3.setOrderPrice(200.0);

        when(orderService.getOrders()).thenReturn(List.of(order1, order2, order3));

        HashMap<String, Double> result = statsService.getEarningsByYearMonth();

        assertNotNull(result);
        assertEquals(250.0, result.get("2023-Styczeń"));
        assertEquals(200.0, result.get("2023-Luty"));
    }

    @Test
    void testGetNBestOrWorstRatedMeals_Best() {
        // Mock data
        Meal meal1 = new Meal();
        meal1.setId(1L);
        meal1.setName("Pizza");

        Meal meal2 = new Meal();
        meal2.setId(2L);
        meal2.setName("Pasta");

        when(mealService.getAllMeals()).thenReturn(List.of(meal1, meal2));
        when(opinionService.getOpinionsForMeal(1L)).thenReturn(List.of(
                new OpinionResponseDTO(1L, 5, "Great meal!"),
                new OpinionResponseDTO(2L, 4, "Great meal!")
        ));

        when(opinionService.getOpinionsForMeal(2L)).thenReturn(List.of(
                new OpinionResponseDTO(2L, 3, "It was okay.")
        ));

        LinkedHashMap<String, Double> result = statsService.getNBestOrWorstRatedMeals("best", 1);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(4.5, result.get("Pizza"));
    }

    @Test
    void testGetNBestOrWorstRatedMeals_Worst() {
        // Mock data
        Meal meal1 = new Meal();
        meal1.setId(1L);
        meal1.setName("Pizza");

        Meal meal2 = new Meal();
        meal2.setId(2L);
        meal2.setName("Pasta");

        when(mealService.getAllMeals()).thenReturn(List.of(meal1, meal2));
        when(opinionService.getOpinionsForMeal(1L)).thenReturn(List.of(
                new OpinionResponseDTO(1L, 5, "Great meal!")
        ));

        when(opinionService.getOpinionsForMeal(2L)).thenReturn(List.of(
                new OpinionResponseDTO(2L, 3, "It was okay.")
        ));

        LinkedHashMap<String, Double> result = statsService.getNBestOrWorstRatedMeals("worst", 1);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(3.0, result.get("Pasta"));
    }

    @Test
    void testGetNBestOrWorstRatedMeals_NoOpinions() {
        Meal meal1 = new Meal();
        meal1.setId(1L);
        meal1.setName("Pizza");

        when(mealService.getAllMeals()).thenReturn(List.of(meal1));
        when(opinionService.getOpinionsForMeal(1L)).thenReturn(Collections.emptyList());

        LinkedHashMap<String, Double> result = statsService.getNBestOrWorstRatedMeals("best", 1);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
