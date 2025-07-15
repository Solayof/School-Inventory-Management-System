package com.solayof.schoolinventorymanagement.dtos;

import jakarta.validation.constraints.Pattern;

public class UpdateCollectorDTO {
    private String name; // Name of the collector
    private String contactInformation; // Contact information for the collector
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", 
             message = "Email must be a valid email format")
    private String email; // Email of the collector

    public UpdateCollectorDTO() {
    }

    public UpdateCollectorDTO(String name, String contactInformation, String email) {
        this.name = name;
        this.contactInformation = contactInformation;
        this.email = email;
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
}
