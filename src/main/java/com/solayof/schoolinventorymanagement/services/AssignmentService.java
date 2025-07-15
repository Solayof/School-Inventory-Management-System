package com.solayof.schoolinventorymanagement.services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.solayof.schoolinventorymanagement.entity.Assignment;
import com.solayof.schoolinventorymanagement.exceptions.AssignmentNotFoundException;
import com.solayof.schoolinventorymanagement.repository.AssignmentRepository;

@Service
public class AssignmentService {
    @Autowired
    private AssignmentRepository assignmentRepository; // Injecting the AssignmentRepository to interact with assignments

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

    public void deleteAssignment(UUID id) {
        assignmentRepository.delete(findByAssignmentId(id));
    }
}
