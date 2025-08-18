package com.solayof.schoolinventorymanagement.dtos;

import java.time.LocalDate;
import java.util.Set;

import com.solayof.schoolinventorymanagement.constants.ERole;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDTO {
    @Size(max = 25)
    private String firstName;
    @Size(max = 25)
    private String middleName;
    @Size(max = 25)
    private String lastName;
    @Size(max = 50)
    private String email;
    private LocalDate dob;
    private String gender;
    private String password;
    private Set<ERole> roles;
}
