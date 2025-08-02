package com.solayof.schoolinventorymanagement.services;

import com.solayof.schoolinventorymanagement.entity.Collector;
import com.solayof.schoolinventorymanagement.exceptions.CollectorNotFoundException;
import com.solayof.schoolinventorymanagement.repository.CollectorRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CollectorService using JUnit 5 and Mockito.
 */
@ExtendWith(MockitoExtension.class) // Enables Mockito for JUnit 5 tests
public class CollectorServiceTest {

    @Mock
    private CollectorRepository collectorRepository; // Mocked repository to avoid hitting real DB

    @InjectMocks
    private CollectorService collectorService; // Service under test, with mocks injected

    private Collector collector;
    private UUID collectorId;

    /**
     * Sets up test data before each test case.
     */
    @BeforeEach
    void setUp() {
        collectorId = UUID.randomUUID(); // Generate a random UUID
        collector = new Collector("John Doe", "123 Main St", "john@example.com"); // Create test collector
        collector.setId(collectorId); // Set the ID manually
    }

    /**
     * Test: Successfully find a collector by ID.
     */
    @Test
    void testFindByCollectorId_Success() {
        when(collectorRepository.findById(collectorId)).thenReturn(Optional.of(collector));

        Collector result = collectorService.findByCollectorId(collectorId);

        assertEquals(collector, result); // Assert returned collector matches expected
        verify(collectorRepository).findById(collectorId); // Ensure repository was called
    }

    /**
     * Test: Throw exception if collector is not found by ID.
     */
    @Test
    void testFindByCollectorId_NotFound() {
        when(collectorRepository.findById(collectorId)).thenReturn(Optional.empty());

        assertThrows(CollectorNotFoundException.class, () ->
                collectorService.findByCollectorId(collectorId)); // Expect exception
        verify(collectorRepository).findById(collectorId); // Verify interaction
    }

    /**
     * Test: Successfully find a collector by email.
     */
    @Test
    void testFindByEmail_Success() {
        when(collectorRepository.findByEmail("john@example.com")).thenReturn(Optional.of(collector));

        Collector result = collectorService.findByEmail("john@example.com");

        assertEquals(collector, result);
        verify(collectorRepository).findByEmail("john@example.com");
    }

    /**
     * Test: Throw exception if collector is not found by email.
     */
    @Test
    void testFindByEmail_NotFound() {
        when(collectorRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        assertThrows(CollectorNotFoundException.class, () ->
                collectorService.findByEmail("notfound@example.com"));
        verify(collectorRepository).findByEmail("notfound@example.com");
    }

    /**
     * Test: Check if collector exists by email (should return true).
     */
    @Test
    void testExistsByEmail_ReturnsTrue() {
        when(collectorRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertTrue(collectorService.existsByEmail("john@example.com"));
        verify(collectorRepository).existsByEmail("john@example.com");
    }

    /**
     * Test: Check if collector exists by email (should return false).
     */
    @Test
    void testExistsByEmail_ReturnsFalse() {
        when(collectorRepository.existsByEmail("jane@example.com")).thenReturn(false);

        assertFalse(collectorService.existsByEmail("jane@example.com"));
        verify(collectorRepository).existsByEmail("jane@example.com");
    }

    /**
     * Test: Save a new collector to the repository.
     */
    @Test
    void testSaveCollector() {
        when(collectorRepository.save(collector)).thenReturn(collector);

        Collector result = collectorService.saveCollector(collector);

        assertEquals(collector, result);
        verify(collectorRepository).save(collector);
    }

    /**
     * Test: Successfully delete a collector by ID.
     */
    @Test
    void testDeleteCollector_Success() {
        when(collectorRepository.findById(collectorId)).thenReturn(Optional.of(collector));

        collectorService.deleteCollector(collectorId);

        verify(collectorRepository).delete(collector);
    }

    /**
     * Test: Throw exception if trying to delete a non-existent collector.
     */
    @Test
    void testDeleteCollector_NotFound() {
        when(collectorRepository.findById(collectorId)).thenReturn(Optional.empty());

        assertThrows(CollectorNotFoundException.class, () ->
                collectorService.deleteCollector(collectorId));
        verify(collectorRepository, never()).delete(any()); // Ensure delete was not called
    }

    /**
     * Test: Retrieve all collectors from the repository.
     */
    @Test
    void testFindAll() {
        List<Collector> mockCollectors = List.of(
                collector,
                new Collector("Jane Doe", "456 Side St", "jane@example.com")
        );

        when(collectorRepository.findAll()).thenReturn(mockCollectors);

        List<Collector> result = collectorService.findAll();

        assertEquals(mockCollectors, result);
        verify(collectorRepository).findAll();
    }
}
