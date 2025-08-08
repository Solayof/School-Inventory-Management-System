package com.solayof.schoolinventorymanagement.dtos;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import com.solayof.schoolinventorymanagement.constants.ReminderStatus;
import com.solayof.schoolinventorymanagement.entity.Reminder;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ReminderDTO {
   private UUID id;
   @NotNull(message = "Reminder date cannot be null")
   private LocalDate reminderDate;
    @NotNull(message = "Status cannot be null")
   private ReminderStatus status;
    @NotNull(message = "Message cannot be null")
    @Size(max = 500, message = "Message must be at most 500 characters")
   private String message;
   private Instant sentAt;
   private Instant createdAt;
   private Instant updatedAt;
    @NotNull(message = "Assignment ID cannot be null") 
   private UUID assignmentId;

   public void setId(UUID id) {
    this.id = id;
   }

   public UUID getId() {
    return id;
   }

   public ReminderStatus getStatus() {
    return status;
   }

   public void setStatus(ReminderStatus status) {
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
    public ReminderDTO(UUID id, LocalDate reminderDate, ReminderStatus status, String message, Instant sentAt, Instant createdAt, Instant updatedAt, UUID assignmentId) {
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
            reminder.getStatus(),
            reminder.getMessage(),
            reminder.getSentAt(),
            reminder.getCreatedAt(),
            reminder.getUpdatedAt(),
            reminder.getAssignment().getId() //!= null ? reminder.getAssignment().getId() : null
        );
    }
}
