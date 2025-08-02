package com.solayof.schoolinventorymanagement.services;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.solayof.schoolinventorymanagement.constants.Status;
import com.solayof.schoolinventorymanagement.entity.Assignment;
import com.solayof.schoolinventorymanagement.entity.Collector;
import com.solayof.schoolinventorymanagement.entity.Item;
import com.solayof.schoolinventorymanagement.entity.Reminder;
import com.solayof.schoolinventorymanagement.exceptions.AssignmentNotFoundException;
import com.solayof.schoolinventorymanagement.repository.AssignmentRepository;

import jakarta.transaction.Transactional;

@Service
public class AssignmentService {
    @Autowired
    private AssignmentRepository assignmentRepository; // Injecting the AssignmentRepository to interact with assignments
    @Autowired
    private CollectorService collectorService; // Injecting the CollectorService to handle collector-related operations
    @Autowired
    private ItemService itemService; // Injecting the ItemService to handle item-related operations

    /**
     * Saves an assignment to the repository.
     *
     * @param assignment the Assignment entity to save
     * @return the saved Assignment entity
     */
    public Assignment saveAssignment(Assignment assignment) {
        return assignmentRepository.save(assignment);
    }
    /**
     * Finds an assignment by its ID.
     *
     * @param assignmentId the ID of the assignment to find
     * @return the found Assignment entity
     * @throws AssignmentNotFoundException if no assignment is found with the given ID
     */
    public Assignment findByAssignmentId(UUID assignmentId) {
        return assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new AssignmentNotFoundException("Assignment not found with id: " + assignmentId));
    }

    /**
     * Delete assignment by its ID
     * 
     * Deleting an assignment deletes all its reminders and
     * set the assigned item status to AVAILABLE
     * 
     * @param id the ID of the assignment to delete
     * @throws AssignmentNotFoundException if no assignment is found with the given ID
     */
    public void deleteAssignment(UUID id) {
        Assignment assignment = findByAssignmentId(id);
        Collector collector = assignment.getCollector();
        
        collector.getAssignments().remove(assignment); // Remove the assignment from the collector's list
        Item item = assignment.getItem();
        item.setAssignment(null); // Clear the assignment reference in the item
        item.setStatus(Status.AVAILABLE); // Update the item's status to AVAILABLE
        itemService.saveItem(item); // Save the updated item status
        // Update the collector in the database
        // Removing an assigment from a collector automatically delete assignment
        collectorService.saveCollector(collector); // 
        
    }

    public List<Assignment> getOverdueAssignments() {
        return assignmentRepository.findByReturnDueDateBeforeAndActualReturnDateIsNull(LocalDate.now());
    }


    public List<Reminder> getRemindersByAssignmentId( UUID assignmentId) {
        Assignment assignment = findByAssignmentId(assignmentId);
        return assignment.getReminders()
        .stream()
        .toList();
    }

    @Transactional
    public Assignment updateAssignment(UUID id, LocalDate newReturnDueDate) {
        Assignment assignment = findByAssignmentId(id);

        if (assignment.getActualReturnDate() != null) {
            throw new IllegalArgumentException("Cannot update due date for an already returned assignment.");
        }

        assignment.setReturnDueDate(newReturnDueDate);
        return assignmentRepository.save(assignment);
    }

    @Transactional
    public Assignment returnItem(UUID assignmentId) {
        Assignment assignment = findByAssignmentId(assignmentId);

        if (assignment.getActualReturnDate() != null) {
            throw new IllegalArgumentException("Item for this assignment has already been returned.");
        }

        assignment.setActualReturnDate(LocalDate.now());
        Assignment updatedAssignment = assignmentRepository.save(assignment);

        // Update item status
        Item item = updatedAssignment.getItem();
        item.setStatus(Status.RETURNED); // or Status.AVAILABLE if it's immediately ready for re-assignment
        item.setAssignment(null); // Unlink the assignment from the item
        itemService.saveItem(item);

        return updatedAssignment;
    }


}
