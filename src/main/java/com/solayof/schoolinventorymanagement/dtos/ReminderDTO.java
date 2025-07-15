package com.solayof.schoolinventorymanagement.dtos;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import com.solayof.schoolinventorymanagement.entity.Reminder;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ReminderDTO {
   private UUID id;
   @NotNull(message = "Reminder date cannot be null")
   @Size(max = 10, message = "Reminder date must be in the format YYYY-MM-DD")
   private LocalDate reminderDate;
    @NotNull(message = "Status cannot be null")
    @Size(max = 10, message = "Status must be at most 10 characters")
   private String status;
    @NotNull(message = "Message cannot be null")
    @Size(max = 500, message = "Message must be at most 500 characters")
   private String message;
   private Instant sentAt;
   private Instant createdAt;
   private Instant updatedAt;
    @NotNull(message = "Assignment ID cannot be null")
    @Size(max = 36, message = "Assignment ID must be a valid UUID")
    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", 
                message = "Invalid UUID format for Assignment ID")
   private UUID assignmentId;

   public void setId(UUID id) {
    this.id = id;
   }

   public UUID getId() {
    return id;
   }

   public String getStatus() {
    return status;
   }

   public void setStatus(String status) {
    this.status = status;
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

    public String getMessage() {
        return message;
    }

    public void setMessage( String message) {
        this.message = message;
    }

    public LocalDate getReminderDate() {
        return reminderDate;
    }

    public void setReminderDate(LocalDate reminderDate) {
        this.reminderDate = reminderDate;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public void setSentAt(Instant sentAt) {
        this.sentAt = sentAt;
    }
    public UUID getAssignmentId() {
        return assignmentId;
    }
    public void setAssignmentId(UUID assignmentId) {
        this.assignmentId = assignmentId;
    }

    public ReminderDTO() {
    }
    public ReminderDTO(UUID id, LocalDate reminderDate, String status, String message, Instant sentAt, Instant createdAt, Instant updatedAt, UUID assignmentId) {
        this.id = id;
        this.reminderDate = reminderDate;
        this.status = status;
        this.message = message;
        this.sentAt = sentAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.assignmentId = assignmentId;
    }
    public static ReminderDTO fromReminder(Reminder reminder) {
        return new ReminderDTO(
            reminder.getId(),
            reminder.getReminderDate(),
            reminder.getStatus().name(),
            reminder.getMessage(),
            reminder.getSentAt(),
            reminder.getCreatedAt(),
            reminder.getUpdatedAt(),
            reminder.getAssignment().getId() //!= null ? reminder.getAssignment().getId() : null
        );
    }
}
