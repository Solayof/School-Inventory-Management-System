package com.solayof.schoolinventorymanagement.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.solayof.schoolinventorymanagement.constants.ERole;
import com.solayof.schoolinventorymanagement.entity.RoleEntity;


public interface RoleRepository extends JpaRepository<RoleEntity, UUID> {
    Optional<RoleEntity> findByName(ERole name);   
}