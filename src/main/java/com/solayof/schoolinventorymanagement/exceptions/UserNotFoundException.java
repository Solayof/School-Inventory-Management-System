package com.solayof.schoolinventorymanagement.exceptions;

import java.util.UUID;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(UUID id) {
        super("Could not find user with id: " + id);
    }

    public UserNotFoundException(String email) {
        super("Could not find user with email: " + email);
    }
}
