package com.solayof.schoolinventorymanagement.restControllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solayof.schoolinventorymanagement.config.TestSecurityConfig;
import com.solayof.schoolinventorymanagement.dtos.CollectorDTO;
import com.solayof.schoolinventorymanagement.dtos.UpdateCollectorDTO;
import com.solayof.schoolinventorymanagement.entity.Collector;
import com.solayof.schoolinventorymanagement.exceptions.CollectorNotFoundException;
import com.solayof.schoolinventorymanagement.modelAssembler.CollectorModelAssembler;
import com.solayof.schoolinventorymanagement.services.CollectorService;
import com.solayof.schoolinventorymanagement.services.JwtService;
import com.solayof.schoolinventorymanagement.services.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for the CollectorController.
 *
 * @WebMvcTest annotation is used for Spring MVC tests. It auto-configures the Spring MVC infrastructure
 * and limits the scanned beans to @Controller, @ControllerAdvice, etc.
 */
@WebMvcTest(CollectorController.class)
@Import(TestSecurityConfig.class)
class CollectorControllerTest {

    // MockMvc provides server-side Spring MVC test support.
    @Autowired
    private MockMvc mockMvc;

    // @MockBean creates a mock for the CollectorService to avoid injecting the actual service.
    @MockBean
    private CollectorService collectorService;

    // Mocking the HATEOAS model assembler.
    @MockBean
    private CollectorModelAssembler assembler;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserService userService;

    // ObjectMapper for JSON serialization and deserialization.
    @Autowired
    private ObjectMapper objectMapper;

    private Collector collector;
    private CollectorDTO collectorDto;
    private EntityModel<CollectorDTO> collectorEntityModel;
    private UUID collectorId;

    /**
     * This method runs before each test and sets up common test objects.
     */
    @BeforeEach
    void setUp() {
        collectorId = UUID.randomUUID();

        // Sample Collector entity
        collector = new Collector("John Doe", "123-456-7890", "john.doe@example.com");
        collector.setId(collectorId);

        // Sample CollectorDTO
        collectorDto = new CollectorDTO("John Doe", "123-456-7890", "john.doe@example.com");

        // Sample HATEOAS EntityModel for the CollectorDTO
        collectorEntityModel = EntityModel.of(collectorDto,
                linkTo(methodOn(CollectorController.class).getOne(collectorId)).withSelfRel(),
                linkTo(methodOn(CollectorController.class).getAll()).withRel("collectors"));
    }

    /**
     * Test for the createCollector endpoint (POST /api/collectors).
     * Verifies successful creation of a collector.
     */
    @Test
    void createCollector_shouldReturnCreated() throws Exception {
        // --- Arrange ---
        when(collectorService.existsByEmail(any(String.class))).thenReturn(false);
        when(collectorService.saveCollector(any(Collector.class))).thenReturn(collector);
        when(assembler.toModel(any(Collector.class))).thenReturn(collectorEntityModel);

        // --- Act & Assert ---
        mockMvc.perform(post("/api/collectors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(collectorDto)))
                .andExpect(status().isCreated()) // Expect HTTP 201
                .andExpect(jsonPath("$.name", is(collectorDto.getName())))
                .andExpect(jsonPath("$.email", is(collectorDto.getEmail())));
    }

    /**
     * Test for the getOne endpoint (GET /api/collectors/{collectorId}).
     * Verifies retrieval of a single collector by ID.
     */
    @Test
    void getOne_shouldReturnCollector() throws Exception {
        // --- Arrange ---
        when(collectorService.findByCollectorId(collectorId)).thenReturn(collector);
        when(assembler.toModel(collector)).thenReturn(collectorEntityModel);

        // --- Act & Assert ---
        mockMvc.perform(get("/api/collectors/{collectorId}", collectorId))
                .andExpect(status().isOk()) // Expect HTTP 200
                .andExpect(jsonPath("$.name", is(collector.getName())))
                .andExpect(jsonPath("$._links.self.href", endsWith("/api/collectors/" + collectorId)));
    }

    /**
     * Test for the getAll endpoint (GET /collectors).
     * Verifies retrieval of all collectors.
     */
    @Test
    void getAll_shouldReturnAllCollectors() throws Exception {
        // --- Arrange ---
        List<Collector> collectors = Collections.singletonList(collector);
        when(collectorService.findAll()).thenReturn(collectors);
        when(assembler.toModel(any(Collector.class))).thenReturn(collectorEntityModel);

        // --- Act & Assert ---
        mockMvc.perform(get("/api/collectors"))
                .andExpect(status().isOk()) // Expect HTTP 200
                .andExpect(jsonPath("$._embedded.collectorDTOList", hasSize(1)))
                .andExpect(jsonPath("$._embedded.collectorDTOList[0].name", is(collector.getName())));
    }

    /**
     * Test for the updateCollector endpoint (PUT /api/collectors/{collectorId}).
     * Verifies successful update of a collector's details.
     */
    @Test
    void updateCollector_shouldReturnUpdatedCollector() throws Exception {
        // --- Arrange ---
        UpdateCollectorDTO updateDto = new UpdateCollectorDTO();
        updateDto.setName("Jane Doe");

        Collector updatedCollector = new Collector("Jane Doe", collector.getContactInformation(), collector.getEmail());
        updatedCollector.setId(collectorId);

        CollectorDTO updatedCollectorDto = new CollectorDTO("Jane Doe", collector.getContactInformation(), collector.getEmail());
        EntityModel<CollectorDTO> updatedEntityModel = EntityModel.of(updatedCollectorDto);

        when(collectorService.findByCollectorId(collectorId)).thenReturn(collector);
        when(collectorService.existsByEmail(any())).thenReturn(false);
        when(collectorService.saveCollector(any(Collector.class))).thenReturn(updatedCollector);
        when(assembler.toModel(updatedCollector)).thenReturn(updatedEntityModel);

        // --- Act & Assert ---
        mockMvc.perform(put("/api/collectors/{collectorId}", collectorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk()) // Expect HTTP 200
                .andExpect(jsonPath("$.name", is("Jane Doe")));
    }

    /**
     * Test for the deleteCollector endpoint (DELETE /api/collectors/{collectorId}).
     * Verifies successful deletion of a collector.
     */
    @Test
    void deleteCollector_shouldReturnNoContent() throws Exception {
        // --- Arrange ---
        doNothing().when(collectorService).deleteCollector(collectorId);

        // --- Act & Assert ---
        mockMvc.perform(delete("/api/collectors/{collectorId}", collectorId))
                .andExpect(status().isNoContent()); // Expect HTTP 204
    }

    /**
     * Test for getOne endpoint when a collector is not found.
     * Verifies that it returns an HTTP 404 Not Found status.
     */
    @Test
    void getOne_whenCollectorNotFound_shouldReturnNotFound() throws Exception {
        // --- Arrange ---
        UUID nonExistentId = UUID.randomUUID();
        when(collectorService.findByCollectorId(nonExistentId))
                .thenThrow(new CollectorNotFoundException("Could not find collector " + nonExistentId));

        // --- Act & Assert ---
        mockMvc.perform(get("/api/collectors/{collectorId}", nonExistentId))
                .andExpect(status().isNotFound()); // Expect HTTP 404
    }
}
