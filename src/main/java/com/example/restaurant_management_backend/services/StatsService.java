package com.example.restaurant_management_backend.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.restaurant_management_backend.jpa.model.MealQuantity;
import com.example.restaurant_management_backend.jpa.model.Order;
import com.example.restaurant_management_backend.exceptions.IllegalStateException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final MealService mealService;

    private final OrderService orderService;

    private static final String NO_ORDERS_FOUND = "Nie znaleziono żadnych zamówień";

    public LinkedHashMap<String, Integer> getNMostPopularMeals(String mostLeast, int n) {

        if (!mostLeast.equals("most") && !mostLeast.equals("least")) {
            throw new IllegalArgumentException("mostLeast musi mieć wartość 'most' or 'least'");
        }

        if (n <= 0) {
            throw new IllegalArgumentException("n musi być większe od 0");
        }

        if (n > mealService.getAllMeals().size()) {
            throw new IllegalArgumentException("n musi być mniejsze lub równe liczbie wszystkich posiłków");
        }

        // get all orders from the database
        List<Order> orders = orderService.getOrders();

        if (orders.isEmpty()) {
            throw new IllegalStateException(NO_ORDERS_FOUND);
        }

        // iterate through list of orders and get all meals mentioned in mealIds list
        // (along with their quantity)
        // sum up the quantities of each meal
        // sort the list of meals by their total quantity
        // return the first n meals from the list

        // Create a dictionary to store the quantity of each meal
        // Iterate through the list of orders

        final var result = new HashMap<String, Integer>();
        // Create key for every meal so that we can show meals with 0 quantity
        for (var meal : mealService.getAllMeals()) {
            final var mealName = meal.getName();
            result.put(mealName, 0);
        }
        for (Order order : orders) {
            final var mealIds = order.getMealIds();
            for (MealQuantity mealQuantity1 : mealIds) {
                final var mealId = mealQuantity1.getMealId();
                final var mealName = mealService.getMealById(mealId).getName();
                final var quantity = mealQuantity1.getQuantity();
                result.put(mealName, result.get(mealName) + quantity);
            }
        }

        // Sort the map by values (either ascending or descending) and collect the first
        // `n` results
        LinkedHashMap<String, Integer> sortedMeals = result.entrySet().stream()
                .sorted((mostLeast.equals("most"))
                        ? Map.Entry.<String, Integer>comparingByValue().reversed() // For "most", sort descending
                        : Map.Entry.comparingByValue()) // For "least", sort ascending
                .limit(n)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1, // Merge function, not needed but required by toMap
                        LinkedHashMap::new // Ensure insertion order is maintained
                ));

        return sortedMeals;
    }

    public HashMap<String, Integer> getAmountOfOrdersByDayAndHour() {
        // get all orders from the database
        List<Order> orders = orderService.getOrders();

        if (orders.isEmpty()) {
            throw new IllegalStateException(NO_ORDERS_FOUND);
        }

        // Create a dictionary to store the amount of orders by day and hour
        HashMap<String, Integer> ordersByDayAndHour = new HashMap<>();

        for (Order order : orders) {
            final var orderDateTime = order.getDateTime();
            final var day = intDayToString(orderDateTime.getDayOfWeek().getValue());
            final var hour = orderDateTime.getHour();

            final var key = day + " " + hour + ":00";

            // if key does not exist, add it to the dictionary
            if (!ordersByDayAndHour.containsKey(key)) {
                ordersByDayAndHour.put(key, 1);
            } else {
                // if key exists, increment the value by 1
                ordersByDayAndHour.put(key, ordersByDayAndHour.get(key) + 1);
            }

        }

        return ordersByDayAndHour;
    }

    public HashMap<String, Double> getEarningsByYearMonth() {
        // get all orders from the database
        List<Order> orders = orderService.getOrders();

        // if orders are empty, throw IllegalState exception
        if (orders.isEmpty()) {
            throw new IllegalStateException(NO_ORDERS_FOUND);
        }

        // Create a dictionary to store the earnings by year and month
        HashMap<String, Double> earningsByYearMonth = new HashMap<>();

        for (Order order : orders) {
            final var orderDateTime = order.getDateTime();
            final var year = orderDateTime.getYear();
            final var month = orderDateTime.getMonth().toString();

            // Use a single string as the key in "Year-Month" format to simplify key
            // handling
            final var key = year + "-" + engMonthToPolMonth(month);

            var price = order.getOrderPrice(); // Assuming there's a getPrice method in the Order class

            // Increment the total price for the year-month key
            earningsByYearMonth.put(key, earningsByYearMonth.getOrDefault(key, 0.0) + price);
        }

        return earningsByYearMonth;
    }

    private String intDayToString(int day) {
        switch (day) {
            case 1:
                return "Poniedziałek";
            case 2:
                return "Wtorek";
            case 3:
                return "Środa";
            case 4:
                return "Czwartek";
            case 5:
                return "Piątek";
            case 6:
                return "Sobota";
            case 7:
                return "Niedziela";
            default:
                return "Niepoprawny dzień";
        }
    }

    private String engMonthToPolMonth(String month) {
        switch (month) {
            case "JANUARY":
                return "Styczeń";
            case "FEBRUARY":
                return "Luty";
            case "MARCH":
                return "Marzec";
            case "APRIL":
                return "Kwiecień";
            case "MAY":
                return "Maj";
            case "JUNE":
                return "Czerwiec";
            case "JULY":
                return "Lipiec";
            case "AUGUST":
                return "Sierpień";
            case "SEPTEMBER":
                return "Wrzesień";
            case "OCTOBER":
                return "Październik";
            case "NOVEMBER":
                return "Listopad";
            case "DECEMBER":
                return "Grudzień";
            default:
                return "Niepoprawny miesiąc";
        }
    }

}
