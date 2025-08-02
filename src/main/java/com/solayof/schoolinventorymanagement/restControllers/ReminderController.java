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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.solayof.schoolinventorymanagement.constants.ReminderStatus;
import com.solayof.schoolinventorymanagement.dtos.ReminderDTO;
import com.solayof.schoolinventorymanagement.dtos.UpdateReminderDTO;
import com.solayof.schoolinventorymanagement.entity.Assignment;
import com.solayof.schoolinventorymanagement.entity.Reminder;
import com.solayof.schoolinventorymanagement.modelAssembler.ReminderModelAssembler;
import com.solayof.schoolinventorymanagement.services.AssignmentService;
import com.solayof.schoolinventorymanagement.services.ReminderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Get a reminder by ID", description = "Retrieves an inventory reminder by its unique identifier.")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "reminder retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "reminder not found")
        })
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
    @Operation(summary = "Create a new reminder", description = "Creates a new inventory reminder with the provided details.")
     @ApiResponses(value = {
        // This annotation documents the API responses for Swagger/OpenAPI
         @ApiResponse(responseCode = "201", description = "Item created successfully"),
         @ApiResponse(responseCode = "400", description = "Invalid input data")
     })
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
    @Operation(summary = "Get all reminders whose status is prvided in the path", description = "Retrieves all inventory reminders whose status is prvided in the path.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reminders retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "No reminders found")
    })
    public ResponseEntity<CollectionModel<EntityModel<ReminderDTO>>> getByStatus(@PathVariable ReminderStatus status) {
        List<Reminder> reminders = reminderService.findByStatus(status);
        return ResponseEntity.ok(reminderModelAssembler.toCollectionModel(reminders));
    }

    /**
     * Updates a reminder.
     * This method is not implemented yet, but it will accept a ReminderDTO and return an
     * 
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a reminder", description = "Updates an existing inventory reminder with the provided details.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reminder updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Reminder not found")
    })
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
    @Operation(summary = "Delete a reminder", description = "Deletes an inventory reminder by its unique identifier.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Reminder deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Reminder not found")
    })
    public ResponseEntity<Void> deleteReminder(@PathVariable UUID id) {
        reminderService.deleteReminder(id);
        return ResponseEntity.noContent().build(); // Return HTTP status 204 (No Content) after deletion
    }

    @GetMapping("/assignment/{assignmentId}")
    public ResponseEntity<List<Reminder>> getRemindersByAssignmentId(@PathVariable UUID assignmentId) {
        List<Reminder> reminders = assignmentService.getRemindersByAssignmentId(assignmentId);
        return ResponseEntity.ok(reminders);
    }

    @PostMapping("/manual-send/{assignmentId}")
    public ResponseEntity<Reminder> manuallySendReminder(
            @PathVariable UUID assignmentId,
            @RequestParam(required = false) String message) {
        Assignment assignment = assignmentService.findByAssignmentId(assignmentId);

        Reminder reminder = new Reminder();
        reminder.setAssignment(assignment);
        if (message != null) reminder.setMessage(message);
        reminderService.saveReminder(reminder);
        reminder = reminderService.sendReminder(reminder.getId());
        
        return new ResponseEntity<>(reminder, HttpStatus.OK);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Reminder> updateReminderStatus(@PathVariable UUID id, ReminderStatus status) {
        Reminder updatedReminder = reminderService.updateReminderStatus(id, status);
        return ResponseEntity.ok(updatedReminder);
    }
}
