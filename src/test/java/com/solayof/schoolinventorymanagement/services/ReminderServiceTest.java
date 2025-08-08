package com.solayof.schoolinventorymanagement.services;

import com.solayof.schoolinventorymanagement.constants.ReminderStatus;
import com.solayof.schoolinventorymanagement.entity.Assignment;
import com.solayof.schoolinventorymanagement.entity.Reminder;
import com.solayof.schoolinventorymanagement.exceptions.ReminderNotFoundException;
import com.solayof.schoolinventorymanagement.repository.ReminderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// @ExtendWith(MockitoExtension.class) integrates Mockito with JUnit 5.
// It enables the use of Mockito annotations like @Mock and @InjectMocks.
@ExtendWith(MockitoExtension.class)
public class ReminderServiceTest {

    // @Mock creates a mock instance of ReminderRepository.
    // We use a mock because we don't want to interact with a real database during unit tests.
    // Instead, we control the behavior of this mock.
    @Mock
    private ReminderRepository reminderRepository;

    // @InjectMocks creates an instance of ReminderService and automatically injects
    // the mocked ReminderRepository into it. This is the service class we are testing.
    @InjectMocks
    private ReminderService reminderService;

    // Common Reminder and Assignment objects for use across tests
    private Reminder testReminder;
    private Assignment testAssignment;
    private UUID testReminderId;

    // @BeforeEach method runs before each test method.
    // It's used to set up common test data and conditions, ensuring each test starts with a clean slate.
    @BeforeEach
    void setUp() {
        // Initialize a test Assignment
        testAssignment = new Assignment();
        testAssignment.setId(UUID.randomUUID());
        // Ensure the reminders list is mutable for the delete test
        testAssignment.setReminders(new HashSet<>(Set.of()));

        // Initialize a test Reminder
        testReminderId = UUID.randomUUID();
        testReminder = new Reminder();
        testReminder.setId(testReminderId);
        testReminder.setReminderDate(LocalDate.now().plusDays(7));
        testReminder.setStatus(ReminderStatus.PENDING);
        testReminder.setMessage("Don't forget the assignment!");
        testReminder.setAssignment(testAssignment); // Link reminder to assignment

        // Add the reminder to the assignment's list for delete test scenario
        testAssignment.getReminders().add(testReminder);
    }

    // --- Test Cases for saveReminder method ---
    @Test
    @DisplayName("Should successfully save a new reminder")
    void saveReminder_shouldSaveAndReturnReminder() {
        // 1. Mock behavior: When reminderRepository.save() is called with any Reminder object,
        // it should return the same Reminder object.
        when(reminderRepository.save(any(Reminder.class))).thenReturn(testReminder);

        // 2. Call the method under test
        Reminder savedReminder = reminderService.saveReminder(testReminder);

        // 3. Verify interactions: Ensure that reminderRepository.save() was called exactly once
        // with the testReminder object.
        verify(reminderRepository, times(1)).save(testReminder);

        // 4. Assert outcomes: Check if the returned reminder is the same as the one we expected.
        assertNotNull(savedReminder, "Saved reminder should not be null");
        assertEquals(testReminder.getId(), savedReminder.getId(), "IDs should match");
        assertEquals(testReminder.getMessage(), savedReminder.getMessage(), "Messages should match");
        assertEquals(testReminder.getStatus(), savedReminder.getStatus(), "Statuses should match");
    }

    // --- Test Cases for findByStatus method ---
    @Test
    @DisplayName("Should return a list of reminders for a given status")
    void findByStatus_shouldReturnListOfReminders() {
        // 1. Define test data: Create a list of reminders with PENDING status.
        List<Reminder> pendingReminders = Arrays.asList(
                testReminder,
                new Reminder(UUID.randomUUID(), LocalDate.now().plusDays(10), ReminderStatus.PENDING, "Another reminder", null, null, null, testAssignment)
        );

        // 2. Mock behavior: When reminderRepository.findByStatus() is called with ReminderStatus.PENDING,
        // it should return our predefined list of pending reminders.
        when(reminderRepository.findByStatus(ReminderStatus.PENDING)).thenReturn(pendingReminders);

        // 3. Call the method under test
        List<Reminder> foundReminders = reminderService.findByStatus(ReminderStatus.PENDING);

        // 4. Verify interactions: Ensure that reminderRepository.findByStatus() was called exactly once
        // with the ReminderStatus.PENDING argument.
        verify(reminderRepository, times(1)).findByStatus(ReminderStatus.PENDING);

        // 5. Assert outcomes: Check if the returned list is not null, has the expected size,
        // and contains the expected reminders.
        assertNotNull(foundReminders, "Found reminders list should not be null");
        assertEquals(2, foundReminders.size(), "Should return 2 pending reminders");
        assertTrue(foundReminders.containsAll(pendingReminders), "Should contain all expected reminders");
    }

    @Test
    @DisplayName("Should return an empty list if no reminders found for a given status")
    void findByStatus_shouldReturnEmptyListWhenNoRemindersFound() {
        // 1. Mock behavior: When reminderRepository.findByStatus() is called with ReminderStatus.SENT,
        // it should return an empty list.
        when(reminderRepository.findByStatus(ReminderStatus.SENT)).thenReturn(new ArrayList<>());

        // 2. Call the method under test
        List<Reminder> foundReminders = reminderService.findByStatus(ReminderStatus.SENT);

        // 3. Verify interactions: Ensure that reminderRepository.findByStatus() was called exactly once
        // with the ReminderStatus.SENT argument.
        verify(reminderRepository, times(1)).findByStatus(ReminderStatus.SENT);

        // 4. Assert outcomes: Check if the returned list is empty.
        assertNotNull(foundReminders, "Found reminders list should not be null");
        assertTrue(foundReminders.isEmpty(), "Should return an empty list");
    }

