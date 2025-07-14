package com.solayof.schoolinventorymanagement.services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.solayof.schoolinventorymanagement.constants.CategoryName;
import com.solayof.schoolinventorymanagement.entity.Category;
import com.solayof.schoolinventorymanagement.exceptions.CategoryNotFoundException;
import com.solayof.schoolinventorymanagement.repository.CategoryRepository;

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
     * Finds a category by its name.
     *
     * @param name the name of the category to find
     * @return the found Category entity
     * @throws CategoryNotFoundException if no category is found with the given name
     */
    public Category findByName(CategoryName name) {
        return categoryRepository.findByName(name)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with name: " + name));
    }
    /**
     * Saves a category to the repository.
     *
     * @param category the Category entity to save
     * @return the saved Category entity
     */
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }
}
