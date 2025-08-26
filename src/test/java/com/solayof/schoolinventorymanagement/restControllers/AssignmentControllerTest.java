package com.solayof.schoolinventorymanagement.restControllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solayof.schoolinventorymanagement.constants.Status;
import com.solayof.schoolinventorymanagement.dtos.AssignmentDTO;
import com.solayof.schoolinventorymanagement.dtos.UpdateAssignmentDTO;
import com.solayof.schoolinventorymanagement.entity.Assignment;
import com.solayof.schoolinventorymanagement.entity.Collector;
import com.solayof.schoolinventorymanagement.entity.Item;
import com.solayof.schoolinventorymanagement.exceptions.AssignmentNotFoundException;
import com.solayof.schoolinventorymanagement.modelAssembler.AssignmentModelAssembler;
import com.solayof.schoolinventorymanagement.services.AssignmentService;
import com.solayof.schoolinventorymanagement.services.CollectorService;
import com.solayof.schoolinventorymanagement.services.ItemService;
import com.solayof.schoolinventorymanagement.services.JwtService;
import com.solayof.schoolinventorymanagement.services.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for the AssignmentController.
 * This class tests the REST endpoints of the AssignmentController using MockMvc.
 */
@WebMvcTest(AssignmentController.class)
@AutoConfigureMockMvc(addFilters = false)
class AssignmentControllerTest {

    @Autowired
    private MockMvc mockMvc; // Main entry point for server-side Spring MVC test support

    @MockBean
    private AssignmentService assignmentService; // Mock service for handling assignment operations

    @MockBean
    private AssignmentModelAssembler assembler; // Mock assembler for converting Assignment entities to DTOs

    @MockBean
    private CollectorService collectorService; // Mock service for handling collector operations

    @MockBean
    private ItemService itemService; // Mock service for handling item operations

    @Autowired
    private ObjectMapper objectMapper; // ObjectMapper for converting Java objects to/from JSON

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserService userService;

    // Test data fields
    private Assignment assignment;
    private AssignmentDTO assignmentDTO;
    private Collector collector;
    private Item item;
    private EntityModel<AssignmentDTO> assignmentEntityModel;
    private UUID assignmentId;
    private UUID collectorId;
    private UUID itemId;

    /**
     * Sets up common test data and mock behavior before each test runs.
     */
    @BeforeEach
    void setUp() {
        // Initialize UUIDs for collector, item, and assignment
        collectorId = UUID.randomUUID();
        itemId = UUID.randomUUID();
        assignmentId = UUID.randomUUID();

        // Create and configure a Collector object
        collector = new Collector();
        collector.setId(collectorId);

        // Create and configure an Item object
        item = new Item();
        item.setId(itemId);
        item.setStatus(Status.AVAILABLE);

        // Create and configure an Assignment object
        assignment = new Assignment();
        assignment.setId(assignmentId);
        assignment.setAssignmentDate(LocalDate.now());
        assignment.setReturnDueDate(LocalDate.now().plusDays(7));
        assignment.setCollector(collector);
        assignment.setItem(item);

        // Create and configure an AssignmentDTO object
        assignmentDTO = new AssignmentDTO();
        assignmentDTO.setCollectorId(collectorId);
        assignmentDTO.setItemId(itemId);
        assignmentDTO.setAssignmentDate(LocalDate.now());
        assignmentDTO.setReturnDueDate(LocalDate.now().plusDays(7));

        // Create and configure an EntityModel for AssignmentDTO
        assignmentEntityModel = EntityModel.of(assignmentDTO,
                linkTo(methodOn(AssignmentController.class).getOne(assignmentId)).withSelfRel(),
                linkTo(methodOn(AssignmentController.class).getOverdueAssignments()).withRel("assignments"));
    }

    /**
     * Test for the createAssignment endpoint (POST /api/assignments).
     * Verifies that a new assignment is created successfully when valid data is provided.
     */
    @Test
    void createAssignment_shouldReturnCreated() throws Exception {
        // Mock service calls
        when(collectorService.findByCollectorId(collectorId)).thenReturn(collector);
        when(itemService.findByItemId(itemId)).thenReturn(item);
        when(assignmentService.saveAssignment(any(Assignment.class))).thenReturn(assignment);
        when(assembler.toModel(any(Assignment.class))).thenReturn(assignmentEntityModel);

        // Perform POST request and verify response
        mockMvc.perform(post("/api/assignments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignmentDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.collectorId", is(assignmentDTO.getCollectorId().toString())))
                .andExpect(jsonPath("$.itemId", is(assignmentDTO.getItemId().toString())));
    }

