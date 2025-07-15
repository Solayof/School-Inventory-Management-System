package com.solayof.schoolinventorymanagement.dtos;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import com.solayof.schoolinventorymanagement.entity.Assignment;

import jakarta.validation.constraints.NotNull;

public class AssignmentDTO {
    private UUID id;
    @NotNull(message = "Assignment date cannot be null")
    private LocalDate assignmentDate; // Date when the assignment was created
    @NotNull(message = "Return due date cannot be null")
    private LocalDate returnDueDate; // Date when the assignment is due for return
    // @NotNull(message = "Actual return date cannot be null")
    private LocalDate actualRetunDate; // Actual date when the assignment was returned
    private Instant createdAt; // Timestamp when the assignment was created
    private Instant updatedAt; // Timestamp when the assignment was last updated
    @NotNull(message = "Item ID cannot be null")
    private UUID itemId; // ID of the item being assigned
    @NotNull(message = "Collector ID cannot be null")
    private UUID collectorId; // ID of the collector to whom the item is assigned

    public AssignmentDTO() {
    }
    public AssignmentDTO(LocalDate assignmentDate, LocalDate returnDueDate, LocalDate actualRetunDate,
                         UUID itemId, UUID collectorId) {

        this.assignmentDate = assignmentDate;
        this.returnDueDate = returnDueDate;
        this.actualRetunDate = actualRetunDate;
        this.itemId = itemId;
        this.collectorId = collectorId;
    }

    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public LocalDate getAssignmentDate() {
        return assignmentDate;
    }
    public void setAssignmentDate(LocalDate assignmentDate) {
        this.assignmentDate = assignmentDate;
    }
    public LocalDate getReturnDueDate() {
        return returnDueDate;
    }
    public void setReturnDueDate(LocalDate returnDueDate) {
        this.returnDueDate = returnDueDate;
    }
    public LocalDate getActualRetunDate() {
        return actualRetunDate;
    }
    public void setActualRetunDate(LocalDate actualRetunDate) {
        this.actualRetunDate = actualRetunDate;
    }
    public Instant getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
    public Instant getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
    public UUID getItemId() {
        return itemId;
    }
    public void setItemId(UUID itemId) {
        this.itemId = itemId;
    }
    public UUID getCollectorId() {
        return collectorId;
    }
    public void setCollectorId(UUID collectorId) {
        this.collectorId = collectorId;
    }
    public static AssignmentDTO fromAssignment(Assignment assignment) {
        AssignmentDTO assignmentDTO = new AssignmentDTO();
        assignmentDTO.setId(assignment.getId());
        assignmentDTO.setAssignmentDate(assignment.getAssignmentDate());
        assignmentDTO.setReturnDueDate(assignment.getReturnDueDate());
        assignmentDTO.setActualRetunDate(assignment.getActualReturnDate());
        assignmentDTO.setCreatedAt(assignment.getCreatedAt());
        assignmentDTO.setUpdatedAt(assignment.getUpdatedAt());
        return assignmentDTO;
    }
}
