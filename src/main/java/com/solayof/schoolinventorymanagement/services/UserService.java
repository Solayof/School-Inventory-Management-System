package com.solayof.schoolinventorymanagement.services;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.solayof.schoolinventorymanagement.entity.UserEntity;
import com.solayof.schoolinventorymanagement.exceptions.UserNotFoundException;
import com.solayof.schoolinventorymanagement.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
        return UserDetailsImpl.build(user);
    }

    public UserEntity getUserById(UUID id) throws UserNotFoundException {
        return userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Transactional
    public String addUser(UserEntity user) {
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);
        return "UserEntity added Sucessfully";
    }

    @Transactional
    public UserEntity save(UserEntity user) {
        return userRepository.save(user);

    }

    @Transactional
    public UserEntity updateUserPassword(UserEntity user) {
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}