    /**
     * Test for the getOne endpoint (GET /api/assignments/{id}).
     * Verifies that a single assignment can be fetched by its ID.
     */
    @Test
    void getOne_shouldReturnAssignment() throws Exception {
        // Mock service calls
        when(assignmentService.findByAssignmentId(assignmentId)).thenReturn(assignment);
        when(assembler.toModel(assignment)).thenReturn(assignmentEntityModel);

        // Perform GET request and verify response
        mockMvc.perform(get("/api/assignments/{id}", assignmentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.collectorId", is(assignmentDTO.getCollectorId().toString())))
                .andExpect(jsonPath("$._links.self.href", endsWith("/api/assignments/" + assignmentId)));
    }

    /**
     * Test for the updateAssignment endpoint (PUT /api/assignments/{id}).
     * Verifies that an existing assignment can be successfully updated.
     */
    @Test
    void updateAssignment_shouldReturnUpdatedAssignment() throws Exception {
        // Create and configure an UpdateAssignmentDTO object
        UpdateAssignmentDTO updateDto = new UpdateAssignmentDTO();
        updateDto.setActualRetunDate(LocalDate.now());

        // Create and configure an updated Assignment object
        Assignment updatedAssignment = new Assignment();
        updatedAssignment.setActualReturnDate(updateDto.getActualRetunDate());

        // Create and configure an updated EntityModel for AssignmentDTO
        EntityModel<AssignmentDTO> updatedEntityModel = EntityModel.of(new AssignmentDTO());
        AssignmentDTO updatedContent = updatedEntityModel.getContent();
        if (updatedContent != null) {
            updatedContent.setActualRetunDate(updateDto.getActualRetunDate());
        }

        // Mock service calls
        when(assignmentService.findByAssignmentId(assignmentId)).thenReturn(assignment);
        when(assignmentService.saveAssignment(any(Assignment.class))).thenReturn(updatedAssignment);
        when(assembler.toModel(updatedAssignment)).thenReturn(updatedEntityModel);

        // Perform PUT request and verify response
        mockMvc.perform(put("/api/assignments/{id}", assignmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.actualRetunDate", is(updateDto.getActualRetunDate().toString())));
    }

    /**
     * Test for the deleteAssignment endpoint (DELETE /api/assignments/{id}).
     * Verifies that an assignment is deleted and returns a No Content status.
     */
    @Test
    void deleteAssignment_shouldReturnNoContent() throws Exception {
        // Mock the void delete method
        doNothing().when(assignmentService).deleteAssignment(assignmentId);

        // Perform DELETE request and verify response
        mockMvc.perform(delete("/api/assignments/{id}", assignmentId))
                .andExpect(status().isNoContent());
    }

    /**
     * Test for the getOverdueAssignments endpoint (GET /api/assignments/overdue).
     * Verifies that overdue assignments can be fetched.
     */
    @Test
    void getOverdueAssignments_shouldReturnOverdueAssignments() throws Exception {
        // Mock service call
        when(assignmentService.getOverdueAssignments()).thenReturn(Collections.singletonList(assignment));

        // Perform GET request and verify response
        mockMvc.perform(get("/api/assignments/overdue"))
                .andExpect(status().isOk());
    }

    /**
     * Test for the returnItem endpoint (PUT/api /assignments/{id}/return).
     * Verifies that an item can be returned.
     */
    @Test
    void returnItem_shouldReturnAssignment() throws Exception {
        // Mock service call
        when(assignmentService.returnItem(assignmentId)).thenReturn(assignment);

        // Perform PUT request and verify response
        mockMvc.perform(put("/api/assignments/{id}/return", assignmentId))
                .andExpect(status().isOk());
    }

    /**
     * Test for the updateAssignmentDueDate endpoint (PUT /assignments/{id}/update-due-date).
     * Verifies that the due date of an assignment can be updated.
     */
    @Test
    void updateAssignmentDueDate_shouldReturnUpdatedAssignment() throws Exception {
        // Set a new return due date
        LocalDate newReturnDueDate = LocalDate.now().plusDays(14);

        // Mock service call
        when(assignmentService.updateAssignment(assignmentId, newReturnDueDate)).thenReturn(assignment);

        // Perform PUT request and verify response
        mockMvc.perform(put("/api/assignments/{id}/update-due-date", assignmentId)
                        .param("newReturnDueDate", newReturnDueDate.toString()))
                .andExpect(status().isOk());
    }

    /**
     * Test for the getOne endpoint when an assignment is not found.
     * Verifies that it returns an HTTP 404 Not Found status.
     */
    @Test
    void getOne_whenAssignmentNotFound_shouldReturnNotFound() throws Exception {
        // Create a non-existent UUID
        UUID nonExistentId = UUID.randomUUID();

        // Mock service to throw an exception when the assignment is not found
        when(assignmentService.findByAssignmentId(nonExistentId)).thenThrow(new AssignmentNotFoundException("Could not find assignment " + nonExistentId));

        // Perform GET request and verify response
        mockMvc.perform(get("/assignments/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }
}
