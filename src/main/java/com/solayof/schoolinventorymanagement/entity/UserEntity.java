package com.solayof.schoolinventorymanagement.entity;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.solayof.schoolinventorymanagement.dtos.UpdateUserDTO;

import jakarta.persistence.*;

import lombok.*;


@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "firstName", nullable = false)
    private String firstName;
    @Column(name = "middleName")
    private String middleName;
    @Column(name = "lastName", nullable = false)
    private String lastName;
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    @Column(name = "phone", unique = true)
    private String phone;
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Column(name = "dob", nullable = false)
    private LocalDate dob;
    @Column(name = "gender")
    private String gender;
    @JsonIgnore
    @Column(name = "password", nullable = false)
    private String password;
    @UpdateTimestamp
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "updated_at")
    @UpdateTimestamp
    private Instant updatedAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_to_roles",
        joinColumns = @JoinColumn(name="user_id"),
        inverseJoinColumns = @JoinColumn(name="role_id"))
    private Set<RoleEntity> roles = new HashSet<>();

    public UserEntity updateEntity(UpdateUserDTO userDTO) {
        if (userDTO.getFirstName() != null) this.setFirstName(userDTO.getFirstName());
        if (userDTO.getGender() != null) this.setGender(userDTO.getGender());
        if (userDTO.getLastName() != null) this.setLastName(userDTO.getLastName());
        if (userDTO.getMiddleName() != null) this.setMiddleName(userDTO.getMiddleName());
        if (userDTO.getDob() != null) this.setDob(userDTO.getDob());
        if (userDTO.getEmail() != null) this.setEmail(userDTO.getEmail());
        // if (userDTO.getPassword() != null) this.setPassword(userDTO.getPassword());
        return this;
    }
}
