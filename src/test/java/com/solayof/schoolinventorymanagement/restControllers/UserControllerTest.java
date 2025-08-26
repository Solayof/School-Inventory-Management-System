package com.solayof.schoolinventorymanagement.restControllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solayof.schoolinventorymanagement.dtos.UpdateUserDTO;
import com.solayof.schoolinventorymanagement.entity.UserEntity;
import com.solayof.schoolinventorymanagement.exceptions.UserNotFoundException;
import com.solayof.schoolinventorymanagement.modelAssembler.UserModelAssembler;
import com.solayof.schoolinventorymanagement.repository.UserRepository;
import com.solayof.schoolinventorymanagement.services.JwtService;
import com.solayof.schoolinventorymanagement.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Use @WebMvcTest to test the controller layer in isolation
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Mock the dependencies of UserController
    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;


    // We also need to mock the assembler, as it's used to construct the response
    @MockBean
    private UserModelAssembler assembler;

    private UserEntity user1;
    private UserEntity user2;
    private UUID user1Id;

    @BeforeEach
    void setUp() {
        user1Id = UUID.fromString("4dc10564-ce1c-443b-81ae-4eb452c32a88");
        user1 = new UserEntity();
        user1.setId(user1Id);
        user1.setFirstName("Ahmad");
        user1.setLastName("Becker");
        user1.setEmail("ahmad.b@example.com");
        user1.setDob(LocalDate.now());

        user2 = new UserEntity();
        user2.setId(UUID.fromString("d3a8d0d3-1b1a-4125-b107-c2106dd6db82"));
        user2.setFirstName("Allen");
        user2.setLastName("Schuster");
        user2.setEmail("allen.s@example.com");

        // Mock the behavior of the UserModelAssembler
        // For any UserEntity, wrap it in an EntityModel with the correct links
        given(assembler.toModel(any(UserEntity.class))).willAnswer(invocation -> {
            UserEntity user = invocation.getArgument(0);
            return EntityModel.of(user,
                    linkTo(methodOn(UserController.class).getOne(user.getId())).withSelfRel(),
                    linkTo(methodOn(UserController.class).users()).withRel("users"));
        });
    }

    //--- Tests for GET /api/users ---

    @Test
    @DisplayName("GET /api/users - Success as ADMIN")
    @WithMockUser(roles = "ADMIN")
    void givenUsers_whenGetUsersAsAdmin_shouldReturnUserList() throws Exception {
        List<UserEntity> allUsers = Arrays.asList(user1, user2);
        given(userRepository.findAll()).willReturn(allUsers);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andExpect(jsonPath("$._embedded.userEntityList", hasSize(2)))
                .andExpect(jsonPath("$._embedded.userEntityList[0].firstName", is(user1.getFirstName())))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    @DisplayName("GET /api/users - Successful for MANAGER role")
    @WithMockUser(roles = "MANAGER")
    void givenUsers_whenGetUsersAsManager_shouldBeOk() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk());
    }
    
    @Test
    @DisplayName("GET /api/users - Forbidden for unauthenticated user")
    void givenUsers_whenGetUsersUnauthenticated_shouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized()); //
    }


    //--- Tests for GET /api/users/{id} ---

    @Test
    @DisplayName("GET /api/users/{id} - Success as MANAGER")
    @WithMockUser(roles = "MANAGER")
    void givenUser_whenGetUserByIdAsUser_shouldReturnUser() throws Exception {
        given(userService.getUserById(user1Id)).willReturn(user1);

        mockMvc.perform(get("/api/users/{id}", user1Id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is(user1.getFirstName())))
                .andExpect(jsonPath("$.email", is(user1.getEmail())))
                .andExpect(jsonPath("$._links.self.href").value("http://localhost/api/users/" + user1Id));
    }

    @Test
    @DisplayName("GET /api/users/{id} - Not Found")
    @WithMockUser(roles = "ADMIN")
    void givenNoUser_whenGetUserById_shouldReturnNotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        given(userService.getUserById(nonExistentId)).willThrow(new UserNotFoundException(nonExistentId));

        mockMvc.perform(get("/api/users/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }
    
    //--- Tests for PUT /api/users/{id} ---
    
    @Test
    @DisplayName("PUT /api/users/{id} - Success as ADMIN")
    @WithMockUser(roles = "ADMIN")
    void givenUpdateData_whenUpdateUserAsAdmin_shouldReturnUpdatedUser() throws Exception {
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setFirstName("AhmadUpdated");
        updateUserDTO.setEmail("ahmad.updated@example.com");

        // Mock finding the existing user
        given(userService.getUserById(user1Id)).willReturn(user1);
        // Mock the check for email existence
        given(userRepository.existsByEmail(updateUserDTO.getEmail())).willReturn(false);
        // Mock the save operation
        given(userService.save(any(UserEntity.class))).willAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/api/users/{id}", user1Id)
                        .with(csrf()) // Include CSRF token for PUT requests
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("AhmadUpdated")))
                .andExpect(jsonPath("$.email", is("ahmad.updated@example.com")));
    }
    
    @Test
    @DisplayName("PUT /api/users/{id} - Forbidden for MANAGER role")
    @WithMockUser(roles = "MANAGER")
    void givenUpdateData_whenUpdateUserAsManager_shouldReturnForbidden() throws Exception {
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setFirstName("NewName");
        
        mockMvc.perform(put("/api/users/{id}", user1Id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PUT /api/users/{id} - Bad Request for existing email")
    @WithMockUser(roles = "ADMIN")
    void givenExistingEmail_whenUpdateUser_shouldReturnBadRequest() throws Exception {
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setEmail("allen.s@example.com"); // Email of user2

        given(userService.getUserById(user1Id)).willReturn(user1);
        // Mock that the new email already exists in the database
        given(userRepository.existsByEmail("allen.s@example.com")).willReturn(true);

        mockMvc.perform(put("/api/users/{id}", user1Id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserDTO)))
                .andExpect(status().isBadRequest()); // Assuming an exception handler translates IllegalArgumentException to 400
    }

    //--- Tests for PUT /api/users/password/{id} ---

    @Test
    @DisplayName("PUT /api/users/password/{id} - Success as MANAGER")
    @WithMockUser(roles = "MANAGER")
    void givenNewPassword_whenUpdatePasswordAsManager_shouldSucceed() throws Exception {
        UpdateUserDTO passwordUpdateDTO = new UpdateUserDTO();
        passwordUpdateDTO.setPassword("newSecurePassword123");

        given(userService.getUserById(user1Id)).willReturn(user1);
        given(userService.updateUserPassword(any(UserEntity.class))).willAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/api/users/password/{id}/", user1Id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordUpdateDTO)))
                .andExpect(status().isOk());
    }
    
    @Test
    @DisplayName("PUT /api/users/password/{id} - Bad Request for null password")
    @WithMockUser(roles = "ADMIN")
    void givenNullPassword_whenUpdatePassword_shouldReturnBadRequest() throws Exception {
        UpdateUserDTO emptyDto = new UpdateUserDTO(); // Password field is null

        given(userService.getUserById(user1Id)).willReturn(user1);

        mockMvc.perform(put("/api/users/password/{id}/", user1Id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/users/password/{id} - Not Found")
    @WithMockUser(roles = "ADMIN")
    void givenNewPasswordForNonExistentUser_whenUpdatePassword_shouldReturnNotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        UpdateUserDTO passwordUpdateDTO = new UpdateUserDTO();
        passwordUpdateDTO.setPassword("newSecurePassword123");
        
        given(userService.getUserById(nonExistentId)).willThrow(new UserNotFoundException(nonExistentId));
        
        mockMvc.perform(put("/api/users/password/{id}/", nonExistentId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordUpdateDTO)))
                .andExpect(status().isNotFound());
    }
}