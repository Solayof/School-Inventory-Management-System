package com.solayof.schoolinventorymanagement.entity;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.solayof.schoolinventorymanagement.constants.ReminderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Entity representing a Reminder for an assignment.
 * Maps to the 'reminders' table in the PostgreSQL database.
 */
@Entity
@Table(name = "reminders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // Auto-incrementing primary key
    private UUID id;

    @Column(name = "reminder_date", nullable = false)
    private LocalDate reminderDate; // Maps to DATE

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING) // Maps the enum to a string in the database
    private ReminderStatus status; // e.g., "PENDING", "SENT", "FAILED", "DISMISSED"

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "sent_at")
    private Instant sentAt; // Maps to TIMESTAMP WITH TIME ZONE (nullable)

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // Many-to-One relationship with Assignment
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;
}
