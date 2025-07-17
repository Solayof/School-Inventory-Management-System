package com.solayof.schoolinventorymanagement.restControllers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.solayof.schoolinventorymanagement.constants.ReminderStatus;
import com.solayof.schoolinventorymanagement.dtos.ReminderDTO;
import com.solayof.schoolinventorymanagement.dtos.UpdateReminderDTO;
import com.solayof.schoolinventorymanagement.entity.Assignment;
import com.solayof.schoolinventorymanagement.entity.Reminder;
import com.solayof.schoolinventorymanagement.modelAssembler.ReminderModelAssembler;
import com.solayof.schoolinventorymanagement.services.AssignmentService;
import com.solayof.schoolinventorymanagement.services.ReminderService;

import jakarta.validation.Valid;
@RestController
@RequestMapping("/reminders")
public class ReminderController {
    @Autowired
    private ReminderService reminderService;
    @Autowired
    private ReminderModelAssembler reminderModelAssembler;

    @Autowired
    private AssignmentService assignmentService;

    /**
     * Retrieves a reminder by its ID.
     *
     * @param id the ID of the reminder to retrieve
     * @return the EntityModel of the ReminderDTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ReminderDTO>> getOne(@PathVariable UUID id) {
        Reminder reminder = reminderService.findByReminderId(id);
        return ResponseEntity.ok(reminderModelAssembler.toModel(reminder));
    }

    /**
     * Creates a new reminder.
     * This method is not implemented yet, but it will accept a ReminderDTO and return an EntityModel of the created reminder.
     *
     * @param entity the ReminderDTO containing the details of the reminder to create
     * @return ResponseEntity with the created reminder
     */
    @PostMapping("")
    public ResponseEntity<EntityModel<ReminderDTO>> createItem(@Valid @RequestBody ReminderDTO entity) {
        Assignment assignment = assignmentService.findByAssignmentId(entity.getAssignmentId());
        Reminder reminder = new Reminder(); // Create a new Reminder entity from the DTO
        // Set properties of the reminder from the DTO
        reminder.setMessage(entity.getMessage());
        reminder.setStatus(entity.getStatus());
        reminder.setReminderDate(entity.getReminderDate());
        reminder.setAssignment(assignment);
        assignment.getReminders().add(reminder);
        reminderService.saveReminder(reminder);
        assignmentService.saveAssignment(assignment);
        return new ResponseEntity<>(reminderModelAssembler.toModel(reminder), HttpStatus.CREATED);
    }

    /**
     * Retrieves all reminders with a specific status.
     * This method is not implemented yet, but it will return a collection of reminders with the specified status.
     *
     * @param status the status of the reminders to retrieve
     * @return ResponseEntity with a collection of reminders
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<CollectionModel<EntityModel<ReminderDTO>>> getByStatus(@PathVariable ReminderStatus status) {
        List<Reminder> reminders = reminderService.findByStatus(status);
        return ResponseEntity.ok(reminderModelAssembler.toCollectionModel(reminders));
    }

    /**
     * Updates a reminder.
     * This method is not implemented yet, but it will accept a ReminderDTO and return an
     * 
     */
    @PutMapping("/{id}/update")
    public ResponseEntity<EntityModel<ReminderDTO>> updateReminder(@PathVariable UUID id, @Valid @RequestBody UpdateReminderDTO entity) {
        Reminder reminder = reminderService.findByReminderId(id);
        // Update properties of the reminder from the DTO
        if (entity.getMessage() != null) {
            reminder.setMessage(entity.getMessage());
        }
        if (entity.getStatus() != null) {
            reminder.setStatus(entity.getStatus());
        }
        if (entity.getReminderDate() != null) {
            reminder.setReminderDate(entity.getReminderDate());
        }
        return ResponseEntity.ok(reminderModelAssembler.toModel(reminderService.saveReminder(reminder)));
    }

    /**
     * Deletes a reminder by its ID.
     * This method is not implemented yet, but it will delete the specified reminder.
     *
     * @param id the ID of the reminder to delete
     * @return ResponseEntity with HTTP status 204 (No Content) after deletion
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReminder(@PathVariable UUID id) {
        reminderService.deleteReminder(id);
        return ResponseEntity.noContent().build(); // Return HTTP status 204 (No Content) after deletion
    }
}
