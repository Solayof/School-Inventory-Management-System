package com.solayof.schoolinventorymanagement.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.github.javafaker.Faker;
import com.solayof.schoolinventorymanagement.entity.UserEntity;
import com.solayof.schoolinventorymanagement.exceptions.UserNotFoundException;
import com.solayof.schoolinventorymanagement.repository.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AutoCloseable closeable;

    private Faker faker = new Faker();

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    void testLoadUserByUsername_UserExists() {
        UserEntity user = new UserEntity();
        user.setEmail(faker.internet().emailAddress());
        user.setPassword("encodedpass");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        UserDetails userDetails = userService.loadUserByUsername(user.getEmail());

        assertEquals(user.getEmail(), userDetails.getUsername());
        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.loadUserByUsername("missing@example.com");
        });

        verify(userRepository, times(1)).findByEmail("missing@example.com");
    }

    @Test
    void testgetUserById_UserExists() {
        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setEmail(faker.internet().emailAddress());
        user.setPassword("encodedpass");

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        UserEntity loadedUser = userService.getUserById(user.getId());
        assertEquals(user, loadedUser);
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void testgetUserById_UserNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.getUserById(id);
        });

        verify(userRepository, times(1)).findById(id);
    }

    @Test
    void testAddUser_EncodesPasswordAndSaves() {
        UserEntity user = new UserEntity();
        user.setEmail(faker.internet().emailAddress());
        user.setPassword("rawpassword");

        when(passwordEncoder.encode("rawpassword")).thenReturn("encodedPassword");

        String result = userService.addUser(user);

        assertEquals("UserEntity added Sucessfully", result);
        assertEquals("encodedPassword", user.getPassword());
        verify(userRepository, times(1)).save(user);
    }
}
