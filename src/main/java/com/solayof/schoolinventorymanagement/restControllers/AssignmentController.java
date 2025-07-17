package com.solayof.schoolinventorymanagement.restControllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.solayof.schoolinventorymanagement.constants.Status;
import com.solayof.schoolinventorymanagement.dtos.AssignmentDTO;
import com.solayof.schoolinventorymanagement.dtos.UpdateAssignmentDTO;
import com.solayof.schoolinventorymanagement.entity.Assignment;
import com.solayof.schoolinventorymanagement.entity.Collector;
import com.solayof.schoolinventorymanagement.entity.Item;
import com.solayof.schoolinventorymanagement.modelAssembler.AssignmentModelAssembler;
import com.solayof.schoolinventorymanagement.services.AssignmentService;
import com.solayof.schoolinventorymanagement.services.CollectorService;
import com.solayof.schoolinventorymanagement.services.ItemService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/assignments")
public class AssignmentController {
    @Autowired
    private AssignmentService assignmentService; // Injecting the AssignmentService to handle assignment-related operations
    @Autowired
    private AssignmentModelAssembler assembler; // Injecting the AssignmentModelAssembler to convert Assignment entities to EntityModel<AssignmentDTO>
    @Autowired
    private CollectorService collectorService; // Injecting the CollectorService to handle collector-related operations
    @Autowired
    private ItemService itemService; // Injecting the ItemService to handle item-related operations


    /**
     * This method is a placeholder for handling requests to get a specific assignment by its ID.
     * It will typically use the AssignmentService to find the assignment and return it as an EntityModel<AssignmentDTO>.
     * @param id the UUID of the assignment to retrieve
     * @return EntityModel<AssignmentDTO> containing the assignment details and links
     */
    @GetMapping("/{id}")
    public EntityModel<AssignmentDTO> getOne(@PathVariable UUID id) {
        Assignment assignment = assignmentService.findByAssignmentId(id);
        return assembler.toModel(assignment);
    }

    /**
     * This method is a placeholder for handling requests to create a new assignment.
     * It will typically use the AssignmentService to save the assignment and return it as an EntityModel<AssignmentDTO>.
     * @param entity the AssignmentDTO containing the assignment details
     * @return EntityModel<AssignmentDTO> containing the created assignment and links
     */
    @PostMapping("")
    public ResponseEntity<EntityModel<AssignmentDTO>> createCollector(@Valid @RequestBody AssignmentDTO entity) {
        Collector collector = collectorService.findByCollectorId(entity.getCollectorId());
        if (entity.getReturnDueDate().isBefore(entity.getAssignmentDate())) {
            throw new IllegalArgumentException("Actual return date cannot be before assignment date.");
        }
        Assignment assignment = new Assignment();
        assignment.setReturnDueDate(entity.getReturnDueDate());
        assignment.setAssignmentDate(entity.getAssignmentDate());
        Item item = itemService.findByItemId(entity.getItemId());
        if (item.getStatus() != Status.AVAILABLE) {
            throw new IllegalArgumentException("Item with name '" + item.getName() + "' is not available for assignment.");
        }
        assignment.setItem(item);
        item.setStatus(Status.ASSIGNED); // Update the item's status to ASSIGNED
        assignment.setCollector(collector);
        collector.getAssignments().add(assignment); // Add the assignment to the collector's list
        assignmentService.saveAssignment(assignment);
        collectorService.saveCollector(collector); // Save the updated collector
        itemService.saveItem(item); // Save the updated item status
        
        return new ResponseEntity<>(
            assembler.toModel(assignment),
        HttpStatus.CREATED);
    }

    /**
     * This method is a placeholder for handling requests to update an existing assignment.
     * It will typically use the AssignmentService to update the assignment and return it as an EntityModel<AssignmentDTO>.
     * @param id the UUID of the assignment to update
     * @param entity the AssignmentDTO containing the updated assignment details
     * @return EntityModel<AssignmentDTO> containing the updated assignment and links
     */
    @PutMapping("/{id}")
    public EntityModel<AssignmentDTO> updateAssignment(@PathVariable UUID id, @Valid @RequestBody UpdateAssignmentDTO entity) {
        Assignment assignment = assignmentService.findByAssignmentId(id);
        assignment.setActualReturnDate(entity.getActualRetunDate());
        
        return assembler.toModel(assignmentService.saveAssignment(assignment));
    }

    /**
     * Deletes an assignment by its ID.
     * This method is not implemented in this snippet, but it would typically delete the assignment with the specified ID.
     * @param id the UUID of the assignment to delete
     * @throws AssingmentNotFoundException if the assignment with the specified ID does not exist
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssignment(@PathVariable UUID id) {
        // This method would typically use the assignmentService to delete the assignment by ID

        assignmentService.deleteAssignment(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Return 204 No Content status after deletion
    }
}
