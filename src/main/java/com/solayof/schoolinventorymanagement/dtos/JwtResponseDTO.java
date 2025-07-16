package com.solayof.schoolinventorymanagement.dtos;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class JwtResponseDTO {
    private String token;
    private String type = "Bearer";
    private UUID id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private List<String> roles;

    public JwtResponseDTO(
        String token,
        UUID id,
        String firstName,
        String middleName,
        String lastName,
        String email,
        List<String> roles
    ) {
        this.token = token;
        this.id = id;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.roles = roles;
    }
}
