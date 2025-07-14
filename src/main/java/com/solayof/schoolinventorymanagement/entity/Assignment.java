package com.solayof.schoolinventorymanagement.entity;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing an Assignment of an item to a collector.
 * Maps to the 'assignments' table in the PostgreSQL database.
 */
@Entity
@Table(name = "assignments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // Auto-incrementing primary key
    private UUID id;

    @Column(name = "assignment_date", nullable = false)
    private LocalDate assignmentDate; // Maps to DATE

    @Column(name = "return_due_date")
    private LocalDate returnDueDate; // Maps to DATE (nullable)

    @Column(name = "actual_return_date")
    private LocalDate actualReturnDate; // Maps to DATE (nullable)

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false) // Maps to TIMESTAMP WITH TIME ZONE
    private Instant createdAt; // Maps to TIMESTAMP WITH TIME ZONE

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false) // Maps to TIMESTAMP WITH TIME ZONE
    private Instant updatedAt; // Maps to TIMESTAMP WITH TIME ZONE

    // One-to-One relationship with Item
    @OneToOne(fetch = FetchType.LAZY) // Lazy loading for performance
    @JoinColumn(name = "item_id", nullable = false) // Foreign key column, must not be null
    private Item item; // This field represents the item being assigned, must not be null.

    // Many-to-One relationship with Collector
    @ManyToOne(fetch = FetchType.LAZY)  // Lazy loading for performance
    @JoinColumn(name = "collector_id", nullable = false) // Foreign key column, must not be null
    private Collector collector; // This field represents the collector to whom the item is assigned, must not be null.

    // One-to-Many relationship with Reminder: one assignment can have many reminders
    @OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL, orphanRemoval = true) // mappedBy indicates the field in the Reminder entity that owns the relationship
    private Set<Reminder> reminders = new HashSet<>(); // Initialize to prevent NullPointerException
}