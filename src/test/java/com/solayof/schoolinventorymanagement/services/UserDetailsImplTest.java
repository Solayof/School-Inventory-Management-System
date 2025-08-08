package com.solayof.schoolinventorymanagement.services;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.solayof.schoolinventorymanagement.constants.ERole;
import com.solayof.schoolinventorymanagement.entity.RoleEntity;
import com.solayof.schoolinventorymanagement.entity.UserEntity;

import static org.junit.jupiter.api.Assertions.*;

class UserDetailsImplTest {

    @Test
    void testBuildFromUserEntity_withRoles() {
        UUID id = UUID.randomUUID();
        LocalDate dob = LocalDate.of(2000, 1, 1);

        RoleEntity adminRole = new RoleEntity();
        adminRole.setName(ERole.ROLE_ADMIN);

        Set<RoleEntity> roles = new HashSet<>();
        roles.add(adminRole);

        UserEntity user = new UserEntity();
        user.setId(id);
        user.setFirstName("John");
        user.setMiddleName("K");
        user.setLastName("Doe");
        user.setEmail("john@example.com");
        user.setPhone("1234567890");
        user.setGender("Male");
        user.setPassword("secret");
        user.setDob(dob);
        user.setRoles(roles);

        UserDetailsImpl details = UserDetailsImpl.build(user);

        assertEquals(id, details.getId());
        assertEquals("John", details.getFirstName());
        assertEquals("K", details.getMiddleName());
        assertEquals("Doe", details.getLastName());
        assertEquals("john@example.com", details.getUsername());
        assertEquals("john@example.com", details.getEmail());
        assertEquals("1234567890", details.getPhone());
        assertEquals("Male", details.getGender());
        assertEquals("secret", details.getPassword());
        assertEquals(dob, details.getDob());

        Collection<? extends GrantedAuthority> authorities = details.getAuthorities();
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    void testAccountStatusFlags_allTrue() {
        UserDetailsImpl user = new UserDetailsImplTest().createUserWithNoRoles();
        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());
    }

    @Test
    void testBuildWithNoRoles_shouldHaveEmptyAuthorities() {
        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setEmail("noroles@example.com");
        user.setPassword("pass");
        user.setRoles(new HashSet<>());

        UserDetailsImpl details = UserDetailsImpl.build(user);
        assertNotNull(details.getAuthorities());
        assertTrue(details.getAuthorities().isEmpty());
    }

    @Test
    void testBuildWithNullRoles_shouldHandleGracefully() {
        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setEmail("nullroles@example.com");
        user.setPassword("pass");
        user.setRoles(null); // Intentionally set roles to null

        assertThrows(NullPointerException.class, () -> {
            UserDetailsImpl.build(user);
        });
    }

    private UserDetailsImpl createUserWithNoRoles() {
        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setEmail("test@example.com");
        user.setPassword("pass");
        user.setRoles(new HashSet<>());

        return UserDetailsImpl.build(user);
    }
}
