package com.solayof.schoolinventorymanagement.restControllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solayof.schoolinventorymanagement.config.TestSecurityConfig;
import com.solayof.schoolinventorymanagement.dtos.CategoryDto;
import com.solayof.schoolinventorymanagement.dtos.ItemDTO;
import com.solayof.schoolinventorymanagement.dtos.UpdateCategoryDTO;
import com.solayof.schoolinventorymanagement.entity.Category;
import com.solayof.schoolinventorymanagement.entity.Item;
import com.solayof.schoolinventorymanagement.modelAssembler.CategoryModelAssembler;
import com.solayof.schoolinventorymanagement.modelAssembler.ItemModelAssembler;
import com.solayof.schoolinventorymanagement.services.CategoryService;
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
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Unit tests for the CategoryController.
 *
 * @WebMvcTest focuses on Spring MVC components. In this case, it's limited to CategoryController.
 * This annotation disables full auto-configuration and instead applies only configuration relevant to MVC tests.
 */
@WebMvcTest(CategoryController.class)
@Import(TestSecurityConfig.class)
class CategoryControllerTest {

    // MockMvc provides support for Spring MVC testing. It encapsulates all web application beans
    // and makes them available for testing.
    @Autowired
    private MockMvc mockMvc;

    // We use @MockBean to create and inject a mock for the CategoryService.
    // This allows us to define the behavior of the service without needing to wire up the actual service.
    @MockBean
    private CategoryService categoryService;

    // Mocking the HATEOAS model assemblers.
    @MockBean
    private CategoryModelAssembler assembler;

    @MockBean
    private ItemModelAssembler itemAssembler;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserService userService;


    // ObjectMapper is used for converting Java objects to JSON and vice-versa.
    @Autowired
    private ObjectMapper objectMapper;

    private Category category;
    private CategoryDto categoryDto;
    private EntityModel<CategoryDto> categoryEntityModel;
    private UUID categoryId;

    /**
     * This method is executed before each test.
     * It sets up common objects needed for the tests.
     */
    @BeforeEach
    void setUp() {
        categoryId = UUID.randomUUID();
        
        // Create a sample Category entity
        category = new Category("Electronics", "Devices and gadgets");
        category.setId(categoryId);

        // Create a sample CategoryDto
        categoryDto = new CategoryDto("Electronics", "Devices and gadgets");

        // Create a HATEOAS EntityModel for the CategoryDto
        categoryEntityModel = EntityModel.of(categoryDto,
                linkTo(methodOn(CategoryController.class).getOne(categoryId)).withSelfRel(),
                linkTo(methodOn(CategoryController.class).getAll()).withRel("categories"));
    }

