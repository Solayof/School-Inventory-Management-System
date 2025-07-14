package com.solayof.schoolinventorymanagement.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.solayof.schoolinventorymanagement.constants.ReminderStatus;
import com.solayof.schoolinventorymanagement.entity.Reminder;

public interface ReminderRepository extends JpaRepository<Reminder, UUID> {
    List<Reminder> findByStatus(ReminderStatus status);
}
