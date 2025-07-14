package com.solayof.schoolinventorymanagement.entity;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.solayof.schoolinventorymanagement.constants.Status;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne; 
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing an Item in the inventory.
 * Maps to the 'items' table in the PostgreSQL database.
 */
@Data // Lombok annotation to generate getters, setters, equals, hashCode, and toString methods
@Table(name = "items") // Specifies the table name in the database
@Entity
@NoArgsConstructor // Lombok annotation to generate a no-args constructor
@AllArgsConstructor // Lombok annotation to generate a constructor with all fields
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @Column(name = "name", nullable = false, unique = true) // Column for item name, must be unique and not null
    private String name; // Name of the item, must be unique and not null

    @Column(name = "description", columnDefinition = "TEXT") // Allows for longer text in the description
    private String description; // Description of the item

    @Column(name = "serial_number", unique = true, nullable = false)
    private String serialNumber; // Unique identifier for the item, e.g., a serial number

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING) // Maps the enum to a string in the database
    private Status status; // e.g., "AVAILABLE", "ASSIGNED", "RETURNED"

    @CreationTimestamp // Automatically sets the creation timestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt; // Maps to TIMESTAMP WITH TIME ZONE

    @UpdateTimestamp // Automatically updates the timestamp on entity update
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt; // Maps to TIMESTAMP WITH TIME ZONE

    // Many-to-One relationship with Category: many items belong to one category
    @ManyToOne(fetch = FetchType.LAZY) // Lazy loading for performance
    @JoinColumn(name = "category_id", nullable = false) // Foreign key column
    private Category category; // This field represents the category to which the item belongs.

    // One-to-One relationship with Assignment: one item can have at most one active assignment.
    // The foreign key constraint with ON DELETE RESTRICT will handle preventing Item deletion
    // if an Assignment still references it at the database level.
    @OneToOne(fetch = FetchType.LAZY, optional = true) // Optional means the item may not be assigned
    @JoinColumn(name = "assignment_id", referencedColumnName = "id",
    foreignKey = @ForeignKey(name = "fk_item_assignment",
    foreignKeyDefinition = "FOREIGN KEY (assignment_id) REFERENCES assignments(id) ON DELETE RESTRICT")) // Ensures that the item cannot be deleted if it is still assigned
    private Assignment assignment; // This field represents the assignment of the item, if any.
    // If the item is not assigned, this field will be null.
}
