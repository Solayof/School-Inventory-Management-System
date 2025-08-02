package com.solayof.schoolinventorymanagement.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.solayof.schoolinventorymanagement.entity.Category;
import com.solayof.schoolinventorymanagement.exceptions.CategoryNotFoundException;
import com.solayof.schoolinventorymanagement.repository.CategoryRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository; // Mocked repository

    @InjectMocks
    private CategoryService categoryService; // Service under test with mocks injected

    private Category sampleCategory;
    private UUID categoryId;

    @BeforeEach
    void setUp() {
        // Create a sample category before each test
        categoryId = UUID.randomUUID();
        sampleCategory = new Category(categoryId, "Stationery", "Items like pens, books, etc.", new HashSet<>());
    }

    @Test
    void testFindByCategoryId_Success() {
        // Arrange: category is found by ID
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(sampleCategory));

        // Act
        Category result = categoryService.findByCategoryId(categoryId);

        // Assert
        assertNotNull(result);
        assertEquals("Stationery", result.getName());
    }

    @Test
    void testFindByCategoryId_NotFound() {
        // Arrange: category is not found
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act & Assert: expect exception
        assertThrows(CategoryNotFoundException.class, () -> categoryService.findByCategoryId(categoryId));
    }

    @Test
    void testExistsById_True() {
        // Simulate that a category with the given ID exists
        when(categoryRepository.existsById(categoryId)).thenReturn(true);

        assertTrue(categoryService.existsById(categoryId));
    }

    @Test
    void testExistsById_False() {
        // Simulate that no category exists with the given ID
        when(categoryRepository.existsById(categoryId)).thenReturn(false);

        assertFalse(categoryService.existsById(categoryId));
    }

    @Test
    void testFindByName_Success() {
        // Category found by name
        when(categoryRepository.findByName("Stationery")).thenReturn(Optional.of(sampleCategory));

        Category result = categoryService.findByName("Stationery");

        assertEquals("Stationery", result.getName());
    }

    @Test
    void testFindByName_NotFound() {
        // Category not found by name
        when(categoryRepository.findByName("Unknown")).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> categoryService.findByName("Unknown"));
    }

    @Test
    void testSaveCategory_NewCategory_Success() {
        // Saving a brand new category with a unique name
        when(categoryRepository.findByName(sampleCategory.getName())).thenReturn(Optional.empty());
        when(categoryRepository.save(sampleCategory)).thenReturn(sampleCategory);

        Category saved = categoryService.saveCategory(sampleCategory);

        assertNotNull(saved);
        assertEquals("Stationery", saved.getName());
    }

    @Test
    void testSaveCategory_UpdatingSameCategory_Success() {
        // Updating an existing category with the same name and same ID
        when(categoryRepository.findByName(sampleCategory.getName())).thenReturn(Optional.of(sampleCategory));
        when(categoryRepository.save(sampleCategory)).thenReturn(sampleCategory);

        Category saved = categoryService.saveCategory(sampleCategory);

        assertEquals(sampleCategory.getId(), saved.getId());
    }

    @Test
    void testSaveCategory_DuplicateNameDifferentId_ThrowsException() {
        // Another category exists with the same name but different ID → should throw error
        Category existing = new Category(UUID.randomUUID(), "Stationery", "Duplicate", new HashSet<>());
        when(categoryRepository.findByName("Stationery")).thenReturn(Optional.of(existing));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                categoryService.saveCategory(sampleCategory));

        assertTrue(ex.getMessage().contains("already exists"));
    }

    @Test
    void testFindAllCategories() {
        // Simulate 2 categories in DB
        List<Category> categories = List.of(
            sampleCategory,
            new Category(UUID.randomUUID(), "Books", "Library books", new HashSet<>())
        );
        when(categoryRepository.findAll()).thenReturn(categories);

        List<Category> result = categoryService.findAllCategories();

        assertEquals(2, result.size());
    }

    @Test
    void testDeleteCategory_Success() {
        // Simulate delete scenario: category is found then deleted
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(sampleCategory));
        doNothing().when(categoryRepository).delete(sampleCategory);

        assertDoesNotThrow(() -> categoryService.deleteCategory(categoryId));

        // Verify deletion was triggered
        verify(categoryRepository).delete(sampleCategory);
    }

    @Test
    void testDeleteCategory_NotFound() {
        // Trying to delete a category that does not exist
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> categoryService.deleteCategory(categoryId));
    }

    @Test
    void testFindByNameContainingIgnoreCase() {
        // Simulate partial name search (case insensitive)
        List<Category> results = List.of(
                new Category(UUID.randomUUID(), "Stationery", "Desc", new HashSet<>()),
                new Category(UUID.randomUUID(), "stationery items", "Desc", new HashSet<>())
        );

        when(categoryRepository.findByNameContainingIgnoreCase("station")).thenReturn(results);

        List<Category> found = categoryService.findByNameContainingIgnoreCase("station");

        assertEquals(2, found.size());
    }
}
