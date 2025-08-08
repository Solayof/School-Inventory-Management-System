package com.solayof.schoolinventorymanagement.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.solayof.schoolinventorymanagement.constants.Status;
import com.solayof.schoolinventorymanagement.entity.Assignment;
import com.solayof.schoolinventorymanagement.entity.Collector;
import com.solayof.schoolinventorymanagement.entity.Item;
import com.solayof.schoolinventorymanagement.entity.Reminder;
import com.solayof.schoolinventorymanagement.exceptions.AssignmentNotFoundException;
import com.solayof.schoolinventorymanagement.repository.AssignmentRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class AssignmentServiceTest {

    @Mock
    private AssignmentRepository assignmentRepository;

    @Mock
    private CollectorService collectorService;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private AssignmentService assignmentService;

    private Assignment assignment;
    private Item item;
    private Collector collector;
    private UUID assignmentId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        assignmentId = UUID.randomUUID();
        collector = new Collector();
        collector.setAssignments(new HashSet<>()); // Initialize the set
        item = new Item();
        assignment = new Assignment();
        assignment.setId(assignmentId);
        assignment.setCollector(collector);
        assignment.setItem(item);
        assignment.setReturnDueDate(LocalDate.now().plusDays(5));
    }

    @Test
    void testSaveAssignment() {
        when(assignmentRepository.save(assignment)).thenReturn(assignment);
        Assignment saved = assignmentService.saveAssignment(assignment);
        assertEquals(assignment, saved);
    }

    @Test
    void testFindByAssignmentId_Success() {
        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));
        Assignment found = assignmentService.findByAssignmentId(assignmentId);
        assertEquals(assignmentId, found.getId());
    }

    @Test
    void testFindByAssignmentId_NotFound() {
        when(assignmentRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
        assertThrows(AssignmentNotFoundException.class, () -> assignmentService.findByAssignmentId(UUID.randomUUID()));
    }

    @Test
    void testDeleteAssignment() {
        // --- Arrange ---
        // Ensure the bidirectional relationship is set correctly for the test
        collector.getAssignments().add(assignment);

        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));
        // Mock the return value for non-void methods instead of using doNothing()
        when(itemService.saveItem(any(Item.class))).thenReturn(item);
        when(collectorService.saveCollector(any(Collector.class))).thenReturn(collector);

        // --- Act ---
        assignmentService.deleteAssignment(assignmentId);

        // --- Assert ---
        // Verify the item's status was updated
        assertEquals(Status.AVAILABLE, item.getStatus());
        // Verify that the services were called to save the updated entities
        verify(itemService).saveItem(item);
        verify(collectorService).saveCollector(collector);
        // Verify the assignment was removed from the collector's set of assignments
        assertFalse(collector.getAssignments().contains(assignment));
    }

    @Test
    void testGetOverdueAssignments() {
        List<Assignment> overdue = Arrays.asList(assignment);
        when(assignmentRepository.findByReturnDueDateBeforeAndActualReturnDateIsNull(any(LocalDate.class))).thenReturn(overdue);
        List<Assignment> result = assignmentService.getOverdueAssignments();
        assertEquals(overdue, result);
    }

    @Test
    void testGetRemindersByAssignmentId() {
        // --- Arrange ---
        // Create two distinct Reminder objects
        Reminder reminder1 = new Reminder();
        reminder1.setId(UUID.randomUUID());
        reminder1.setMessage("First reminder");

        Reminder reminder2 = new Reminder();
        reminder2.setId(UUID.randomUUID());
        reminder2.setMessage("Second reminder");

        // Use a mutable Set implementation like HashSet
        Set<Reminder> reminderSet = new HashSet<>();
        reminderSet.add(reminder1);
        reminderSet.add(reminder2);
        assignment.setReminders(reminderSet);

        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));
        
        // --- Act ---
        List<Reminder> reminders = assignmentService.getRemindersByAssignmentId(assignmentId);
        
        // --- Assert ---
        assertEquals(2, reminders.size());
    }

    @Test
    void testUpdateAssignment_Success() {
        LocalDate newDate = LocalDate.now().plusDays(10);
        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));
        when(assignmentRepository.save(any())).thenReturn(assignment);
        Assignment updated = assignmentService.updateAssignment(assignmentId, newDate);
        assertEquals(newDate, updated.getReturnDueDate());
    }

    @Test
    void testUpdateAssignment_AlreadyReturned() {
        assignment.setActualReturnDate(LocalDate.now());
        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));
        assertThrows(IllegalArgumentException.class, () -> assignmentService.updateAssignment(assignmentId, LocalDate.now().plusDays(3)));
    }

    @Test
    void testReturnItem_Success() {
        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));
        when(assignmentRepository.save(any())).thenReturn(assignment);
        when(itemService.saveItem(any(Item.class))).thenReturn(item); // Mock saveItem call

        Assignment result = assignmentService.returnItem(assignmentId);

        assertNotNull(result.getActualReturnDate());
        assertEquals(Status.AVAILABLE, result.getItem().getStatus());
        verify(itemService).saveItem(any(Item.class));
    }

    @Test
    void testReturnItem_AlreadyReturned() {
        assignment.setActualReturnDate(LocalDate.now());
        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));
        assertThrows(IllegalArgumentException.class, () -> assignmentService.returnItem(assignmentId));
    }

    @Test
    void testGetAllAssignments() {
        List<Assignment> all = Arrays.asList(assignment);
        when(assignmentRepository.findAll()).thenReturn(all);
        List<Assignment> result = assignmentService.getAllAssignments();
        assertEquals(1, result.size());
    }
}
