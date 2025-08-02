package com.solayof.schoolinventorymanagement.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.solayof.schoolinventorymanagement.entity.Category;
import com.solayof.schoolinventorymanagement.exceptions.CategoryNotFoundException;
import com.solayof.schoolinventorymanagement.repository.CategoryRepository;

import jakarta.transaction.Transactional;

/**
 * Service class for managing categories in the school inventory management system.
 * Provides methods to find and save categories.
 */
@Service
public class CategoryService {
    @Autowired // Using Spring's @Autowired to inject the CategoryRepository
    private CategoryRepository categoryRepository; // Injecting the CategoryRepository to interact with the database

    /**
     * Finds a category by its ID.
     *
     * @param categoryId the ID of the category to find
     * @return the found Category entity
     * @throws CategoryNotFoundException if no category is found with the given ID
     */
    public Category findByCategoryId(UUID categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + categoryId));
    }

    /**
     * Checks if a category exists by its ID.
     * 
     * @param categoryId the ID of the category to check
     * @return true if a category with the given ID exists, false otherwise
     */
    public boolean existsById(UUID categoryId) {
        return categoryRepository.existsById(categoryId);
    }
    
    /**
     * Finds a category by its name.
     *
     * @param name the name of the category to find
     * @return the found Category entity
     * @throws CategoryNotFoundException if no category is found with the given name
     */
    public Category findByName(String name) {
        return categoryRepository.findByName(name)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with name: " + name));
    }
    /**
     * Saves a category to the repository.
     *
     * @param category the Category entity to save
     * @return the saved Category entity
     */
    @Transactional // Using @Transactional to ensure the operation is atomic
    public Category saveCategory(Category category) {
        Optional<Category> existingCategory = categoryRepository.findByName(category.getName());
        if (existingCategory.isPresent() && !existingCategory.get().getId().equals(category.getId())) {
            throw new IllegalArgumentException("Category with name '" + category.getName() + "' already exists.");
        }
        return categoryRepository.save(category);
    }

    /**
     * findallCategories
     * Retrieves all categories from the repository.
     */
    public List<Category> findAllCategories() {
        return categoryRepository.findAll();
    }

    /**
     * Deletes a category by its ID.
     *
     * @param categoryId the ID of the category to delete
     * @throws CategoryNotFoundException if no category is found with the given ID
     */
    public void deleteCategory(UUID categoryId) {
        Category category = findByCategoryId(categoryId);
        categoryRepository.delete(category);
    }

    /**
     * Finds categories by their name containing a specific string.
     *
     * @param name the string to search for in category names
     * @return a list of Category entities that contain the specified name
     */
    public List<Category> findByNameContainingIgnoreCase(String name) {
        return categoryRepository.findByNameContainingIgnoreCase(name);
    }
}
