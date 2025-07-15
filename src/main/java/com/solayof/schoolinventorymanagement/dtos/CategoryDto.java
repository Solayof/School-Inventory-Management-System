package com.solayof.schoolinventorymanagement.dtos;

import java.util.UUID;

import com.solayof.schoolinventorymanagement.entity.Category;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CategoryDto {
    /**
     * Represents a Data Transfer Object (DTO) for a category in the school inventory management system.
     * This DTO is used to transfer category data between different layers of the application.
     */
    private UUID id; // Unique identifier for the category, typically a UUID
    @NotNull(message = "Name cannot be null")
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z0-9 ]+$", message = "Name can only contain alphanumeric characters and spaces")
    private String name; // Name of the category
    @NotNull(message = "Description cannot be null")
    @Size(max = 500, message = "Description must be at most 500 characters")
    @Pattern(regexp = "^[a-zA-Z0-9 .,!?]+$", message = "Description can only contain alphanumeric characters, spaces, and punctuation (.,!?).")
    private String description; // Description of the category

    // Default constructor
    public CategoryDto() {
    }

    // Parameterized constructor
    public CategoryDto(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }

    public static CategoryDto fromCategory(Category category) {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        return dto;
    }
}
