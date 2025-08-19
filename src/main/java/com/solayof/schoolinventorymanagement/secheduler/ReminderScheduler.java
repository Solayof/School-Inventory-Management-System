package com.solayof.schoolinventorymanagement.secheduler;

import com.solayof.schoolinventorymanagement.services.ReminderService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@Slf4j // Lombok annotation for logging
public class ReminderScheduler {
    @Autowired
    private ReminderService reminderService;


    // This method will run daily at 9 AM (0 0 9 * * ?)
    @Scheduled(cron = "0 0 9 * * ?")
    public void checkAndSendOverdueReminders() {
        log.info("Running scheduled task: Checking for overdue assignments and sending reminders...");
        reminderService.processOverdueReminders();
        log.info("Finished scheduled task: Overdue reminder check completed.");
    }

    @Scheduled(fixedRate = 3600000) // Every hour from application start
    public void sendDueReminderEveryHour() {
        log.info("Running scheduled task: Sending due reminders...");
        reminderService.sendDueReminders();
        log.info("Finished scheduled task: Due reminders sent.");
    }
}