    // --- Test Cases for findByReminderId method ---
    @Test
    @DisplayName("Should find and return a reminder by its ID")
    void findByReminderId_shouldReturnReminderWhenFound() {
        // 1. Mock behavior: When reminderRepository.findById() is called with testReminderId,
        // it should return an Optional containing our testReminder.
        when(reminderRepository.findById(testReminderId)).thenReturn(Optional.of(testReminder));

        // 2. Call the method under test
        Reminder foundReminder = reminderService.findByReminderId(testReminderId);

        // 3. Verify interactions: Ensure that reminderRepository.findById() was called exactly once
        // with the correct ID.
        verify(reminderRepository, times(1)).findById(testReminderId);

        // 4. Assert outcomes: Check if the returned reminder matches the expected one.
        assertNotNull(foundReminder, "Found reminder should not be null");
        assertEquals(testReminder.getId(), foundReminder.getId(), "IDs should match");
        assertEquals(testReminder.getMessage(), foundReminder.getMessage(), "Messages should match");
    }

    @Test
    @DisplayName("Should throw ReminderNotFoundException when reminder ID is not found")
    void findByReminderId_shouldThrowExceptionWhenNotFound() {
        // 1. Define test data: Create a non-existent ID.
        UUID nonExistentId = UUID.randomUUID();

        // 2. Mock behavior: When reminderRepository.findById() is called with the nonExistentId,
        // it should return an empty Optional, simulating no reminder found.
        when(reminderRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // 3. Call the method under test and assert that it throws ReminderNotFoundException.
        // assertThrows is used to verify that a specific exception is thrown by the lambda expression.
        ReminderNotFoundException thrown = assertThrows(ReminderNotFoundException.class, () ->
                reminderService.findByReminderId(nonExistentId),
                "Should throw ReminderNotFoundException when reminder is not found");

        // 4. Verify interactions: Ensure that reminderRepository.findById() was called exactly once.
        verify(reminderRepository, times(1)).findById(nonExistentId);

        // 5. Assert outcomes: Check the exception message.
        assertTrue(thrown.getMessage().contains("Reminder not found with id: " + nonExistentId),
                "Exception message should contain the ID");
    }

    // --- Test Cases for deleteReminder method ---
    @Test
    @DisplayName("Should successfully delete a reminder by removing it from its assignment's reminders list")
    void deleteReminder_shouldRemoveReminderFromAssignment() {
        // 1. Mock behavior:
        // First, mock findById to return the testReminder. This is crucial because deleteReminder
        // internally calls findByReminderId.
        when(reminderRepository.findById(testReminderId)).thenReturn(Optional.of(testReminder));

        // 2. Pre-condition check: Ensure the reminder is initially in the assignment's list.
        assertTrue(testAssignment.getReminders().contains(testReminder), "Reminder should be in assignment's list initially");
        assertEquals(1, testAssignment.getReminders().size(), "Assignment should have 1 reminder initially");

        // 3. Call the method under test
        reminderService.deleteReminder(testReminderId);

        // 4. Verify interactions:
        // Verify that findById was called to retrieve the reminder.
        verify(reminderRepository, times(1)).findById(testReminderId);
        // IMPORTANT: The current implementation of deleteReminder only modifies the in-memory
        // Assignment object's reminder list. It does NOT call reminderRepository.delete()
        // nor does it call reminderRepository.save() on the Assignment or Reminder.
        // Therefore, we verify that no further interactions happened with the repository.
        verifyNoMoreInteractions(reminderRepository);

        // 5. Assert outcomes:
        // Verify that the reminder has been removed from the assignment's list.
        assertFalse(testAssignment.getReminders().contains(testReminder), "Reminder should be removed from assignment's list");
        assertEquals(0, testAssignment.getReminders().size(), "Assignment should have 0 reminders after deletion");
    }

    @Test
    @DisplayName("Should throw ReminderNotFoundException if reminder to delete is not found")
    void deleteReminder_shouldThrowExceptionWhenReminderNotFound() {
        // 1. Define test data: Create a non-existent ID for deletion.
        UUID nonExistentId = UUID.randomUUID();

        // 2. Mock behavior: When findById is called with the nonExistentId, return an empty Optional.
        when(reminderRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // 3. Call the method under test and assert that it throws ReminderNotFoundException.
        ReminderNotFoundException thrown = assertThrows(ReminderNotFoundException.class, () ->
                reminderService.deleteReminder(nonExistentId),
                "Should throw ReminderNotFoundException when reminder to delete is not found");

        // 4. Verify interactions: Ensure that findById was called.
        verify(reminderRepository, times(1)).findById(nonExistentId);

        // 5. Assert outcomes: Check the exception message.
        assertTrue(thrown.getMessage().contains("Reminder not found with id: " + nonExistentId),
                "Exception message should contain the ID");
        // Ensure no other repository methods were called.
        verifyNoMoreInteractions(reminderRepository);
    }
}
