package com.solayof.schoolinventorymanagement.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.solayof.schoolinventorymanagement.constants.ReminderStatus;
import com.solayof.schoolinventorymanagement.entity.Reminder;

public interface ReminderRepository extends JpaRepository<Reminder, UUID> {
    List<Reminder> findByStatus(ReminderStatus status);

    @Query("""
        SELECT r FROM Reminder r
        WHERE r.reminderDate BETWEEN :startTime AND :endTime
        AND r.status IN (:statuses)
    """)
    List<Reminder> findRemindersDueWithinLastHour(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("statuses") List<ReminderStatus> statuses
    );
}
