package com.solayof.schoolinventorymanagement.restControllers;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.solayof.schoolinventorymanagement.dtos.CategoryDto;
import com.solayof.schoolinventorymanagement.dtos.ItemDTO;
import com.solayof.schoolinventorymanagement.dtos.UpdateCategoryDTO;
import com.solayof.schoolinventorymanagement.services.CategoryService;


import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.solayof.schoolinventorymanagement.modelAssembler.CategoryModelAssembler;
import com.solayof.schoolinventorymanagement.modelAssembler.ItemModelAssembler;
import com.solayof.schoolinventorymanagement.entity.Category;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;



@RestController
@RequestMapping("/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryModelAssembler assembler;

    @Autowired
    private ItemModelAssembler itemAssembler;

    /**
     * Creates a new category. 
     * This method accepts a CategoryDto object, which contains the name and description of the category.
     * It returns an EntityModel<Category> that includes the created category and links to relevant actions.
     * @param entity the CategoryDto containing the category details
     * @return EntityModel<Category> containing the created category and links
     * */
     @PostMapping("")
     @Operation(summary = "Create a new category", description = "Creates a new inventory category with the provided name and description.")
     @ApiResponses(value = {
        // This annotation documents the API responses for Swagger/OpenAPI
         @ApiResponse(responseCode = "201", description = "Category created successfully"),
         @ApiResponse(responseCode = "400", description = "Invalid input data")
     })
     public ResponseEntity<EntityModel<CategoryDto>> createCategory(@Valid @RequestBody CategoryDto entity) {
         // This method uses the CategoryModelAssembler to convert the created Category entity into an EntityModel<Category>
         return new ResponseEntity<EntityModel<CategoryDto>>( assembler.toModel(
             categoryService.saveCategory(new Category(
                 entity.getName(),
                 entity.getDescription()
             ))),
             HttpStatus.CREATED
         ); // Convert CategoryDto to Category entity and create it using the service

     }

     /**
      * Retrieves a category by its ID.
      * This method is not implemented in this snippet, but it would typically return an EntityModel<Category> for the specified category ID.
      * @param id the UUID of the category to retrieve
      * @return EntityModel<Category> containing the requested category and links
      * @throws CategotyNotFoundException if the category with the specified ID does not exist
      */
      @GetMapping("/{id}")
        @Operation(summary = "Get a category by ID", description = "Retrieves an inventory category by its unique identifier.")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found")
        })
        public ResponseEntity<EntityModel<CategoryDto>> getOne(@PathVariable UUID id) {
            // This method would typically use the CategoryService to find the category by ID and return it as an EntityModel<Category>
            Category category = categoryService.findByCategoryId(id);
            return new ResponseEntity<EntityModel<CategoryDto>>(
                assembler.toModel(category),
                HttpStatus.OK
            );
                
                
        }

    /**
     * Retrieves all categories.
     * 
     * This method is not implemented in this snippet, but it would typically return a collection of EntityModel<Category> for all categories.
     * @return Collection<EntityModel<Category>> containing all categories and links
     */
    @GetMapping("")
    @Operation(summary = "Get all categories", description = "Retrieves all inventory categories.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categories retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "No categories found")
    })
    public ResponseEntity<CollectionModel<EntityModel<CategoryDto>>> getAll() {
        // This method would typically use the CategoryService to find all categories and return them as a collection of EntityModel<Category>
        List<Category> categories = categoryService.findAllCategories();
        return new ResponseEntity<CollectionModel<EntityModel<CategoryDto>>>(CollectionModel.of(
            categories.stream()
                .map(assembler::toModel)
                .collect(Collectors.toList()),
            linkTo(methodOn(CategoryController.class).getAll()).withSelfRel()
        ),
            HttpStatus.OK
        );
    }
    
    /**
     * Updates an existing category.
     * 
     * This method is not implemented in this snippet, but it would typically accept a CategoryDto object and update the specified category.
     * @param id the UUID of the category to update
     * @param entity the CategoryDto containing the updated category details
     * @throws CategoryNotFoundException if the category with the specified ID does not exist
     * @return EntityModel<Category> containing the updated category and links
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a category", description = "Updates an existing inventory category with the provided details.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<EntityModel<CategoryDto>> updateCategory(@PathVariable UUID id, @Valid @RequestBody UpdateCategoryDTO entity) {
        // This method would typically use the CategoryService to find the category by ID, update it with the new details, and return it as an EntityModel<Category>
        Category category = categoryService.findByCategoryId(id);
         if (entity.getName() != null) {
            category.setName(entity.getName());
        }
        // Update properties of the category from the DTO
        if (entity.getName() != null) {
            category.setName(entity.getName());
        }
        if (entity.getDescription() != null) {
            category.setDescription(entity.getDescription());
        
        }
        return ResponseEntity.ok(assembler.toModel(categoryService.saveCategory(category)));
    }

    /**
     * Deletes a category by its ID.
     * 
     * This method is not implemented in this snippet, but it would typically delete the specified category and return a response indicating success or failure.
     * @param id the UUID of the category to delete
     * @throws CategoryNotFoundException if the category with the specified ID does not exist
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a category", description = "Deletes an inventory category by its unique identifier.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        // This method would typically use the CategoryService to find the category by ID and delete it
        categoryService.deleteCategory(id);

        return new ResponseEntity<>(org.springframework.http.HttpHeaders.EMPTY, HttpStatus.NO_CONTENT); // Return a 204 No Content response to indicate successful deletion
    }

    
    /**
     * Retrieves all items in a category.
     * 
     * This method is not implemented in this snippet, but it would typically return a collection of EntityModel<Item> for all items in the specified category.
     * @param id the UUID of the category to retrieve items from
     * @throws CategoryNotFoundException if the category with the specified ID does not exist
     * @return Collection<EntityModel<Item>> containing all items in the specified category and links
     */
    @GetMapping("/{id}/items")
    @Operation(summary = "Get all items in a category", description = "Retrieves all items in a specific inventory category.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Items retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<CollectionModel<EntityModel<ItemDTO>>> getItemsInCategory(@PathVariable UUID id) {
        // This method would typically use the CategoryService to find the category by ID and return its items as a collection of EntityModel<Item>
        Category category = categoryService.findByCategoryId(id);
        return new ResponseEntity<>(
            CollectionModel.of(
                category.getItems()
                    .stream()
                    .map(itemAssembler::toModel)
                    .collect(Collectors.toList()),
                linkTo(methodOn(ItemController.class).getAll()).withSelfRel()
            ),
            HttpStatus.OK
        );
    }

    /**
     * Retrieves all categories by their name containing a specific string.
     * 
     * This method is not implemented in this snippet, but it would typically return a collection of EntityModel<Category> for categories matching the specified name.
     * @param name the string to search for in category names
     * @return Collection<EntityModel<Category>> containing categories that match the specified name and links
     */
    @GetMapping("/search")
    @Operation(summary = "Search categories by name", description = "Retrieves all inventory categories whose names contain the specified string.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categories retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "No categories found")
    })
    public ResponseEntity<CollectionModel<EntityModel<CategoryDto>>> searchCategoriesByName(String name) {
        // This method would typically use the CategoryService to find categories by name and return them as a collection of EntityModel<Category>
        List<Category> categories = categoryService.findByNameContainingIgnoreCase(name);
        return new ResponseEntity<>(
            CollectionModel.of(
                categories.stream()
                    .map(assembler::toModel)
                    .collect(Collectors.toList()),
                linkTo(methodOn(CategoryController.class).searchCategoriesByName(name)).withSelfRel()
            ),
            HttpStatus.OK
        );
    }
}
