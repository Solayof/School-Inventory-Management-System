package com.solayof.schoolinventorymanagement.restControllers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import com.solayof.schoolinventorymanagement.dtos.CollectorDTO;
import com.solayof.schoolinventorymanagement.dtos.UpdateCollectorDTO;
import com.solayof.schoolinventorymanagement.entity.Collector;
import com.solayof.schoolinventorymanagement.modelAssembler.CollectorModelAssembler;
import com.solayof.schoolinventorymanagement.services.CollectorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/collectors")
public class CollectorController {
    @Autowired
    private CollectorService collectorService; // Injecting the CollectorService to handle collector-related operations
    @Autowired
    private CollectorModelAssembler assembler; // Injecting the CollectorModelAssembler to convert Collector entities to EntityModel<CollectorDTO>


    /**
     * Creates a new collector.
     * This method accepts a CollectorDTO object, which contains the details of the collector to be created.
     * It returns an EntityModel<CollectorDTO> that includes the created collector and links to relevant
     * actions.
     * @param entity the CollectorDTO containing the collector details
     * @return EntityModel<CollectorDTO> containing the created collector and links
     */
    @PostMapping("")
    @Operation(summary = "Create a new collector", description = "Creates a new inventory collector with the provided name and email.")
     @ApiResponses(value = {
        // This annotation documents the API responses for Swagger/OpenAPI
         @ApiResponse(responseCode = "201", description = "Collector created successfully"),
         @ApiResponse(responseCode = "400", description = "Collector input data")
     })
    public ResponseEntity<EntityModel<CollectorDTO>> createCollector(@Valid @RequestBody CollectorDTO entity) {
        if (collectorService.existsByEmail(entity.getEmail())) {
            throw new IllegalArgumentException("Collector with email " + entity.getEmail() + " already exists.");
        }

        // Create a new Collector entity from the DTO
        Collector collector = new Collector(
            entity.getName(),
            entity.getContactInformation(),
            entity.getEmail()
        );
        // save the collector and convert it to EntityModel<CollectorDTO>
        EntityModel<CollectorDTO> model = assembler.toModel(collectorService.saveCollector(collector));
        // Return the created collector with HTTP status 201 (Created)
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    /**
     * Retrieves a collector by their ID.
     * This method is not implemented yet, but it will return an EntityModel<CollectorDTO> for the specified collector.
     * @param collectorId the ID of the collector to retrieve
     * @return EntityModel<CollectorDTO> containing the collector details
     */
    @GetMapping("/{collectorId}")
    @Operation(summary = "Get a collector by ID", description = "Retrieves an inventory collector by its unique identifier.")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Collector retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Collector not found")
        })
    public ResponseEntity<EntityModel<CollectorDTO>> getOne(@PathVariable UUID collectorId) {
        Collector collector = collectorService.findByCollectorId(collectorId);
        EntityModel<CollectorDTO> model = assembler.toModel(collector);
        return ResponseEntity.ok(model);
    }

    /**
     * Deletes a collector by their ID.
     * This method is not implemented yet, but it will delete the specified collector.
     * @param collectorId the ID of the collector to delete
     */
    @DeleteMapping("/{collectorId}")
    public ResponseEntity<Void> deleteCollector(@PathVariable UUID collectorId) {
        collectorService.deleteCollector(collectorId);
        return ResponseEntity.noContent().build(); // Return HTTP status 204 (No Content) after deletion
    }

    /**
     * Retrieves all collectors.
     * This method is not implemented yet, but it will return a list of EntityModel<CollectorDTO> for all collectors.
     * @return ResponseEntity containing a list of EntityModel<CollectorDTO>
     */
    @GetMapping("")
    public ResponseEntity<CollectionModel<EntityModel<CollectorDTO>>> getAll() {
        List<Collector> collectors = collectorService.findAll(); // Assuming findAll() method exists in CollectorService
        List<EntityModel<CollectorDTO>> collectorModels = collectors.stream()
            .map(assembler::toModel)
            .toList();
        CollectionModel<EntityModel<CollectorDTO>> collectionModel = CollectionModel.of(
            collectorModels,
            linkTo(methodOn(CollectorController.class).getAll()).withSelfRel()
        );
        return ResponseEntity.ok(collectionModel); // Return HTTP status 200 (OK) with the collection of collectors
    }

    /**
     * Updates a collector's information.
     * This method is not implemented yet, but it will update the specified collector's details.
     * @param collectorId the ID of the collector to update
     * @param entity the CollectorDTO containing the updated collector details
     * @return EntityModel<CollectorDTO> containing the updated collector and links
     */
    @PutMapping("/{collectorId}")
    @Operation(summary = "Update a collector", description = "Updates an existing inventory collector with the provided details.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Collector updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Collector not found")
    })
    public ResponseEntity<EntityModel<CollectorDTO>> updateCollector(@PathVariable UUID collectorId, @Valid @RequestBody UpdateCollectorDTO entity) {
        Collector collector = collectorService.findByCollectorId(collectorId);
        if (entity.getEmail() != null && collectorService.existsByEmail(entity.getEmail())) {
            throw new IllegalArgumentException("Collector with email " + entity.getEmail() + " already exists.");
        }

        // Update the collector's details
        if (entity.getName() != null) collector.setName(entity.getName()); // Update name if provided
        if (entity.getContactInformation() != null) collector.setContactInformation(entity.getContactInformation()); // Update contact information if provided
        if (entity.getEmail() != null) collector.setEmail(entity.getEmail()); // Update email if provided
        // Save the updated collector
        EntityModel<CollectorDTO> model = assembler.toModel(collectorService.saveCollector(collector));
        return ResponseEntity.ok(model); // Return the updated collector with HTTP status 200 (OK)
    }
}
