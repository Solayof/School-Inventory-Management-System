package com.solayof.schoolinventorymanagement.restControllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.solayof.schoolinventorymanagement.constants.ERole;
import com.solayof.schoolinventorymanagement.dtos.LoginDTO;
import com.solayof.schoolinventorymanagement.dtos.SignupDTO;
import com.solayof.schoolinventorymanagement.entity.RoleEntity;
import com.solayof.schoolinventorymanagement.repository.RoleRepository;
import com.solayof.schoolinventorymanagement.repository.UserRepository;
import com.solayof.schoolinventorymanagement.services.JwtService;
import com.solayof.schoolinventorymanagement.services.UserDetailsImpl;
import com.solayof.schoolinventorymanagement.services.UserService;

import jakarta.servlet.http.HttpServletResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class AuthControllerTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private JwtService jwtService;
    @Mock private UserService userServiceImpl;
    @Mock private HttpServletResponse response;

    @InjectMocks private AuthController authController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoginUser_success() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("test@example.com");
        loginDTO.setPassword("pass");

        Authentication auth = mock(Authentication.class);
        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(jwtService.generateJwtToken(any())).thenReturn("jwt-token");

        doReturn(
            List.of("ROLE_ADMIN", "ROLE_USER", "ROLE_MODERATOR")
                .stream()
                .map(role -> new SimpleGrantedAuthority(role))
                .collect(Collectors.toList())
        ).when(userDetails).getAuthorities();
        when(userDetails.getId()).thenReturn(UUID.randomUUID());
        when(userDetails.getFirstName()).thenReturn("John");
        when(userDetails.getMiddleName()).thenReturn("Doe");
        when(userDetails.getLastName()).thenReturn("Smith");
        when(userDetails.getEmail()).thenReturn("test@example.com");

        ResponseEntity<?> result = authController.loginUser(loginDTO, response);
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void testCreateUser_success() {
        SignupDTO signupDTO = new SignupDTO();
        signupDTO.setEmail("newuser@example.com");
        signupDTO.setPassword("123456");
        signupDTO.setFirstName("Test");
        signupDTO.setDob(LocalDate.now());

        signupDTO.setRoles(Set.of(ERole.ROLE_ADMIN, ERole.ROLE_MANAGER));

        when(userRepository.existsByEmail(signupDTO.getEmail())).thenReturn(false);


        when(roleRepository.findByName(ERole.ROLE_ADMIN)).thenReturn(Optional.of(new RoleEntity(UUID.randomUUID(), ERole.ROLE_ADMIN)));
        when(roleRepository.findByName(ERole.ROLE_MANAGER)).thenReturn(Optional.of(new RoleEntity(UUID.randomUUID(), ERole.ROLE_MANAGER)));
        when(roleRepository.findByName(ERole.ROLE_SUPERADMIN)).thenReturn(Optional.of(new RoleEntity(UUID.randomUUID(), ERole.ROLE_SUPERADMIN)));

        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(jwtService.generateJwtToken(any())).thenReturn("jwt-token");

        ResponseEntity<?> response = authController.createUser(signupDTO, this.response);
        assertEquals(201, response.getStatusCode().value());
    }
}
