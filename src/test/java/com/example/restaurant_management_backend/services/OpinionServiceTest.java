package com.example.restaurant_management_backend.services;

import com.example.restaurant_management_backend.dto.AverageRatingResponseDTO;
import com.example.restaurant_management_backend.dto.OpinionResponseDTO;
import com.example.restaurant_management_backend.exceptions.NotFoundException;
import com.example.restaurant_management_backend.exceptions.ResourceConflictException;
import com.example.restaurant_management_backend.jpa.model.Customer;
import com.example.restaurant_management_backend.jpa.model.Meal;
import com.example.restaurant_management_backend.jpa.model.Opinion;
import com.example.restaurant_management_backend.jpa.model.command.OpinionAddCommand;
import com.example.restaurant_management_backend.jpa.repositories.OpinionRepository;
import com.example.restaurant_management_backend.mappers.OpinionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OpinionServiceTest {

    @InjectMocks
    private OpinionService opinionService;

    @Mock
    private OpinionRepository opinionRepository;

    @Mock
    private MealService mealService;

    @Mock
    private CustomerUserDetailsService customerService;

    @Mock
    private OpinionMapper opinionMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test for addOpinion - valid case
    @Test
    void addOpinion_whenValid_shouldSaveOpinion() {
        // Given
        Long customerId = 1L;
        Long mealId = 1L;
        OpinionAddCommand opinionAddCommand = createMockOpinionAddCommand(customerId, mealId);

        when(customerService.getCustomerById(customerId)).thenReturn(Optional.of(new Customer()));
        when(opinionRepository.existsByMealIdAndCustomerId(mealId, customerId)).thenReturn(false);
        when(mealService.getMealById(mealId)).thenReturn(new Meal());
        when(opinionMapper.mapToDto(any(Opinion.class))).thenReturn(new OpinionResponseDTO(1L, 0, null));

        // When
        OpinionResponseDTO result = opinionService.addOpinion(opinionAddCommand);

        // Then
        verify(opinionRepository, times(1)).save(any(Opinion.class));
        assertNotNull(result);
    }

    // Test for addOpinion - opinion already exists
    @Test
    void addOpinion_whenOpinionAlreadyExists_shouldThrowResourceConflictException() {
        // Given
        Long customerId = 1L;
        Long mealId = 1L;
        OpinionAddCommand opinionAddCommand = createMockOpinionAddCommand(customerId, mealId);

        when(customerService.getCustomerById(customerId)).thenReturn(Optional.of(new Customer()));
        when(opinionRepository.existsByMealIdAndCustomerId(mealId, customerId)).thenReturn(true);

        // When & Then
        assertThrows(ResourceConflictException.class, () -> opinionService.addOpinion(opinionAddCommand));
        verify(opinionRepository, never()).save(any(Opinion.class));
    }

    // Test for addOpinion - customer not found
    @Test
    void addOpinion_whenCustomerNotFound_shouldThrowNotFoundException() {
        // Given
        Long customerId = 1L;
        OpinionAddCommand opinionAddCommand = createMockOpinionAddCommand(customerId, 1L);

        when(customerService.getCustomerById(customerId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> opinionService.addOpinion(opinionAddCommand));
        verify(opinionRepository, never()).save(any(Opinion.class));
    }

    // Test for getAverageRating - valid case
    @Test
    void getAverageRating_whenOpinionsExist_shouldReturnCorrectAverage() {
        // Given
        Long mealId = 1L;
        List<Opinion> opinions = List.of(
                createMockOpinion(5),
                createMockOpinion(3),
                createMockOpinion(4)
        );

        when(opinionRepository.findByMealId(mealId)).thenReturn(opinions);

        // When
        AverageRatingResponseDTO result = opinionService.getAverageRating(mealId);

        // Then
        assertEquals(4.0, result.averageRating());
        assertEquals(3, result.numberOfOpinions());
    }

    // Test for getAverageRating - no opinions
    @Test
    void getAverageRating_whenNoOpinionsExist_shouldReturnZero() {
        // Given
        Long mealId = 1L;
        when(opinionRepository.findByMealId(mealId)).thenReturn(List.of());

        // When
        AverageRatingResponseDTO result = opinionService.getAverageRating(mealId);

        // Then
        assertNull(result.averageRating());
        assertEquals(0, result.numberOfOpinions());
    }

    // Test for getOpinionsForCustomer - valid case
    @Test
    void getOpinionsForCustomer_whenValid_shouldReturnOpinionList() {
        // Given
        Long customerId = 1L;
        List<Opinion> opinions = List.of(createMockOpinion(5), createMockOpinion(3));
        when(customerService.getCustomerById(customerId)).thenReturn(Optional.of(new Customer()));
        when(opinionRepository.findByCustomerId(customerId)).thenReturn(opinions);
        when(opinionMapper.mapToDto(any(Opinion.class))).thenReturn(new OpinionResponseDTO(1L, 4, null));

        // When
        List<OpinionResponseDTO> result = opinionService.getOpinionsForCustomer(customerId);

        // Then
        assertEquals(2, result.size());
    }

    // Test for getOpinionsForMeal - valid case
    @Test
    void getOpinionsForMeal_whenValid_shouldReturnOpinionList() {
        // Given
        Long mealId = 1L;
        List<Opinion> opinions = List.of(createMockOpinion(5), createMockOpinion(4));
        when(opinionRepository.findByMealId(mealId)).thenReturn(opinions);
        when(opinionMapper.mapToDto(any(Opinion.class))).thenReturn(new OpinionResponseDTO(1L, 4, null));

        // When
        List<OpinionResponseDTO> result = opinionService.getOpinionsForMeal(mealId);

        // Then
        assertEquals(2, result.size());
    }

    // Test for updateOpinion - valid case
    @Test
    void updateOpinion_whenValid_shouldUpdateOpinion() {
        // Given
        Long customerId = 1L;
        Long mealId = 1L;
        OpinionAddCommand opinionAddCommand = createMockOpinionAddCommand(customerId, mealId);
        Opinion opinion = createMockOpinion(5);

        when(customerService.getCustomerById(customerId)).thenReturn(Optional.of(new Customer()));
        when(opinionRepository.findByMealIdAndCustomerId(mealId, customerId)).thenReturn(Optional.of(opinion));
        when(opinionMapper.mapToDto(opinion)).thenReturn(new OpinionResponseDTO(1L, 4, null));

        // When
        OpinionResponseDTO result = opinionService.updateOpinion(opinionAddCommand);

        // Then
        assertNotNull(result);
        verify(opinionRepository, times(1)).save(opinion);
    }

    // Test for updateOpinion - opinion not found
    @Test
    void updateOpinion_whenOpinionNotFound_shouldThrowNotFoundException() {
        // Given
        Long customerId = 1L;
        Long mealId = 1L;
        OpinionAddCommand opinionAddCommand = createMockOpinionAddCommand(customerId, mealId);

        when(customerService.getCustomerById(customerId)).thenReturn(Optional.of(new Customer()));
        when(opinionRepository.findByMealIdAndCustomerId(mealId, customerId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> opinionService.updateOpinion(opinionAddCommand));
        verify(opinionRepository, never()).save(any(Opinion.class));
    }

    // Helper method to create a mock OpinionAddCommand
    private OpinionAddCommand createMockOpinionAddCommand(Long customerId, Long mealId) {
        return new OpinionAddCommand(customerId, mealId, 5, "Great meal!");
    }

    // Helper method to create a mock Opinion
    private Opinion createMockOpinion(int rating) {
        Opinion opinion = new Opinion();
        opinion.setRating(rating);
        opinion.setComment("Sample comment");
        return opinion;
    }
}