    /**
     * Test for the createCategory endpoint (POST /api/categories).
     * It verifies that a new category can be created successfully.
     */
    @Test
    void createCategory_shouldReturnCreated() throws Exception {
        // --- Arrange ---
        // Mock the behavior of the categoryService and assembler
        when(categoryService.saveCategory(any(Category.class))).thenReturn(category);
        when(assembler.toModel(any(Category.class))).thenReturn(categoryEntityModel);

        // --- Act & Assert ---
        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isCreated()) // Expect HTTP 201 Created
                .andExpect(jsonPath("$.name", is(categoryDto.getName()))) // Check if the name in the response matches
                .andExpect(jsonPath("$.description", is(categoryDto.getDescription()))); // Check if the description matches
    }

    /**
     * Test for the getOne endpoint (GET /api/categories/{id}).
     * It verifies that a single category can be retrieved by its ID.
     */
    @Test
    void getOne_shouldReturnCategory() throws Exception {
        // --- Arrange ---
        when(categoryService.findByCategoryId(categoryId)).thenReturn(category);
        when(assembler.toModel(category)).thenReturn(categoryEntityModel);

        // --- Act & Assert ---
        mockMvc.perform(get("/api/categories/{id}", categoryId))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.name", is(category.getName())))
                .andExpect(jsonPath("$._links.self.href", endsWith("/api/categories/" + categoryId)));
    }

    /**
     * Test for the getAll endpoint (GET /api/categories).
     * It verifies that a list of all categories can be retrieved.
     */
    @Test
    void getAll_shouldReturnAllCategories() throws Exception {
        // --- Arrange ---
        List<Category> categories = Collections.singletonList(category);
        when(categoryService.findAllCategories()).thenReturn(categories);
        when(assembler.toModel(any(Category.class))).thenReturn(categoryEntityModel);

        // --- Act & Assert ---
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$._embedded.categoryDtoList", hasSize(1))) // Check if the list contains one item
                .andExpect(jsonPath("$._embedded.categoryDtoList[0].name", is("Electronics")));
    }

    /**
     * Test for the updateCategory endpoint (PUT /api/categories/{id}).
     * It verifies that an existing category can be updated.
     */
    @Test
    void updateCategory_shouldReturnUpdatedCategory() throws Exception {
        // --- Arrange ---
        UpdateCategoryDTO updateDto = new UpdateCategoryDTO();
        updateDto.setName("Updated Electronics");
        updateDto.setDescription("All updated electronic devices");

        Category updatedCategory = new Category("Updated Electronics", "All updated electronic devices");
        updatedCategory.setId(categoryId);
        
        EntityModel<CategoryDto> updatedEntityModel = EntityModel.of(new CategoryDto("Updated Electronics", "All updated electronic devices"));

        when(categoryService.findByCategoryId(categoryId)).thenReturn(category);
        when(categoryService.saveCategory(any(Category.class))).thenReturn(updatedCategory);
        when(assembler.toModel(updatedCategory)).thenReturn(updatedEntityModel);

        // --- Act & Assert ---
        mockMvc.perform(put("/api/categories/{id}", categoryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.name", is("Updated Electronics")));
    }

    /**
     * Test for the deleteCategory endpoint (DELETE /api/categories/{id}).
     * It verifies that a category can be deleted by its ID.
     */
    @Test
    void deleteCategory_shouldReturnNoContent() throws Exception {
        // --- Arrange ---
        // For void methods, use doNothing()
        doNothing().when(categoryService).deleteCategory(categoryId);

        // --- Act & Assert ---
        mockMvc.perform(delete("/api/categories/{id}", categoryId))
                .andExpect(status().isNoContent()); // Expect HTTP 204 No Content
    }

    /**
     * Test for the getItemsInCategory endpoint (GET /api/categories/{id}/items).
     * It verifies that all items for a specific category can be retrieved.
     */
    @Test
    void getItemsInCategory_shouldReturnItems() throws Exception {
        // --- Arrange ---
        Item item = new Item();
        item.setId(UUID.randomUUID());
        item.setName("Laptop");
        category.setItems(Set.of(item));
        
        ItemDTO itemDto = new ItemDTO();
        itemDto.setName("Laptop");
        EntityModel<ItemDTO> itemEntityModel = EntityModel.of(itemDto);

        when(categoryService.findByCategoryId(categoryId)).thenReturn(category);
        when(itemAssembler.toModel(any(Item.class))).thenReturn(itemEntityModel);

        // --- Act & Assert ---
        mockMvc.perform(get("/api/categories/{id}/items", categoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.itemDTOList[0].name", is("Laptop")));
    }

    /**
     * Test for the searchCategoriesByName endpoint (GET /api/categories/search).
     * It verifies that categories can be searched by name.
     */
    @Test
    void searchCategoriesByName_shouldReturnMatchingCategories() throws Exception {
        // --- Arrange ---
        String searchTerm = "Elec";
        List<Category> categories = Collections.singletonList(category);
        when(categoryService.findByNameContainingIgnoreCase(searchTerm)).thenReturn(categories);
        when(assembler.toModel(any(Category.class))).thenReturn(categoryEntityModel);

        // --- Act & Assert ---
        mockMvc.perform(get("/api/categories/search").param("name", searchTerm))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.categoryDtoList[0].name", is("Electronics")));
    }
}

