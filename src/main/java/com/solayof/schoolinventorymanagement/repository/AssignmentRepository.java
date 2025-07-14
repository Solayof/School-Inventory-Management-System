package com.solayof.schoolinventorymanagement.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.solayof.schoolinventorymanagement.entity.Assignment;


public interface AssignmentRepository extends JpaRepository<Assignment, UUID> {
}
