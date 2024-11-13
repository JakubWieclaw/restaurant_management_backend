package com.example.restaurant_management_backend.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.restaurant_management_backend.jpa.model.UnitType;
import com.example.restaurant_management_backend.jpa.model.command.MealAddCommand;
import com.example.restaurant_management_backend.jpa.repositories.CategoryRepository;
import com.example.restaurant_management_backend.jpa.repositories.MealRepository;
import com.example.restaurant_management_backend.mappers.MealMapper;
import com.example.restaurant_management_backend.exceptions.NotFoundException;
import com.example.restaurant_management_backend.jpa.model.Meal;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class MealServiceTest {

    @Mock
    private MealRepository mealRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private MealMapper mealMapper;

    @InjectMocks
    private MealService mealService;

    private Meal meal;
    private MealAddCommand mealAddCommand;

    @BeforeEach
    void setUp() {
        final var ingredientsList = List.of("Tomato", "Cheese");
        final var removableIngredientsList = List.of("Cheese");
        final var allergensList = List.of("Gluten");

        meal = new Meal();
        meal.setName("Pasta");
        meal.setPrice(12.5);
        meal.setPhotographUrl(null);
        meal.setIngredients(ingredientsList);
        meal.setRemovableIngredList(removableIngredientsList);
        meal.setWeightOrVolume(250.0);
        meal.setUnitType(UnitType.GRAMY);
        meal.setCategoryId(1L);
        meal.setAllergens(allergensList);
        meal.setCalories(350);

        mealAddCommand = new MealAddCommand("Pasta", 12.5, "http://image.url",
                ingredientsList, removableIngredientsList,
                250.0, UnitType.GRAMY, 1L, allergensList, 350);
    }

    @Test
    void shouldAddMealSuccessfully() {
        when(categoryRepository.existsById(anyLong())).thenReturn(true);
        when(mealMapper.toMeal(any(MealAddCommand.class))).thenReturn(meal);
        when(mealRepository.save(any(Meal.class))).thenReturn(meal);

        Meal savedMeal = mealService.addMeal(mealAddCommand);

        assertNotNull(savedMeal);
        assertEquals(meal.getName(), savedMeal.getName());
        verify(mealRepository, times(1)).save(any(Meal.class));
    }

    @Test
    void shouldThrowExceptionWhenAddingMealWithInvalidCategory() {
        when(categoryRepository.existsById(anyLong())).thenReturn(false);

        Exception exception = assertThrows(NotFoundException.class, () -> {
            mealService.addMeal(mealAddCommand);
        });

        assertTrue(exception.getMessage().contains("Kategoria o id"));
        verify(mealRepository, never()).save(any(Meal.class));
    }

    @Test
    void shouldUpdateMealSuccessfully() {
        when(mealRepository.findById(anyLong())).thenReturn(Optional.of(meal));
        when(categoryRepository.existsById(anyLong())).thenReturn(true);
        when(mealRepository.save(any(Meal.class))).thenReturn(meal);

        Meal updatedMeal = mealService.updateMeal(1L, mealAddCommand);

        assertNotNull(updatedMeal);
        assertEquals(meal.getName(), updatedMeal.getName());
        verify(mealRepository, times(1)).save(any(Meal.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentMeal() {
        when(mealRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> {
            mealService.updateMeal(1L, mealAddCommand);
        });

        assertTrue(exception.getMessage().contains("Nie znaleziono dania o id"));
    }

    @Test
    void shouldDeleteMealSuccessfully() {
        when(mealRepository.existsById(anyLong())).thenReturn(true);

        mealService.deleteMealById(1L);

        verify(mealRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentMeal() {
        when(mealRepository.existsById(anyLong())).thenReturn(false);

        Exception exception = assertThrows(NotFoundException.class, () -> {
            mealService.deleteMealById(1L);
        });

        assertTrue(exception.getMessage().contains("Nie znaleziono dania o id"));
    }

    @Test
    void shouldGetAllMeals() {
        when(mealRepository.findAll()).thenReturn(Collections.singletonList(meal));

        List<Meal> meals = mealService.getAllMeals();

        assertEquals(1, meals.size());
        assertEquals(meal.getName(), meals.get(0).getName());
        verify(mealRepository, times(1)).findAll();
    }

    @Test
    void shouldGetMealsByCategoryId() {
        when(categoryRepository.existsById(anyLong())).thenReturn(true);
        when(mealRepository.findByCategoryId(anyLong())).thenReturn(Collections.singletonList(meal));

        List<Meal> meals = mealService.getMealsByCategoryId(1L);

        assertEquals(1, meals.size());
        assertEquals(meal.getName(), meals.get(0).getName());
        verify(mealRepository, times(1)).findByCategoryId(anyLong());
    }

    @Test
    void shouldThrowExceptionWhenGettingMealsByInvalidCategoryId() {
        when(categoryRepository.existsById(anyLong())).thenReturn(false);

        Exception exception = assertThrows(NotFoundException.class, () -> {
            mealService.getMealsByCategoryId(1L);
        });

        assertTrue(exception.getMessage().contains("Kategoria o id"));
    }

    @Test
    public void testAddMeal_ShouldNotAdd_WhenRemovableIngredients_Contain_Ingredient_ThatDoesNotExist() throws Exception {
        Exception exception = assertThrows(NotFoundException.class, () -> {
            when(categoryRepository.existsById(anyLong())).thenReturn(true);

            final var mealAddCommand = new MealAddCommand(
                    "Pasta",
                    12.5,
                    null,
                    List.of("Pasta"),
                    List.of("Cheese"),
                    250.0,
                    UnitType.GRAMY,
                    1L,
                    Collections.emptyList(),
                    350);
            mealService.addMeal(mealAddCommand);
        });

        assertEquals("Nie wszystkie składniki możliwe do usunięcia są obecne w daniu", exception.getMessage());
    }


    @Test
    public void testUpdateMeal_ShouldNotAdd_WhenRemovableIngredients_Contain_Ingredient_ThatDoesNotExist() throws Exception {
        Exception exception = assertThrows(NotFoundException.class, () -> {
            when(categoryRepository.existsById(anyLong())).thenReturn(true);
            when(mealRepository.findById(anyLong())).thenReturn(Optional.of(meal));
            // when(mealService.getMealById(anyLong())).thenReturn(new Meal());

            final var mealAddCommand = new MealAddCommand(
                    "Pasta",
                    12.5,
                    null,
                    List.of("Pasta"),
                    List.of("Cheese"),
                    250.0,
                    UnitType.GRAMY,
                    1L,
                    Collections.emptyList(),
                    350);
            mealService.updateMeal(1L, mealAddCommand);
        });

        assertEquals("Nie wszystkie składniki możliwe do usunięcia są obecne w daniu", exception.getMessage());
    }

}
