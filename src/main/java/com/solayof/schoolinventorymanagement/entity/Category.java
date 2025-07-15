package com.solayof.schoolinventorymanagement.entity;

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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing an Inventory Category.
 * Maps to the 'categories' table in the PostgreSQL database.
 */
@Entity
@Table(name = "categories")
@Data // Lombok: Generates getters, setters, toString, equals, and hashCode
@NoArgsConstructor // Lombok: Generates a no-argument constructor
@AllArgsConstructor // Lombok: Generates a constructor with all fields
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // Auto-incrementing primary key
    private UUID id;

    @Column(name = "name", nullable = false, unique = true) // Column for category name, must be unique and not null
    private String name; // Category name

    @Column(name = "description", columnDefinition = "TEXT") // Column for category description, allows for longer text
    private String description; // Category description

    // One-to-Many relationship with Item: one category can have many items
    // mappedBy indicates the field in the Item entity that owns the relationship
    // CascadeType.ALL means all operations (persist, merge, remove, refresh, detach) will cascade
    // orphanRemoval = true means if an item is removed from this category's items set, it will be deleted
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Item> items = new HashSet<>(); // Initialize to prevent NullPointerException

    /**
     * Constructor to create a Category with name and description.
     *
     * @param name        the name of the category
     * @param description the description of the category
     */
    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

}