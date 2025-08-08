package com.solayof.schoolinventorymanagement.dtos;

import java.util.UUID;

import com.solayof.schoolinventorymanagement.entity.Collector;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class CollectorDTO {
    private UUID id; // Unique identifier for the collector
    @NotNull(message = "Name cannot be null")
    private String name; // Name of the collector
    @NotNull(message = "Contact information cannot be null")
    private String contactInformation; // Contact information for the collector
    @NotNull(message = "Email cannot be null")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", 
             message = "Email must be a valid email format")
    private String email; // Email of the collector
    private String createdAt; // Creation timestamp of the collector
    private String updatedAt; // Last update timestamp of the collector

    public CollectorDTO() {
    }
    
    public CollectorDTO(String name, String contactInformation, String email) {
        this.name = name;
        this.contactInformation = contactInformation;
        this.email = email;
    }

    public CollectorDTO(UUID id, String name, String contactInformation, String email, String createdAt) {
        this.id = id;
        this.name = name;
        this.contactInformation = contactInformation;
        this.email = email;
        this.createdAt = createdAt;
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
    public String getContactInformation() {
        return contactInformation;
    }
    public void setContactInformation(String contactInformation) {
        this.contactInformation = contactInformation;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    public String getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public static CollectorDTO fromCollector(Collector collector) {
        CollectorDTO dto = new CollectorDTO();
        dto.setId(collector.getId());
        dto.setName(collector.getName());
        dto.setContactInformation(collector.getContactInformation());
        dto.setEmail(collector.getEmail());
        dto.setCreatedAt(collector.getCreatedAt().toString());
        dto.setUpdatedAt(collector.getUpdatedAt().toString());
        return dto;
    }
}
