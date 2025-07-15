package com.solayof.schoolinventorymanagement.restControllers;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
import com.solayof.schoolinventorymanagement.constants.Status;
import com.solayof.schoolinventorymanagement.dtos.AssignmentDTO;
import com.solayof.schoolinventorymanagement.dtos.ItemDTO;
import com.solayof.schoolinventorymanagement.dtos.UpdateItemDTO;
import com.solayof.schoolinventorymanagement.entity.Category;
import com.solayof.schoolinventorymanagement.entity.Item;
import com.solayof.schoolinventorymanagement.modelAssembler.AssignmentModelAssembler;
import com.solayof.schoolinventorymanagement.modelAssembler.ItemModelAssembler;
import com.solayof.schoolinventorymanagement.services.CategoryService;
import com.solayof.schoolinventorymanagement.services.ItemService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/items") // Base URL for all item-related endpoints
public class ItemController {
    @Autowired
    private ItemService itemService; // Injecting the ItemService to handle item-related operations
    @Autowired
    private ItemModelAssembler assembler; // Injecting the ItemModelAssembler to convert Item entities to EntityModel<Item>

    @Autowired
    private CategoryService categoryService; // Injecting the CategoryService to handle category-related operations
    @Autowired
    private AssignmentModelAssembler assignmentAssembler; // Injecting the AssignmentModelAssebler to conver Assignment enties to EntityModel<AssignmentDTO>
    

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
        category.getItems().add(item); // Add the item to the category's list of items
        categoryService.saveCategory(category); // Save the updated category with the new item
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
    public ResponseEntity<EntityModel<ItemDTO>> getOne(@PathVariable UUID id) {
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
     * Updates an item.
     * This method is not implemented yet, but it will accept a UpdateItemDTO and return an
     * 
     */
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<ItemDTO>> updateItem(@PathVariable UUID id, @Valid @RequestBody UpdateItemDTO entity) {
        Item item = itemService.findByItemId(id);
        // Update properties of the item from the DTO
        if (entity.getName() != null) {
            item.setName(entity.getName());
        }
        if (entity.getDescription() != null) {
            item.setDescription(entity.getDescription());
        
        }
        return ResponseEntity.ok(assembler.toModel(itemService.saveItem(item)));
    }
    
    /**
     * Deletes an item by its ID.
     * This method is not implemented in this snippet, but it would typically delete the item with the specified ID.
     * @param id the UUID of the item to delete
     * @throws ItemNotFoundException if the item with the specified ID does not exist
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable UUID id) {
        // This method would typically use the ItemService to delete the item by ID
        itemService.deleteItem(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Return 204 No Content status after deletion
    }
    
    /**
     * Retrieves items by their category IDs.
     * This method is not implemented in this snippet, but it would typically return a collection of EntityModel<Item> for items in the specified categories.
     * @param categoryIds the list of category IDs to retrieve items from
     * @return Collection<EntityModel<Item>> containing items in the specified categories and links
     */
    @GetMapping("/categories")
    public ResponseEntity<CollectionModel<EntityModel<ItemDTO>>> getItemsByCategoryIds(@RequestBody List<UUID> categoryIds) {
        // This method would typically use the ItemService to find items by category IDs and return them as a collection of EntityModel<Item>
        List<Item> items = itemService.findByCategoryIdIn(categoryIds);
        return new ResponseEntity<>(
            CollectionModel.of(
                items.stream()
                    .map(assembler::toModel)
                    .collect(Collectors.toList()),
                linkTo(methodOn(ItemController.class).getItemsByCategoryIds(categoryIds)).withSelfRel()
            ),
            HttpStatus.OK
        );
    }

    /**
     * Retrieves items by their category IDs and name containing a specific string.
     * This method is not implemented in this snippet, but it would typically return a collection of EntityModel<Item> for items in the specified categories and containing the specified name.
     * @param categoryIds the list of category IDs to retrieve items from
     * @param name the string to search for in item names
     * @return Collection<EntityModel<Item>> containing items in the specified categories and containing the specified name and links
     */
    // @GetMapping("/categories/name")
    // public ResponseEntity<CollectionModel<EntityModel<ItemDTO>>> getItemsByCategoryIdsAndName(List<UUID> categoryIds, String name) {
    //     // This method would typically use the ItemService to find items by category IDs and name containing a specific string
    //     List<Item> items = itemService.findByCategoryIdInAndNameContainingIgnoreCase(categoryIds, name);
    //     return new ResponseEntity<>(
    //         CollectionModel.of(
    //             items.stream()
    //                 .map(assembler::toModel)
    //                 .collect(Collectors.toList()),
    //             linkTo(methodOn(ItemController.class).getItemsByCategoryIdsAndName(categoryIds, name)).withSelfRel()
    //         ),
    //         HttpStatus.OK
    //     );
    // }

    /**
     * Retrieves items by their status.
     * This method is not implemented in this snippet, but it would typically return a collection of EntityModel<Item> for items with the specified status.
     * @param status the status of the items to retrieve
     * @return Collection<EntityModel<Item>> containing items with the specified status and links
     */
    @GetMapping("/status")
    public ResponseEntity<CollectionModel<EntityModel<ItemDTO>>> getItemsByStatus(Status status) {
        // This method would typically use the ItemService to find items by status and return them as a collection of EntityModel<Item>
        List<Item> items = itemService.findByStatus(status);
        return new ResponseEntity<>(
            CollectionModel.of(
                items.stream()
                    .map(assembler::toModel)
                    .collect(Collectors.toList()),
                linkTo(methodOn(ItemController.class).getItemsByStatus(status)).withSelfRel()
            ),
            HttpStatus.OK
        );
    }

    /**
     * Retrieves items by their name containing a specific string.
     * This method is not implemented in this snippet, but it would typically return a collection of EntityModel<Item> for items containing the specified name.
     * @param name the string to search for in item names
     * @return Collection<EntityModel<Item>> containing items with names containing the specified string and links
     */
    @GetMapping("/name")
    public ResponseEntity<CollectionModel<EntityModel<ItemDTO>>> getItemsByName(String name) {
        // This method would typically use the ItemService to find items by name containing a specific string
        List<Item> items = itemService.findByNameContainingIgnoreCase(name);
        return new ResponseEntity<>(
            CollectionModel.of(
                items.stream()
                    .map(assembler::toModel)
                    .collect(Collectors.toList()),
                linkTo(methodOn(ItemController.class).getItemsByName(name)).withSelfRel()
            ),
            HttpStatus.OK
        );
    }

    /**
     * 
     */
    @GetMapping("/{id}/assgnment")
    public ResponseEntity<EntityModel<AssignmentDTO>> getItemAssignment(@PathVariable UUID id) {
        Item item = itemService.findByCategoryId(id);
        // if (item.getAssignment() == null) throw new AssignmentNotFoundException("item: " + item.getName() + " has no assignment");
        return new ResponseEntity<>(assignmentAssembler.toModel(item.getAssignment()), HttpStatus.OK);
    }
}
