package com.solayof.schoolinventorymanagement.services;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.solayof.schoolinventorymanagement.constants.ReminderStatus;
import com.solayof.schoolinventorymanagement.entity.Assignment;
import com.solayof.schoolinventorymanagement.entity.Reminder;
import com.solayof.schoolinventorymanagement.exceptions.ReminderNotFoundException;
import com.solayof.schoolinventorymanagement.repository.ReminderRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReminderService {
    @Autowired
    private ReminderRepository reminderRepository; // Injecting the ReminderRepository to interact with reminders
    @Autowired
    private MailService mailService; // Inject MailService

    /**
     * Saves a reminder to the repository.
     *
     * @param reminder the Reminder entity to save
     * @return the saved Reminder entity
     */
    @Transactional
    public Reminder saveReminder(Reminder reminder) {
        return reminderRepository.save(reminder);
    }

    /**
     * Finds reminders by their status.
     *
     * @param status the status of the reminders to find
     * @return a list of reminders with the specified status
     */
    public List<Reminder> findByStatus(ReminderStatus status) {
        return reminderRepository.findByStatus(status);
    }

    /**
     * Finds a reminder by its ID.
     *
     * @param reminderId the ID of the reminder to find
     * @return the found Reminder entity
     * @throws ReminderNotFoundException if no reminder is found with the given ID
     */
    @Transactional
    public Reminder findByReminderId(UUID reminderId) {
        return reminderRepository.findById(reminderId)
                .orElseThrow(() -> new ReminderNotFoundException("Reminder not found with id: " + reminderId));
    }

    /**
     * Deletes a reminder by its ID.
     * @param id the ID of the reminder to delete
     * @return void
     */
    @Transactional
    public void deleteReminder(UUID id) {
        Reminder reminder = findByReminderId(id);
        Assignment assignment = reminder.getAssignment();
        assignment.getReminders().remove(reminder);
    }

    @Transactional
    public Reminder sendReminder(UUID reminderId) {
        Reminder reminder = findByReminderId(reminderId);

        // Ensure the assignment and collector are loaded to get email
        Assignment assignment = reminder.getAssignment();
        if (assignment == null || assignment.getCollector() == null || assignment.getItem() == null) {
            throw new IllegalStateException("Cannot send reminder: associated assignment or collector/item data is missing.");
        }
        String recipientEmail = assignment.getCollector().getEmail();
        String subject = "Inventory Return Reminder: " + assignment.getItem().getName();
        String body = reminder.getMessage() != null ? reminder.getMessage() :
                      "Dear " + assignment.getCollector().getName() + ",\n\n" +
                      "This is a reminder that the item '" + assignment.getItem().getName() + "' (Serial: " + assignment.getItem().getSerialNumber() + ") " +
                      "assigned to you on " + assignment.getAssignmentDate() + " is due for return by " + assignment.getReturnDueDate() + ".\n\n" +
                      "Please return it as soon as possible. Thank you.";

        try {
            mailService.sendEmail(recipientEmail, subject, body);
            reminder.setStatus(ReminderStatus.SENT);
            reminder.setSentAt(Instant.now());
            reminder.setMessage(body);
            log.info("Reminder email sent successfully to {}", recipientEmail);
            return saveReminder(reminder);
        } catch (Exception e) {
            log.error("Failed to send reminder email for reminder ID: {}", reminderId, e);
            reminder.setStatus(ReminderStatus.FAILED);
            reminder.setMessage("Failed to send reminder email: " + e.getMessage());
            saveReminder(reminder); // Save status as failed
        }
        return reminder; // Return the reminder with updated status
    }

    @Transactional
    public Reminder updateReminderStatus(UUID id, ReminderStatus newStatus) {
        Reminder reminder = findByReminderId(id);
        reminder.setStatus(newStatus);
        return reminderRepository.save(reminder);
    }

}
