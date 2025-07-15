package com.solayof.schoolinventorymanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.solayof.schoolinventorymanagement.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, UUID>{
   Optional<Category> findByName(String name);
   Boolean existsByName(String name);
   List<Category> findByNameContainingIgnoreCase(String name);
   boolean existsById(@NonNull UUID id); 
}
