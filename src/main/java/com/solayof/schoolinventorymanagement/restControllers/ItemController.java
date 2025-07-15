package com.solayof.schoolinventorymanagement.restControllers;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import com.solayof.schoolinventorymanagement.constants.Status;
import com.solayof.schoolinventorymanagement.dtos.ItemDTO;
import com.solayof.schoolinventorymanagement.entity.Category;
import com.solayof.schoolinventorymanagement.entity.Item;
import com.solayof.schoolinventorymanagement.modelAssembler.ItemModelAssembler;
import com.solayof.schoolinventorymanagement.services.CategoryService;
import com.solayof.schoolinventorymanagement.services.ItemService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;

@RestController
public class ItemController {
    @Autowired
    private ItemService itemService; // Injecting the ItemService to handle item-related operations
    @Autowired
    private ItemModelAssembler assembler; // Injecting the ItemModelAssembler to convert Item entities to EntityModel<Item>

    @Autowired
    private CategoryService categoryService; // Injecting the CategoryService to handle category-related operations


    /**
     * creates a new item.
     * This method accepts an ItemDto object, which contains the details of the item to be created.
     * It returns an EntityModel<ItemDto> that includes the created item and links to relevant
     * actions.
     * @param entity the ItemDto containing the item details
     * @return EntityModel<ItemDto> containing the created item and links
     * 
     */
    @PostMapping("")
    public ResponseEntity<EntityModel<ItemDTO>> createItem(@Valid @RequestBody ItemDTO entity) {
        // Check if the category exists
        if (!categoryService.existsById(entity.getCategoryId())) {
            throw new IllegalArgumentException("Category not found with id: " + entity.getCategoryId());
        }
        if (itemService.existsByName(entity.getName())) {
            throw new IllegalArgumentException("Item with name " + entity.getName() + " already exists.");
        }
        if (itemService.existsBySerialNumber(entity.getSerialNumber())) {
            throw new IllegalArgumentException("Item with serial number " + entity.getSerialNumber() + " already exists.");
        }
        // Create a new Item entity from the ItemDTO and save it using the ItemService
        Category category = categoryService.findByCategoryId(entity.getCategoryId());
        Item item = new Item(
            entity.getName(),
            entity.getDescription(),
            entity.getSerialNumber(),
            category
        );
        // Set the status based on the provided status string
        switch(entity.getStatus().toUpperCase()) {
            case "AVAILABLE":
                item.setStatus(Status.AVAILABLE); // Set the status to AVAILABLE
                break;
            case "ASSIGNED":
                item.setStatus(Status.ASSIGNED); // Set the status to ASSIGNED
                break;
            case "RETURNED":
                item.setStatus(Status.RETURNED); // Set the status to RETURNED
                break;
            default:
            // If the status is not recognized, throw an exception
                throw new IllegalArgumentException("Invalid status: " + entity.getStatus());
        }
        // Save the item and return it as an EntityModel<ItemDTO>
        // The assembler converts the Item entity to an EntityModel<ItemDTO>
        return new ResponseEntity<>(
            assembler.toModel(
                itemService.saveItem(item)),
            HttpStatus.CREATED
        ); // Convert ItemDto to Item entity and create it using the service
    }

    /**
     * Retrieves an item by its ID.
     * This method is not implemented in this snippet, but it would typically return an EntityModel<Item> for the specified item ID.
     * @param id the UUID of the item to retrieve
     * @return EntityModel<Item> containing the requested item and links
     * @throws ItemNotFoundException if the item with the specified ID does not exist
     */
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ItemDTO>> getOne(UUID id) {
        // This method would typically use the ItemService to find the item by ID and return it as an EntityModel<Item>
        Item item = itemService.findByItemId(id);
        return new ResponseEntity<>(
            assembler.toModel(item),
            HttpStatus.OK
        );
    }

    /**
     * Retrieves all items.
     * This method is not implemented in this snippet, but it would typically return a collection of EntityModel<Item> for all items.
     * @return Collection<EntityModel<Item>> containing all items and links
     */
    @GetMapping("")
    public ResponseEntity<CollectionModel<EntityModel<ItemDTO>>> getAll() {
        // This method would typically use the ItemService to find all items and return them as a collection of EntityModel<Item>
        List<Item> items = itemService.findAllItems();
        return new ResponseEntity<>(
            CollectionModel.of(
                items.stream()
                    .map(assembler::toModel)
                    .collect(Collectors.toList()),
                linkTo(methodOn(ItemController.class).getAll()).withSelfRel()
            ),
            HttpStatus.OK
        );
    }
    
    /**
     * Deletes an item by its ID.
     * This method is not implemented in this snippet, but it would typically delete the item with the specified ID.
     * @param id the UUID of the item to delete
     * @throws ItemNotFoundException if the item with the specified ID does not exist
     */
    @PostMapping("/{id}/delete")
    public ResponseEntity<Void> deleteItem(UUID id) {
        // This method would typically use the ItemService to delete the item by ID
        itemService.deleteItem(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Return 204 No Content status after deletion
    }
    
    /**
     * Updates an existing item.
     */
}
