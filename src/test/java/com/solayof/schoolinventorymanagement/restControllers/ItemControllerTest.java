package com.solayof.schoolinventorymanagement.restControllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solayof.schoolinventorymanagement.constants.Status;
import com.solayof.schoolinventorymanagement.dtos.ItemDTO;
import com.solayof.schoolinventorymanagement.dtos.UpdateItemDTO;
import com.solayof.schoolinventorymanagement.entity.Category;
import com.solayof.schoolinventorymanagement.entity.Item;
import com.solayof.schoolinventorymanagement.exceptions.ItemNotFoundException;
import com.solayof.schoolinventorymanagement.modelAssembler.AssignmentModelAssembler;
import com.solayof.schoolinventorymanagement.modelAssembler.ItemModelAssembler;
import com.solayof.schoolinventorymanagement.services.CategoryService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

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
 * Unit tests for the ItemController.
 *
 * @WebMvcTest isolates the Spring MVC components, specifically the ItemController, for testing.
 * It provides a focused test environment without loading the full application context.
 */
@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc(addFilters = false)
class ItemControllerTest {

    // MockMvc is the main entry point for server-side Spring MVC test support.
    @Autowired
    private MockMvc mockMvc;

    // @MockBean creates a Mockito mock for the ItemService, which will be injected into the application context.
    @MockBean
    private ItemService itemService;

    // Mocking the CategoryService as it's a dependency in ItemController.
    @MockBean
    private CategoryService categoryService;

    // Mocking HATEOAS assemblers.
    @MockBean
    private ItemModelAssembler assembler;

    @MockBean
    private AssignmentModelAssembler assignmentAssembler;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserService userService;


    // ObjectMapper for converting Java objects to/from JSON.
    @Autowired
    private ObjectMapper objectMapper;

    private Item item;
    private ItemDTO itemDto;
    private Category category;
    private EntityModel<ItemDTO> itemEntityModel;
    private UUID itemId;
    private UUID categoryId;

    /**
     * Sets up common test data and mock behavior before each test runs.
     */
    @BeforeEach
    void setUp() {
        categoryId = UUID.randomUUID();
        itemId = UUID.randomUUID();

        // Sample category
        category = new Category("Laptops", "All company laptops");
        category.setId(categoryId);

        // Sample Item entity
        item = new Item("MacBook Pro", "16-inch model", "SN12345", category);
        item.setId(itemId);
        item.setStatus(Status.AVAILABLE);

        // Sample ItemDTO
        itemDto = new ItemDTO();
        itemDto.setName("MacBook Pro");
        itemDto.setDescription("16-inch model");
        itemDto.setSerialNumber("SN12345");
        itemDto.setCategoryId(categoryId);
        itemDto.setStatus(Status.AVAILABLE);

        // Sample HATEOAS EntityModel for the ItemDTO
        itemEntityModel = EntityModel.of(itemDto,
                linkTo(methodOn(ItemController.class).getOne(itemId)).withSelfRel(),
                linkTo(methodOn(ItemController.class).getAll()).withRel("items"));
    }

