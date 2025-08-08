package com.solayof.schoolinventorymanagement.secheduler;

import com.solayof.schoolinventorymanagement.constants.ReminderStatus;
import com.solayof.schoolinventorymanagement.entity.Assignment;
import com.solayof.schoolinventorymanagement.entity.Reminder;
import com.solayof.schoolinventorymanagement.services.AssignmentService;
import com.solayof.schoolinventorymanagement.services.ReminderService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j // Lombok annotation for logging
public class ReminderScheduler {
    @Autowired
    private AssignmentService assignmentService;
    @Autowired
    private ReminderService reminderService;


    // This method will run daily at 9 AM (0 0 9 * * ?)
    @Scheduled(cron = "0 0 9 * * ?")
    public void checkAndSendOverdueReminders() {
        log.info("Running scheduled task: Checking for overdue assignments and sending reminders...");
        List<Assignment> overdueAssignments = assignmentService.getOverdueAssignments();

        if (overdueAssignments.isEmpty()) {
            log.info("No overdue assignments found today.");
            return;
        }

        for (Assignment assignment : overdueAssignments) {
            // Check if a reminder for this assignment is already pending or sent today
            // For simplicity, we'll create a new one if not found or if previous was not sent.
            // More sophisticated logic might check for 'SENT' status within a certain period.
            try {
                // Create a new reminder entry
                Reminder newReminder = new Reminder();
                newReminder.setAssignment(assignment);
                newReminder.setStatus(ReminderStatus.valueOf("PENDING"));
                reminderService.saveReminder(newReminder);
                reminderService.sendReminder(newReminder.getId());
                log.info("Sent reminder for overdue assignment ID: {} (Item: {}, Collector: {})",
                        assignment.getId(), assignment.getItem().getName(), assignment.getCollector().getName());
            } catch (Exception e) {
                log.error("Failed to send reminder for assignment ID: {}. Error: {}", assignment.getId(), e.getMessage());
            }
        }
        log.info("Finished scheduled task: Overdue reminder check completed.");
    }
}