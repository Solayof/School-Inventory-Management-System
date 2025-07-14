package com.solayof.schoolinventorymanagement.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.solayof.schoolinventorymanagement.entity.Collector;

public interface CollectorRepository extends JpaRepository<Collector, UUID> {
    Optional<Collector> findByEmail(String email);
}
