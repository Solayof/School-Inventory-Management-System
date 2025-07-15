package com.solayof.schoolinventorymanagement.dtos;

import java.time.LocalDate;

import jakarta.validation.constraints.Size;

public class UpdateReminderDTO {
   @Size(max = 10, message = "Reminder date must be in the format YYYY-MM-DD")
   private LocalDate reminderDate;
    @Size(max = 10, message = "Status must be at most 10 characters")
   private String status;
    @Size(max = 500, message = "Message must be at most 500 characters")
   private String message;

    public UpdateReminderDTO() {
    }
    public UpdateReminderDTO(LocalDate reminderDate, String status, String message) {
          this.reminderDate = reminderDate;
          this.status = status;
          this.message = message;
     }

   public String getStatus() {
    return status;
   }

   public void setStatus(String status) {
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
