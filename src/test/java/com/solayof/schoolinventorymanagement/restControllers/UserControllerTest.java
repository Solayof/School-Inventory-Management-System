package com.solayof.schoolinventorymanagement.restControllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.hateoas.EntityModel;

import com.solayof.schoolinventorymanagement.dtos.UpdateUserDTO;
import com.solayof.schoolinventorymanagement.entity.UserEntity;
import com.solayof.schoolinventorymanagement.exceptions.UserNotFoundException;
import com.solayof.schoolinventorymanagement.modelAssembler.UserModelAssembler;
import com.solayof.schoolinventorymanagement.repository.UserRepository;

import org.springframework.hateoas.CollectionModel;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserModelAssembler assembler;

    @InjectMocks
    private UserController userController;

    private UUID userId;
    private UserEntity user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = UUID.randomUUID();
        user = new UserEntity();
        user.setId(userId);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("test@example.com");
    }

    @Test
    public void testGetAllUsers_returnsUserList() {
        List<UserEntity> users = List.of(user);
        when(userRepository.findAll()).thenReturn(users);
        when(assembler.toModel(any(UserEntity.class))).thenReturn(EntityModel.of(user));

        CollectionModel<EntityModel<UserEntity>> result = userController.users();

        assertThat(result.getContent()).hasSize(1);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void testGetUserById_returnsUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(assembler.toModel(user)).thenReturn(EntityModel.of(user));

        EntityModel<UserEntity> result = userController.getOne(userId);

        assertThat(result.getContent()).isEqualTo(user);
    }

    @Test
    public void testGetUserById_notFound_throwsException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userController.getOne(userId));
    }

    @Test
    public void testUpdateUser_success() {
        UpdateUserDTO updatedUser = new UpdateUserDTO();
        updatedUser.setFirstName("Updated");
        updatedUser.setMiddleName("Middle");
        updatedUser.setLastName("User");
        updatedUser.setRoles(Set.of());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(UserEntity.class))).thenReturn(user);
        when(assembler.toModel(user)).thenReturn(EntityModel.of(user));

        EntityModel<UserEntity> result = userController.updateOne(updatedUser, userId);

        assertThat(result.getContent()).isNotNull();
        assertThat(Objects.requireNonNull(result.getContent()).getFirstName()).isEqualTo("Updated");
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    public void testUpdateUser_userNotFound_throwsException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UpdateUserDTO newUser = new UpdateUserDTO();
        newUser.setFirstName("New");

        assertThrows(UserNotFoundException.class, () -> userController.updateOne(newUser, userId));
    }
}
