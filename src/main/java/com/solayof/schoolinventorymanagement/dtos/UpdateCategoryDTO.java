package com.solayof.schoolinventorymanagement.dtos;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UpdateCategoryDTO {
    @Size(max = 100, message = "Name must be between 1 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z0-9 ]+$", message = "Name can only contain alphanumeric characters and spaces")
    private String name; // Name of the category
    @Size(max = 500, message = "Description must be at most 500 characters")
    @Pattern(regexp = "^[a-zA-Z0-9 .,!?]+$", message = "Description can only contain alphanumeric characters, spaces, and punctuation (.,!?).")
    private String description; // Description of the category

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
    public UpdateCategoryDTO() {
    }
    public UpdateCategoryDTO(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
