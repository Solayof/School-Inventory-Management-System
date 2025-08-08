package com.solayof.schoolinventorymanagement.dtos;

import java.time.LocalDate;

import com.solayof.schoolinventorymanagement.constants.ReminderStatus;

import jakarta.validation.constraints.Size;

public class UpdateReminderDTO {
   private LocalDate reminderDate;
   private ReminderStatus status;
    @Size(max = 500, message = "Message must be at most 500 characters")
   private String message;

    public UpdateReminderDTO() {
    }
    public UpdateReminderDTO(LocalDate reminderDate, ReminderStatus status, String message) {
          this.reminderDate = reminderDate;
          this.status = status;
          this.message = message;
     }

   public ReminderStatus getStatus() {
    return status;
   }

   public void setStatus(ReminderStatus status) {
    this.status = status;
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
}