    /**
     * Test for the createItem endpoint (POST /items).
     * Verifies that a new item is created successfully when valid data is provided.
     */
    @Test
    void createItem_shouldReturnCreated() throws Exception {
        // --- Arrange ---
        // Mock service calls
        when(categoryService.existsById(categoryId)).thenReturn(true);
        when(categoryService.findByCategoryId(categoryId)).thenReturn(category);
        when(itemService.existsByName(any(String.class))).thenReturn(false);
        when(itemService.existsBySerialNumber(any(String.class))).thenReturn(false);
        when(itemService.saveItem(any(Item.class))).thenReturn(item);
        when(assembler.toModel(any(Item.class))).thenReturn(itemEntityModel);

        // --- Act & Assert ---
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isCreated()) // Expect HTTP 201
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.serialNumber", is(itemDto.getSerialNumber())));
    }

    /**
     * Test for the getOne endpoint (GET /items/{id}).
     * Verifies that a single item can be fetched by its ID.
     */
    @Test
    void getOne_shouldReturnItem() throws Exception {
        // --- Arrange ---
        when(itemService.findByItemId(itemId)).thenReturn(item);
        when(assembler.toModel(item)).thenReturn(itemEntityModel);

        // --- Act & Assert ---
        mockMvc.perform(get("/items/{id}", itemId))
                .andExpect(status().isOk()) // Expect HTTP 200
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$._links.self.href", endsWith("/items/" + itemId)));
    }

    /**
     * Test for the getAll endpoint (GET /items).
     * Verifies that a list of all items is returned.
     */
    @Test
    void getAll_shouldReturnAllItems() throws Exception {
        // --- Arrange ---
        when(itemService.findAllItems()).thenReturn(Collections.singletonList(item));
        when(assembler.toModel(any(Item.class))).thenReturn(itemEntityModel);

        // --- Act & Assert ---
        mockMvc.perform(get("/items"))
                .andExpect(status().isOk()) // Expect HTTP 200
                .andExpect(jsonPath("$._embedded.itemDTOList[0].name", is(item.getName())));
    }

    /**
     * Test for the updateItem endpoint (PUT /items/{id}).
     * Verifies that an existing item can be successfully updated.
     */
    @Test
    void updateItem_shouldReturnUpdatedItem() throws Exception {
        // --- Arrange ---
        UpdateItemDTO updateDto = new UpdateItemDTO();
        updateDto.setName("MacBook Air");

        Item updatedItem = new Item("MacBook Air", item.getDescription(), item.getSerialNumber(), category);
        updatedItem.setId(itemId);

        EntityModel<ItemDTO> updatedEntityModel = EntityModel.of(new ItemDTO());
        ItemDTO updatedContent = updatedEntityModel.getContent();
        if (updatedContent != null) {
            updatedContent.setName("MacBook Air");
        }

        when(itemService.findByItemId(itemId)).thenReturn(item);
        when(itemService.saveItem(any(Item.class))).thenReturn(updatedItem);
        when(assembler.toModel(updatedItem)).thenReturn(updatedEntityModel);

        // --- Act & Assert ---
        mockMvc.perform(put("/items/{id}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk()) // Expect HTTP 200
                .andExpect(jsonPath("$.name", is("MacBook Air")));
    }

    /**
     * Test for the deleteItem endpoint (DELETE /items/{id}).
     * Verifies that an item is deleted and returns a No Content status.
     */
    @Test
    void deleteItem_shouldReturnNoContent() throws Exception {
        // --- Arrange ---
        // Mock the void delete method
        doNothing().when(itemService).deleteItem(itemId);

        // --- Act & Assert ---
        mockMvc.perform(delete("/items/{id}", itemId))
                .andExpect(status().isNoContent()); // Expect HTTP 204
    }

    /**
     * Test for the getItemsByStatus endpoint (GET /items/status).
     * Verifies that items can be filtered by their status.
     */
    @Test
    void getItemsByStatus_shouldReturnMatchingItems() throws Exception {
        // --- Arrange ---
        when(itemService.findByStatus(Status.AVAILABLE)).thenReturn(Collections.singletonList(item));
        when(assembler.toModel(any(Item.class))).thenReturn(itemEntityModel);

        // --- Act & Assert ---
        mockMvc.perform(get("/items/status").param("status", "AVAILABLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.itemDTOList[0].status", is("AVAILABLE")));
    }

    /**
     * Test for getOne endpoint when an item is not found.
     * Verifies that it returns an HTTP 404 Not Found status.
     */
    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    void getOne_whenItemNotFound_shouldReturnNotFound() throws Exception {
        // --- Arrange ---
        UUID nonExistentId = UUID.randomUUID();
        // Mock service to throw an exception when the item is not found
        when(itemService.findByItemId(nonExistentId)).thenThrow(new ItemNotFoundException("Could not find item " + nonExistentId));

        // --- Act & Assert ---
        mockMvc.perform(get("/items/{id}", nonExistentId))
                .andExpect(status().isNotFound()); // Expect HTTP 404
    }
}
