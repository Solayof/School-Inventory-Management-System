package com.solayof.schoolinventorymanagement.services;

import com.solayof.schoolinventorymanagement.constants.Status;
import com.solayof.schoolinventorymanagement.entity.Assignment;
import com.solayof.schoolinventorymanagement.entity.Collector;
import com.solayof.schoolinventorymanagement.entity.Item;
import com.solayof.schoolinventorymanagement.exceptions.AssignmentNotFoundException;
import com.solayof.schoolinventorymanagement.repository.AssignmentRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssignmentServiceTest {

    @Mock
    private AssignmentRepository assignmentRepository;

    @Mock
    private CollectorService collectorService;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private AssignmentService assignmentService;

    private UUID assignmentId;
    private Assignment assignment;
    private Collector collector;
    private Item item;

    @BeforeEach
    void setup() {
        assignmentId = UUID.randomUUID();

        // Create a dummy item
        item = new Item();
        item.setId(UUID.randomUUID());
        item.setStatus(Status.ASSIGNED);

        // Create a dummy collector with a set containing the assignment
        collector = new Collector();
        collector.setId(UUID.randomUUID());

        // Create an assignment
        assignment = new Assignment();
        assignment.setId(assignmentId);
        assignment.setAssignmentDate(LocalDate.now());
        assignment.setReturnDueDate(LocalDate.now().plusDays(7));
        assignment.setItem(item);
        assignment.setCollector(collector);
        assignment.setReminders(Set.of()); // assume empty reminders for simplicity

        // Set bi-directional references
        item.setAssignment(assignment);
        collector.getAssignments().add(assignment);
    }

    @Test
    void testSaveAssignment() {
        when(assignmentRepository.save(assignment)).thenReturn(assignment);

        Assignment result = assignmentService.saveAssignment(assignment);

        assertEquals(assignment, result);
        verify(assignmentRepository).save(assignment);
    }

    @Test
    void testFindByAssignmentId_Found() {
        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));

        Assignment result = assignmentService.findByAssignmentId(assignmentId);

        assertNotNull(result);
        assertEquals(assignmentId, result.getId());
        verify(assignmentRepository).findById(assignmentId);
    }

    @Test
    void testFindByAssignmentId_NotFound() {
        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.empty());

        assertThrows(AssignmentNotFoundException.class, () -> assignmentService.findByAssignmentId(assignmentId));
        verify(assignmentRepository).findById(assignmentId);
    }

    @Test
    void testDeleteAssignment_Success() {
        // Simulate that the assignment exists
        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));

        // Perform the delete operation
        assignmentService.deleteAssignment(assignmentId);

        // Validate that item status is updated and saved
        assertEquals(Status.AVAILABLE, item.getStatus());
        assertNull(item.getAssignment());

        // Verify interactions with dependent services
        verify(itemService).saveItem(item);
        verify(collectorService).saveCollector(collector);
    }

    @Test
    void testDeleteAssignment_NotFound() {
        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.empty());

        assertThrows(AssignmentNotFoundException.class, () -> assignmentService.deleteAssignment(assignmentId));
        verify(itemService, never()).saveItem(any());
        verify(collectorService, never()).saveCollector(any());
    }
}
