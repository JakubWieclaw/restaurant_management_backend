package com.example.restaurant_management_backend.services;

import com.example.restaurant_management_backend.exceptions.NotFoundException;
import com.example.restaurant_management_backend.jpa.model.*;
import com.example.restaurant_management_backend.jpa.model.command.OrderAddCommand;
import com.example.restaurant_management_backend.jpa.repositories.CustomerRepository;
import com.example.restaurant_management_backend.jpa.repositories.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    @Mock
    private MealService mealService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ConfigService configService;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetOrders_ShouldReturnListOfOrders() {

        Order order1 = new Order();
        Order order2 = new Order();
        when(orderRepository.findAll()).thenReturn(Arrays.asList(order1, order2));

        List<Order> result = orderService.getOrders();

        assertThat(result).containsExactly(order1, order2);
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    public void testGetOrderById_ShouldReturnOrder_WhenOrderExists() {

        Order order = new Order();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Optional<Order> result = orderService.getOrderById(1L);

        assertThat(result).isPresent().contains(order);
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetOrderById_ShouldThrowNotFoundException_WhenOrderDoesNotExist() {

        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrderById(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Nie znaleziono zamówienia");
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetAllOrdersOfCustomer_ShouldReturnOrders_WhenCustomerExists() {

        Long customerId = 1L;
        Order order = new Order();
        when(customerRepository.existsById(customerId)).thenReturn(true);
        when(orderRepository.findByCustomerId(customerId)).thenReturn(Collections.singletonList(order));

        List<Order> result = orderService.getAllOrdersOfCustomer(customerId);

        assertThat(result).containsExactly(order);
        verify(orderRepository, times(1)).findByCustomerId(customerId);
    }

    @Test
    public void testGetAllOrdersOfCustomer_ShouldThrowNotFoundException_WhenCustomerDoesNotExist() {

        Long customerId = 1L;
        when(customerRepository.existsById(customerId)).thenReturn(false);

        assertThatThrownBy(() -> orderService.getAllOrdersOfCustomer(customerId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Klient o identyfikatorze " + customerId + " nie istnieje");
        verify(customerRepository, times(1)).existsById(customerId);
    }

    @Test
    public void testAddOrder_ShouldSaveOrderSuccessfully() {

        MealQuantity mealQuantity = new MealQuantity(1L, 2);
        OrderAddCommand command = new OrderAddCommand(Collections.singletonList(mealQuantity), 1L, OrderType.DOSTAWA,
                OrderStatus.OCZEKUJĄCE, null, "Some Address", 5.0, null);
        Meal meal = new Meal("Meal", 20.0, null, Collections.emptyList(), 0.5, UnitType.GRAMY, 1L,
                Collections.emptyList(), 100);
        when(mealService.mealExists(1L)).thenReturn(true);
        when(mealService.getMealById(1L)).thenReturn(meal);
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);
        when(configService.isSystemInitialized()).thenReturn(true);
        DeliveryPricing deliveryPricing = new DeliveryPricing();
        deliveryPricing.setId(1L);
        deliveryPricing.setMaximumRange(5);
        deliveryPricing.setPrice(5.0);
        when(configService.getDeliveryPrices()).thenReturn(Collections.singletonList(deliveryPricing));

        Order result = orderService.addOrder(command);

        assertThat(result.getOrderPrice()).isEqualTo(40.0); // 20.0 * 2 = 40.0
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    public void testDeleteOrder_ShouldDeleteOrderSuccessfully() {

        Long orderId = 1L;
        when(orderRepository.existsById(orderId)).thenReturn(true);

        orderService.deleteOrder(orderId);

        verify(orderRepository, times(1)).deleteById(orderId);
    }

    @Test
    public void testDeleteOrder_ShouldThrowNotFoundException_WhenOrderDoesNotExist() {

        Long orderId = 1L;
        when(orderRepository.existsById(orderId)).thenReturn(false);

        assertThatThrownBy(() -> orderService.deleteOrder(orderId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Nie znaleziono zamówienia");
        verify(orderRepository, never()).deleteById(orderId);
    }
}
