package com.solayof.schoolinventorymanagement.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.solayof.schoolinventorymanagement.entity.UserEntity;
import com.solayof.schoolinventorymanagement.exceptions.UserNotFoundException;
import com.solayof.schoolinventorymanagement.repository.UserRepository;
import com.solayof.schoolinventorymanagement.services.UserDetailsImpl;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class CommonUtilTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetailsImpl userDetails;

    @InjectMocks
    private CommonUtil commonUtil;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
    }

    @Test
    public void testLoggedInUser_returnsUserDetails() {
        UserDetailsImpl result = commonUtil.loggedInUser();

        assertThat(result).isNotNull();
        verify(securityContext).getAuthentication();
        verify(authentication).getPrincipal();
    }

    @Test
    public void testLoggedInUserEmail_returnsCorrectEmail() {
        when(userDetails.getEmail()).thenReturn("test@example.com");

        String email = commonUtil.loggedInUserEmail();

        assertThat(email).isEqualTo("test@example.com");
    }

    @Test
    public void testLoggedInUserEntity_returnsUserEntity() {
        String email = "test@example.com";
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);

        when(userDetails.getEmail()).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));

        UserEntity result = commonUtil.loggedInUserEntity();

        assertThat(result).isEqualTo(userEntity);
    }

    @Test
    public void testLoggedInUserEntity_userNotFound_throwsException() {
        String email = "missing@example.com";

        when(userDetails.getEmail()).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> commonUtil.loggedInUserEntity());
    }
        
}