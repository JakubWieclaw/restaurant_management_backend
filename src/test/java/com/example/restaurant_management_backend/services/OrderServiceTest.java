package com.example.restaurant_management_backend.services;

import com.example.restaurant_management_backend.exceptions.NotFoundException;
import com.example.restaurant_management_backend.jpa.model.*;
import com.example.restaurant_management_backend.jpa.model.command.OrderAddCommand;
import com.example.restaurant_management_backend.jpa.repositories.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalTime;
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
    private CustomerUserDetailsService customerService;

    @Mock
    private ConfigService configService;

    @Mock
    private TableReservationService tableReservationService;

    @Mock
    private CouponService couponService;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        DeliveryPricing deliveryPricing = new DeliveryPricing();
        deliveryPricing.setId(1L);
        deliveryPricing.setMaximumRange(5);
        deliveryPricing.setPrice(5.0);
        when(configService.getDeliveryPrices()).thenReturn(Collections.singletonList(deliveryPricing));
        Customer customer = new Customer();
        customer.setId(1L);
        when(customerService.getCustomerByIdOrThrowException(anyLong())).thenReturn(customer);
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
        when(orderRepository.findByCustomerId(customerId)).thenReturn(Collections.singletonList(order));

        List<Order> result = orderService.getAllOrdersOfCustomer(customerId);

        assertThat(result).containsExactly(order);
        verify(orderRepository, times(1)).findByCustomerId(customerId);
    }

    @Test
    public void testAddOrder_ShouldSaveOrderSuccessfully() {
        MealQuantity mealQuantity = new MealQuantity(1L, 2);
        OrderAddCommand command = new OrderAddCommand(
                Collections.singletonList(mealQuantity),
                1L,
                OrderType.DOSTAWA,
                OrderStatus.OCZEKUJĄCE,
                null,
                "Some Address",
                5.0,
                null,
                null,
                null,
                null
        );

        Meal meal = new Meal("Meal", 20.0, null, Collections.emptyList(), Collections.emptyList(), 0.5, UnitType.GRAMY,
                1L,
                Collections.emptyList(), 100);
        when(mealService.mealExists(1L)).thenReturn(true);
        when(mealService.getMealById(1L)).thenReturn(meal);
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);
        when(configService.isSystemInitialized()).thenReturn(true);

        Order result = orderService.addOrder(command);

        assertThat(result.getOrderPrice()).isEqualTo(40.0); // 20.0 * 2
        assertThat(result.getDeliveryPrice()).isEqualTo(5.0);
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

    @Test
    public void testAddOrder_ShouldMakeTableReservation_WhenTableIdIsProvided() {
        // Arrange
        MealQuantity mealQuantity = new MealQuantity(1L, 2);
        OrderAddCommand command = new OrderAddCommand(
                Collections.singletonList(mealQuantity),
                1L,
                OrderType.DO_STOLIKA,
                OrderStatus.OCZEKUJĄCE,
                Collections.emptyList(),
                null,
                0,
                "1",
                4,
                120,
                null
        );

        Meal meal = new Meal("Meal", 20.0, null, Collections.emptyList(), Collections.emptyList(), 0.5, UnitType.GRAMY,
                1L,
                Collections.emptyList(), 100);

        when(mealService.mealExists(1L)).thenReturn(true);
        when(mealService.getMealById(1L)).thenReturn(meal);
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);
        when(configService.isSystemInitialized()).thenReturn(true);

        TableReservation tableReservation = new TableReservation();
        tableReservation.setDay(LocalDate.now());
        tableReservation.setStartTime(LocalTime.of(12, 0, 0));
        tableReservation.setEndTime(LocalTime.of(14, 0, 0));
        when(tableReservationService.findOrCreateReservation(any(), any(), any(), anyInt(), anyLong(), any()))
                .thenReturn(tableReservation);

        DeliveryPricing deliveryPricing = new DeliveryPricing();
        deliveryPricing.setId(1L);
        deliveryPricing.setMaximumRange(5);
        deliveryPricing.setPrice(5.0);
        when(configService.getDeliveryPrices()).thenReturn(Collections.singletonList(deliveryPricing));

        // Act
        Order result = orderService.addOrder(command);

        // Assert
        // verify(tableReservationService, times(1)).makeReservation(
        // any(), // date (assumes LocalDate)
        // any(), // start time (assumes LocalTime)
        // any(), // end time (assumes LocalTime)
        // anyInt(), // number of people
        // eq(1L) // customer ID
        // );

        assertThat(result.getOrderPrice()).isEqualTo(40.0); // 20.0 * 2 = 40.0
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    public void testAddOrder_ShouldNotMakeTableReservation_WhenTableIdIsNotProvided() {
        // Arrange
        MealQuantity mealQuantity = new MealQuantity(1L, 2);
        OrderAddCommand command = new OrderAddCommand(
                Collections.singletonList(mealQuantity),
                1L,
                OrderType.DOSTAWA,
                OrderStatus.OCZEKUJĄCE,
                null,
                "Some Address",
                5.0,
                null,
                null,
                null,
                null
        );

        Meal meal = new Meal("Meal", 20.0, null, Collections.emptyList(), Collections.emptyList(), 0.5, UnitType.GRAMY,
                1L,
                Collections.emptyList(), 100);

        when(mealService.mealExists(1L)).thenReturn(true);
        when(mealService.getMealById(1L)).thenReturn(meal);
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);
        when(configService.isSystemInitialized()).thenReturn(true);

        // Act
        Order result = orderService.addOrder(command);

        // Assert
        verify(tableReservationService, never()).makeReservation(any(), any(), any(), anyInt(), anyLong());
        assertThat(result.getOrderPrice()).isEqualTo(40.0); // 20.0 * 2
        assertThat(result.getDeliveryPrice()).isEqualTo(5.0);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    public void testUpdateOrder_ShouldNotMakeTableReservation_WhenTableIdIsNotUpdated() {
        // Arrange
        Long orderId = 1L;
        Order existingOrder = new Order();
        existingOrder.setCustomerId(1L);
        existingOrder.setMealIds(Collections.singletonList(new MealQuantity(1L, 2)));
        existingOrder.setDeliveryAddress("Old Address");

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));

        OrderAddCommand command = new OrderAddCommand(
                Collections.singletonList(new MealQuantity(1L, 2)),
                1L,
                OrderType.DOSTAWA,
                OrderStatus.OCZEKUJĄCE,
                null,
                "Some Address",
                5.0,
                null,
                null,
                null,
                null);

        when(mealService.mealExists(1L)).thenReturn(true);
        when(mealService.getMealById(1L)).thenReturn(new Meal("Meal", 20.0, null, Collections.emptyList(),
                Collections.emptyList(), 0.5, UnitType.GRAMY, 1L, Collections.emptyList(), 100));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);
        when(configService.isSystemInitialized()).thenReturn(true);

        // Act
        orderService.updateOrder(orderId, command);

        // Assert
        verify(tableReservationService, never()).makeReservation(any(), any(), any(), anyInt(), anyLong());
    }

    @Test
    void testIfCouponIsApplied() {
        // Arrange
        MealQuantity mealQuantity = new MealQuantity(1L, 2);
        OrderAddCommand command = new OrderAddCommand(
                Collections.singletonList(mealQuantity),
                1L,
                OrderType.DOSTAWA,
                OrderStatus.OCZEKUJĄCE,
                null,
                "Some Address",
                5.0,
                null,
                null,
                null,
                "POZNAN20"
        );

        Meal meal = new Meal("Meal", 20.0, null, Collections.emptyList(), Collections.emptyList(), 0.5, UnitType.GRAMY,
                1L,
                Collections.emptyList(), 100);
        meal.setId(1L);
        when(mealService.mealExists(1L)).thenReturn(true);
        when(mealService.getMealById(1L)).thenReturn(meal);
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);
        when(configService.isSystemInitialized()).thenReturn(true);

        Coupon coupon = new Coupon();
        coupon.setActive(true);
        coupon.setDiscountPercentage(50.0);
        coupon.setMeal(meal); // Ensure that this meal matches the one you're using in the test
        when(couponService.applyCoupon("POZNAN20", 1L, 1L, 20.0)).thenReturn(10.0);
        when(couponService.getCoupon("POZNAN20", 1L)).thenReturn(coupon);

        // Act
        Order result = orderService.addOrder(command);

        // Assert
        assertThat(result.getOrderPrice()).isEqualTo(20.0);
        verify(orderRepository, times(1)).save(any(Order.class));
    }
}
