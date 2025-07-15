package com.solayof.schoolinventorymanagement.dtos;

import jakarta.validation.constraints.Size;

public class UpdateItemDTO {
    private String name;
    @Size(max = 500, message = "Description must be at most 500 characters")
    private String description;

    public UpdateItemDTO() {
    }
    public UpdateItemDTO(String name, String description) {
        this.name = name;
        this.description = description;
    }

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
}
