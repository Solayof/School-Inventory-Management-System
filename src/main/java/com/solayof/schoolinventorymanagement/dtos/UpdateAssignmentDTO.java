package com.solayof.schoolinventorymanagement.dtos;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public class UpdateAssignmentDTO {
    @NotNull(message = "Actual return date cannot be null")
    private LocalDate actualRetunDate; // Actual date when the assignment was returned

    public UpdateAssignmentDTO() {
    }
    public UpdateAssignmentDTO(LocalDate actualRetunDate) {
        this.actualRetunDate = actualRetunDate;
    }
    public LocalDate getActualRetunDate() {
        return actualRetunDate;
    }
    public void setActualRetunDate(LocalDate actualRetunDate) {
        this.actualRetunDate = actualRetunDate;
    }
}
