package com.example.restaurant_management_backend.services;

import com.example.restaurant_management_backend.exceptions.NotFoundException;
import com.example.restaurant_management_backend.exceptions.ResourceConflictException;
import com.example.restaurant_management_backend.jpa.model.Category;
import com.example.restaurant_management_backend.jpa.model.command.CategoryAddCommand;
import com.example.restaurant_management_backend.jpa.repositories.CategoryRepository;
import com.example.restaurant_management_backend.jpa.repositories.MealRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private MealRepository mealRepository;

    @InjectMocks
    private CategoryService categoryService;

    private CategoryAddCommand categoryAddCommand;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        categoryAddCommand = new CategoryAddCommand("Test Category", "http://example.com/photo.jpg");
    }

    @Test
    void getAllCategories_ShouldReturnCategories() {
        // Arrange
        Category category = new Category("Test Category");
        when(categoryRepository.findAll()).thenReturn(List.of(category));

        // Act
        List<Category> result = categoryService.getAllCategories();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Category");
    }

    @Test
    void getCategoryById_ShouldReturnCategory_WhenExists() {
        // Arrange
        Long categoryId = 1L;
        Category category = new Category("Test Category");
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        // Act
        Category result = categoryService.getCategoryById(categoryId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Category");
    }

    @Test
    void getCategoryById_ShouldThrowNotFoundException_WhenNotFound() {
        // Arrange
        Long categoryId = 1L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> categoryService.getCategoryById(categoryId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Nie znaleziono kategorii o id 1");
    }

    @Test
    void addCategory_ShouldReturnSavedCategory() {
        // Arrange
        Category category = new Category("Test Category");
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        // Act
        Category result = categoryService.addCategory(categoryAddCommand);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Category");
    }

    @Test
    void updateCategory_ShouldReturnUpdatedCategory() {
        // Arrange
        Long categoryId = 1L;
        Category existingCategory = new Category("Old Category");
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(existingCategory);

        // Act
        Category updatedCategory = categoryService.updateCategory(categoryId, categoryAddCommand);

        // Assert
        assertThat(updatedCategory.getName()).isEqualTo("Test Category");
    }

    @Test
    void updateCategory_ShouldThrowNotFoundException_WhenCategoryNotFound() {
        // Arrange
        Long categoryId = 1L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> categoryService.updateCategory(categoryId, categoryAddCommand))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Nie znaleziono kategorii o id 1");
    }

    @Test
    void deleteCategoryById_ShouldDeleteCategory_WhenNoMealsAssociated() {
        // Arrange
        Long categoryId = 1L;
        when(mealRepository.existsByCategoryId(categoryId)).thenReturn(false);

        // Act
        categoryService.deleteCategoryById(categoryId);

        // Assert
        verify(categoryRepository, times(1)).deleteById(categoryId);
    }

    @Test
    void deleteCategoryById_ShouldThrowResourceConflictException_WhenMealsAreAssociated() {
        // Arrange
        Long categoryId = 1L;
        when(mealRepository.existsByCategoryId(categoryId)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> categoryService.deleteCategoryById(categoryId))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessageContaining("Nie można usunąć kategorii, ponieważ są z nią powiązane posiłki.");
    }
}
