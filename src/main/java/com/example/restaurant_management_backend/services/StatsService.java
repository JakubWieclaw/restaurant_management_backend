package com.example.restaurant_management_backend.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.restaurant_management_backend.jpa.model.MealQuantity;
import com.example.restaurant_management_backend.jpa.model.Order;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final MealService mealService;
    
    private final OrderService orderService;

    public List<MealQuantity> getNMostPopularMeals(int n) {

        if (n <= 0) {
            throw new IllegalArgumentException("n must be greater than 0");
        }

        if (n > mealService.getAllMeals().size()) {
            throw new IllegalArgumentException("n must be less than or equal to the number of meals");
        }
        
        // get all orders from the database
        List<Order> orders = orderService.getOrders();

        // iterate through list of orders and get all meals mentioned in mealIds list (along with their quantity)
        // sum up the quantities of each meal
        // sort the list of meals by their total quantity
        // return the first n meals from the list

        // Create a dictionary to store the quantity of each meal
        // Iterate through the list of orders

        final var mealQuantity = new HashMap<Long, Integer>();
        for (Order order : orders) {
            final var mealIds = order.getMealIds();
            for (MealQuantity mealQuantity1 : mealIds) {
                final var mealId = mealQuantity1.getMealId();
                final var quantity = mealQuantity1.getQuantity();
                if (mealQuantity.containsKey(mealId)) {
                    mealQuantity.put(mealId, mealQuantity.get(mealId) + quantity);
                } else {
                    mealQuantity.put(mealId, quantity);
                }
                
            }
        }


        // return the first n meals from the list
        List<MealQuantity> mostPopularMeals = new ArrayList<>();
        mealQuantity.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(n)
                .forEach(e -> mostPopularMeals.add(new MealQuantity(e.getKey(), e.getValue())));

        return mostPopularMeals;
    }


}
