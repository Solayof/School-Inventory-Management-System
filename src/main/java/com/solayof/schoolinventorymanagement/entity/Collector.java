package com.solayof.schoolinventorymanagement.entity;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


/**
 * Entity representing a Collector of inventory items.
 * Maps to the 'collectors' table in the PostgreSQL database.
 */
@Entity
@Table(name = "collectors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Collector {

    @Id // Primary key for the collector entity
    @GeneratedValue(strategy = GenerationType.AUTO) // Auto-incrementing primary key
    private UUID id; // Auto-incrementing primary key

    @Column(name = "name", nullable = false) // Maps to name, must not be null
    private String name; // Name of the collector, must not be null

    @Column(name = "contact_information") // Maps to contact_information
    private String contactInformation; // Maps to contact_information

    @CreationTimestamp // Automatically sets the creation timestamp
    @Column(name = "created_at", nullable = false, updatable = false) // Maps to TIMESTAMP WITH TIME ZONE
    private Instant createdAt; // Maps to TIMESTAMP WITH TIME ZONE

    @Column(name = "email", nullable = false, unique = true) // Maps to email, must be unique and not null
    private String email; // Email of the collector, must be unique and not null

    @UpdateTimestamp // Automatically updates the timestamp on entity update
    @Column(name = "updated_at", nullable = false) // Maps to TIMESTAMP WITH TIME ZONE
    private Instant updatedAt; // Maps to TIMESTAMP WITH TIME ZONE

    // One-to-Many relationship with Assignment: one collector can have many assignments
    @OneToMany(mappedBy = "collector", cascade = CascadeType.ALL, orphanRemoval = true) // mappedBy indicates the field in the Assignment entity that owns the relationship
    // CascadeType.ALL means all operations (persist, merge, remove, refresh, detach)
    @JsonManagedReference
    @EqualsAndHashCode.Exclude
    private Set<Assignment> assignments = new HashSet<>();

    public Collector(String name, String contactInformation, String email) {
        this.name = name;
        this.contactInformation = contactInformation;
        this.email = email;
    }
}

