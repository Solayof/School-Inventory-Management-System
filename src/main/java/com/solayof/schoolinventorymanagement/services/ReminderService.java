package com.solayof.schoolinventorymanagement.services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.solayof.schoolinventorymanagement.constants.ReminderStatus;
import com.solayof.schoolinventorymanagement.entity.Reminder;
import com.solayof.schoolinventorymanagement.exceptions.ReminderNotFoundException;
import com.solayof.schoolinventorymanagement.repository.ReminderRepository;

@Service
public class ReminderService {
    @Autowired
    private ReminderRepository reminderRepository; // Injecting the ReminderRepository to interact with reminders

    /**
     * Saves a reminder to the repository.
     *
     * @param reminder the Reminder entity to save
     * @return the saved Reminder entity
     */
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
    public Reminder findByReminderId(UUID reminderId) {
        return reminderRepository.findById(reminderId)
                .orElseThrow(() -> new ReminderNotFoundException("Reminder not found with id: " + reminderId));
    }
}
