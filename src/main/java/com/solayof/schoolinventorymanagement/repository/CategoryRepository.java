package com.solayof.schoolinventorymanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

import com.solayof.schoolinventorymanagement.constants.CategoryName;
import com.solayof.schoolinventorymanagement.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, UUID>{
   Optional<Category> findByName(CategoryName name); 
}
