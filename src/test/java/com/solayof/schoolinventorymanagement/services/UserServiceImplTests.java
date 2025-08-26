package com.solayof.schoolinventorymanagement.services;

import com.solayof.schoolinventorymanagement.entity.UserEntity;
import com.solayof.schoolinventorymanagement.exceptions.UserNotFoundException;
import com.solayof.schoolinventorymanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

// Use MockitoExtension to enable mock annotations
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    // Create a mock of the UserRepository
    @Mock
    private UserRepository userRepository;

    // Create a mock of the PasswordEncoder
    @Mock
    private PasswordEncoder passwordEncoder;

    // Inject the mocks into the UserService instance
    @InjectMocks
    private UserService userService;

    private UserEntity user;
    private UUID userId;
    private String userEmail;

    @BeforeEach
    void setUp() {
        // Initialize common test data before each test
        userId = UUID.fromString("ad0facf3-3155-402f-8d5f-34b7e566e08c");
        userEmail = "solop@gmail.com";
        user = new UserEntity();
        user.setId(userId);
        user.setFirstName("Solomon");
        user.setLastName("Moses");
        user.setEmail(userEmail);
        user.setPassword("plainPassword123");
        user.setDob(LocalDate.of(1992, 10, 12));
    }

    // --- Tests for loadUserByUsername ---

    @Test
    @DisplayName("Given existing email, when loadUserByUsername, then return UserDetails")
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        // Arrange: Mock the repository to return our test user when findByEmail is called
        given(userRepository.findByEmail(userEmail)).willReturn(Optional.of(user));

        // Act: Call the method under test
        UserDetails userDetails = userService.loadUserByUsername(userEmail);

        // Assert: Check that the returned UserDetails object is not null and has the correct username
        assertNotNull(userDetails);
        assertEquals(userEmail, userDetails.getUsername());
    }

    @Test
    @DisplayName("Given non-existing email, when loadUserByUsername, then throw UsernameNotFoundException")
    void loadUserByUsername_ShouldThrowException_WhenUserDoesNotExist() {
        // Arrange: Mock the repository to return an empty Optional
        given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());

        // Act & Assert: Verify that a UsernameNotFoundException is thrown
        assertThrows(UserNotFoundException.class, () -> {
            userService.loadUserByUsername("nonexistent@example.com");
        });
    }

    // --- Tests for getUserById ---

    @Test
    @DisplayName("Given existing ID, when getUserById, then return UserEntity")
    void getUserById_ShouldReturnUser_WhenUserExists() {
        // Arrange: Mock the repository to return our test user
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // Act: Call the method
        UserEntity foundUser = userService.getUserById(userId);

        // Assert: Check that the correct user is returned
        assertNotNull(foundUser);
        assertEquals(userId, foundUser.getId());
    }

    @Test
    @DisplayName("Given non-existing ID, when getUserById, then throw UserNotFoundException")
    void getUserById_ShouldThrowException_WhenUserDoesNotExist() {
        // Arrange: Mock the repository to return an empty Optional for a random UUID
        UUID nonExistentId = UUID.randomUUID();
        given(userRepository.findById(nonExistentId)).willReturn(Optional.empty());

        // Act & Assert: Verify that the correct exception is thrown
        assertThrows(UserNotFoundException.class, () -> {
            userService.getUserById(nonExistentId);
        });
    }

    // --- Tests for addUser ---

    @Test
    @DisplayName("When addUser, then encode password and save user")
    void addUser_ShouldEncodePasswordAndSaveUser() {
        // Arrange
        String plainPassword = user.getPassword();
        String encodedPassword = "encodedPassword_xyz";
        given(passwordEncoder.encode(plainPassword)).willReturn(encodedPassword);
        given(userRepository.save(any(UserEntity.class))).willReturn(user);

        // Act
        String result = userService.addUser(user);

        // Assert
        // Verify that the password encoder was called with the plain password
        verify(passwordEncoder).encode(plainPassword);
        // Verify that the user's password was set to the encoded one
        assertEquals(encodedPassword, user.getPassword());
        // Verify that the repository's save method was called once with the modified user
        verify(userRepository).save(user);
        assertEquals("UserEntity added Sucessfully", result);
    }

    // --- Tests for save ---

    @Test
    @DisplayName("When save, then call repository save")
    void save_ShouldCallRepositorySave() {
        // Arrange
        given(userRepository.save(user)).willReturn(user);

        // Act
        UserEntity savedUser = userService.save(user);

        // Assert
        // Verify the repository's save method was called
        verify(userRepository).save(user);
        // Ensure the returned user is the same as the one we saved
        assertThat(savedUser).isNotNull();
        assertThat(savedUser).isEqualTo(user);
        // Ensure the password was not encoded in this method
        verify(passwordEncoder, never()).encode(anyString());
    }

    // --- Tests for updateUserPassword ---

    @Test
    @DisplayName("When updateUserPassword, then encode password and save user")
    void updateUserPassword_ShouldEncodePasswordAndSaveUser() {
        // Arrange
        String newPassword = "newPassword456";
        String encodedNewPassword = "encodedNewPassword_abc";
        user.setPassword(newPassword); // Set the new plain password on the user object

        given(passwordEncoder.encode(newPassword)).willReturn(encodedNewPassword);
        given(userRepository.save(user)).willReturn(user);

        // Act
        UserEntity updatedUser = userService.updateUserPassword(user);

        // Assert
        // Verify the encoder was called with the new plain password
        verify(passwordEncoder).encode(newPassword);
        // Verify the user object now has the encoded password
        assertEquals(encodedNewPassword, updatedUser.getPassword());
        // Verify the repository's save method was called with the updated user
        verify(userRepository).save(user);
    }
}
