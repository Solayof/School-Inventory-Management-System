package com.solayof.schoolinventorymanagement.restControllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solayof.schoolinventorymanagement.constants.ReminderStatus;
import com.solayof.schoolinventorymanagement.dtos.ReminderDTO;
import com.solayof.schoolinventorymanagement.dtos.UpdateReminderDTO;
import com.solayof.schoolinventorymanagement.entity.Assignment;
import com.solayof.schoolinventorymanagement.entity.Reminder;
import com.solayof.schoolinventorymanagement.exceptions.ReminderNotFoundException;
import com.solayof.schoolinventorymanagement.modelAssembler.ReminderModelAssembler;
import com.solayof.schoolinventorymanagement.services.AssignmentService;
import com.solayof.schoolinventorymanagement.services.JwtService;
import com.solayof.schoolinventorymanagement.services.ReminderService;
import com.solayof.schoolinventorymanagement.services.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.CollectionModel;
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
 * Test class for the ReminderController.
 * This class tests the REST endpoints of the ReminderController using MockMvc.
 */
@WebMvcTest(ReminderController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReminderControllerTest {

    @Autowired
    private MockMvc mockMvc; // MockMvc for simulating HTTP requests

    @MockBean
    private ReminderService reminderService; // Mock service for reminder operations

    @MockBean
    private ReminderModelAssembler reminderModelAssembler; // Mock assembler for converting entities to models

    @MockBean
    private AssignmentService assignmentService; // Mock service for assignment operations

    @Autowired
    private ObjectMapper objectMapper; // ObjectMapper for JSON conversion

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserService userService;

    private Reminder reminder; // Reminder entity for testing
    private ReminderDTO reminderDTO; // Reminder DTO for testing
    private Assignment assignment; // Assignment entity for testing
    private EntityModel<ReminderDTO> reminderEntityModel; // EntityModel for ReminderDTO
    private UUID reminderId; // UUID for reminder
    private UUID assignmentId; // UUID for assignment

    /**
     * Setup method to initialize test data before each test.
     */
    @BeforeEach
    void setUp() {
        assignmentId = UUID.randomUUID();
        reminderId = UUID.randomUUID();

        // Initialize an Assignment object for testing
        assignment = new Assignment();
        assignment.setId(assignmentId);

        // Initialize a Reminder object for testing
        reminder = new Reminder();
        reminder.setId(reminderId);
        reminder.setMessage("Test reminder message");
        reminder.setStatus(ReminderStatus.PENDING);
        reminder.setReminderDate(LocalDate.now().plusDays(1));
        reminder.setAssignment(assignment);

        // Initialize a ReminderDTO object for testing
        reminderDTO = new ReminderDTO();
        reminderDTO.setMessage("Test reminder message");
        reminderDTO.setStatus(ReminderStatus.PENDING);
        reminderDTO.setReminderDate(LocalDate.now().plusDays(1));
        reminderDTO.setAssignmentId(assignmentId);

        // Initialize an EntityModel for ReminderDTO
        reminderEntityModel = EntityModel.of(reminderDTO,
                linkTo(methodOn(ReminderController.class).getOne(reminderId)).withSelfRel(),
                linkTo(methodOn(ReminderController.class).getByStatus(ReminderStatus.PENDING)).withRel("reminders"));
    }

    /**
     * Test for retrieving a single reminder by ID.
     * @throws Exception if the test fails
     */
    @Test
    void getOne_shouldReturnReminder() throws Exception {
        // Mock the service call to return the reminder
        when(reminderService.findByReminderId(reminderId)).thenReturn(reminder);
        when(reminderModelAssembler.toModel(reminder)).thenReturn(reminderEntityModel);

        // Perform the GET request and verify the response
        mockMvc.perform(get("/api/reminders/{id}", reminderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(reminderDTO.getMessage())))
                .andExpect(jsonPath("$._links.self.href", endsWith("/api/reminders/" + reminderId)));
    }

    /**
     * Test for creating a new reminder.
     * @throws Exception if the test fails
     */
    @Test
    void createReminder_shouldReturnCreated() throws Exception {
        // Mock the service calls
        when(assignmentService.findByAssignmentId(assignmentId)).thenReturn(assignment);
        when(reminderService.saveReminder(any(Reminder.class))).thenReturn(reminder);
        when(reminderModelAssembler.toModel(any(Reminder.class))).thenReturn(reminderEntityModel);

        // Perform the POST request and verify the response
        mockMvc.perform(post("/api/reminders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reminderDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message", is(reminderDTO.getMessage())))
                .andExpect(jsonPath("$.status", is(reminderDTO.getStatus().toString())));
    }

    /**
     * Test for retrieving reminders by status.
     * @throws Exception if the test fails
     */
    @Test
    void getByStatus_shouldReturnReminders() throws Exception {
        // Mock the service call to return a list of reminders
        when(reminderService.findByStatus(ReminderStatus.PENDING)).thenReturn(Collections.singletonList(reminder));
        when(reminderModelAssembler.toCollectionModel(any())).thenReturn(CollectionModel.of(Collections.singletonList(reminderEntityModel)));

        // Perform the GET request and verify the response
        mockMvc.perform(get("/api/reminders/status/{status}", ReminderStatus.PENDING))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.reminderDTOList[0].status", is(ReminderStatus.PENDING.toString())));
    }

    /**
     * Test for updating an existing reminder.
     * @throws Exception if the test fails
     */
    @Test
    void updateReminder_shouldReturnUpdatedReminder() throws Exception {
        // Create an UpdateReminderDTO with updated values
        UpdateReminderDTO updateDto = new UpdateReminderDTO();
        updateDto.setMessage("Updated reminder message");
        updateDto.setStatus(ReminderStatus.SENT);
        updateDto.setReminderDate(LocalDate.now().plusDays(2));

        // Create an updated Reminder object
        Reminder updatedReminder = new Reminder();
        updatedReminder.setMessage(updateDto.getMessage());
        updatedReminder.setStatus(updateDto.getStatus());
        updatedReminder.setReminderDate(updateDto.getReminderDate());

        // Create an EntityModel for the updated ReminderDTO
        EntityModel<ReminderDTO> updatedEntityModel = EntityModel.of(new ReminderDTO());
        ReminderDTO updatedContent = updatedEntityModel.getContent();
        if (updatedContent != null) {
            updatedContent.setMessage(updateDto.getMessage());
            updatedContent.setStatus(updateDto.getStatus());
            updatedContent.setReminderDate(updateDto.getReminderDate());
        }

        // Mock the service calls
        when(reminderService.findByReminderId(reminderId)).thenReturn(reminder);
        when(reminderService.saveReminder(any(Reminder.class))).thenReturn(updatedReminder);
        when(reminderModelAssembler.toModel(updatedReminder)).thenReturn(updatedEntityModel);

        // Perform the PUT request and verify the response
        mockMvc.perform(put("/api/reminders/{id}", reminderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(updateDto.getMessage())))
                .andExpect(jsonPath("$.status", is(updateDto.getStatus().toString())));
    }

    /**
     * Test for deleting a reminder.
     * @throws Exception if the test fails
     */
    @Test
    void deleteReminder_shouldReturnNoContent() throws Exception {
        // Mock the service call to do nothing when deleting a reminder
        doNothing().when(reminderService).deleteReminder(reminderId);

        // Perform the DELETE request and verify the response
        mockMvc.perform(delete("/api/reminders/{id}", reminderId))
                .andExpect(status().isNoContent());
    }

    /**
     * Test for retrieving reminders by assignment ID.
     * @throws Exception if the test fails
     */
    @Test
    void getRemindersByAssignmentId_shouldReturnReminders() throws Exception {
        // Mock the service call to return a list of reminders
        when(assignmentService.getRemindersByAssignmentId(assignmentId)).thenReturn(Collections.singletonList(reminder));

        // Perform the GET request and verify the response
        mockMvc.perform(get("/api/reminders/assignment/{assignmentId}", assignmentId))
                .andExpect(status().isOk());
    }

    /**
     * Test for manually sending a reminder.
     * @throws Exception if the test fails
     */
    @Test
    void manuallySendReminder_shouldReturnReminder() throws Exception {
        // Mock the service calls
        when(assignmentService.findByAssignmentId(assignmentId)).thenReturn(assignment);
        when(reminderService.saveReminder(any(Reminder.class))).thenReturn(reminder);
        when(reminderService.sendReminder(reminderId)).thenReturn(reminder);

        // Perform the POST request and verify the response
        mockMvc.perform(post("/api/reminders/manual-send/{assignmentId}", assignmentId)
                        .param("message", "Manual reminder message"))
                .andExpect(status().isOk());
    }

    /**
     * Test for updating the status of a reminder.
     * @throws Exception if the test fails
     */
    @Test
    void updateReminderStatus_shouldReturnUpdatedReminder() throws Exception {
        // Define the new status
        ReminderStatus newStatus = ReminderStatus.SENT;
        // Mock the service call to return the updated reminder
        when(reminderService.updateReminderStatus(reminderId, newStatus)).thenReturn(reminder);

        // Perform the PUT request and verify the response
        mockMvc.perform(put("/api/reminders/{id}/status", reminderId)
                        .param("status", newStatus.toString()))
                .andExpect(status().isOk());
    }

    /**
     * Test for handling the case when a reminder is not found.
     * @throws Exception if the test fails
     */
    @Test
    void getOne_whenReminderNotFound_shouldReturnNotFound() throws Exception {
        // Create a non-existent reminder ID
        UUID nonExistentId = UUID.randomUUID();
        // Mock the service call to throw an exception when the reminder is not found
        when(reminderService.findByReminderId(nonExistentId)).thenThrow(new ReminderNotFoundException("Could not find reminder " + nonExistentId));

        // Perform the GET request and verify the response
        mockMvc.perform(get("/api/reminders/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }
}
