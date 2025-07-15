package com.solayof.schoolinventorymanagement.dtos;

import java.util.UUID;

import com.solayof.schoolinventorymanagement.entity.Item;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ItemDTO {
    private UUID id; // Unique identifier for the item, typically a UUID
    @NotNull(message = "Name cannot be null")
    private String name;
    @NotNull(message = "Description cannot be null")
    @Size(max = 500, message = "Description must be at most 500 characters")
    @Size(min = 1, message = "Description must be at least 1 character long")
    private String description;
    @NotNull(message = "Serial number cannot be null")
    private String serialNumber;
    @NotNull(message = "Category ID cannot be null")
    // @Pattern(regexp = "^[a-fA-F0-9-]{36}$", message = "Category ID must be a valid UUID format")
    // @Size(min = 36, max = 36, message = "Category ID must be exactly 36 characters long")
    private UUID categoryId;
    @NotNull(message = "Status cannot be null")
    @Size(max = 10, message = "Status must be at most 10 characters")
    private String status; // Status of the item, e.g., "AVAILABLE", "ASSIGNED", "RETURNED"

    public ItemDTO() {
    }

    public ItemDTO(String name, String description, String serialNumber, String status, UUID categoryId) {
        this.name = name;
        this.description = description;
        this.serialNumber = serialNumber;
        this.status = status;
        this.categoryId = categoryId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public static ItemDTO fromItem(Item item) {
        ItemDTO dto = new ItemDTO();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setSerialNumber(item.getSerialNumber());
        dto.setCategoryId(item.getCategory().getId());
        dto.setStatus(item.getStatus().name());
        return dto;
    }
}
