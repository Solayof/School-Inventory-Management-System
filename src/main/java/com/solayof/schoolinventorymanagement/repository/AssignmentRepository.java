package com.solayof.schoolinventorymanagement.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.solayof.schoolinventorymanagement.entity.Assignment;


public interface AssignmentRepository extends JpaRepository<Assignment, UUID> {
    List<Assignment> findByReturnDueDateBeforeAndActualReturnDateIsNull(LocalDate dueDate);
}
