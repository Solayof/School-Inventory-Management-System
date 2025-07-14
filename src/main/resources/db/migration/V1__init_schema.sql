-- PostgreSQL Database Schema for School Inventory Management System
-- Updated to use UUIDs for primary keys and reflect the Item-Assignment One-to-One relationship.

-- Enable the pgcrypto extension for gen_random_uuid() if not already enabled.
-- You might need superuser privileges to run this command.
-- CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Function to update 'updated_at' columns automatically
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Table for Categories of inventory items (e.g., electronics, books, equipment)
CREATE TABLE categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(), -- UUID primary key, auto-generated
    name VARCHAR(255) UNIQUE NOT NULL,             -- Category name (from CategoryName enum, stored as string)
    description TEXT,                              -- Optional description for the category
    -- No created_at/updated_at for Category in the Java entity, but good practice to include
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Apply trigger for updated_at
CREATE TRIGGER update_categories_updated_at
BEFORE UPDATE ON categories
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();


-- Table for Collectors (e.g., teachers, staff, students who borrow items)
CREATE TABLE collectors (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(), -- UUID primary key, auto-generated
    name VARCHAR(255) NOT NULL,
    contact_information VARCHAR(255),              -- E.g., phone number
    email VARCHAR(255) UNIQUE NOT NULL,            -- Email of the collector, must be unique
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Index for faster lookups on collector email
CREATE UNIQUE INDEX idx_collectors_email ON collectors (email);
-- Apply trigger for updated_at
CREATE TRIGGER update_collectors_updated_at
BEFORE UPDATE ON collectors
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();


-- Table for Assignments of items to collectors
-- Note: This table now represents an assignment instance, and the Item table will hold the foreign key to it.
CREATE TABLE assignments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(), -- UUID primary key, auto-generated
    assignment_date DATE NOT NULL DEFAULT CURRENT_DATE,
    return_due_date DATE,                          -- Optional: Can be NULL if no specific due date
    actual_return_date DATE,                       -- Nullable: Set when the item is actually returned
    collector_id UUID NOT NULL,                    -- Foreign Key to Collectors
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_collector
        FOREIGN KEY (collector_id)
        REFERENCES collectors (id)
        ON DELETE RESTRICT -- Prevent deletion of a collector if they have active assignments
);

-- Index for faster lookups on collector_id in assignments
CREATE INDEX idx_assignments_collector_id ON assignments (collector_id);
-- Index for efficient querying of overdue assignments
CREATE INDEX idx_assignments_due_date ON assignments (return_due_date) WHERE actual_return_date IS NULL;
-- Apply trigger for updated_at
CREATE TRIGGER update_assignments_updated_at
BEFORE UPDATE ON assignments
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();


-- Table for Inventory Items
CREATE TABLE items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(), -- UUID primary key, auto-generated
    name VARCHAR(255) UNIQUE NOT NULL,             -- Item name, must be unique
    description TEXT,
    category_id UUID NOT NULL,                     -- Foreign Key to Categories
    serial_number VARCHAR(255) UNIQUE NOT NULL,    -- Serial number must be unique for each item
    status VARCHAR(50) NOT NULL DEFAULT 'AVAILABLE', -- Status (from Status enum, stored as string)
    -- One-to-One relationship with Assignment. 'assignment_id' is the foreign key.
    -- It's UNIQUE because an item can only have one active assignment at a time.
    assignment_id UUID UNIQUE,                     -- Foreign Key to Assignments, nullable for optional relationship
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_category
        FOREIGN KEY (category_id)
        REFERENCES categories (id)
        ON DELETE RESTRICT, -- Prevent deletion of a category if items are associated with it
    CONSTRAINT fk_item_assignment
        FOREIGN KEY (assignment_id)
        REFERENCES assignments (id)
        ON DELETE RESTRICT -- Prevent deletion of an assignment if an item still references it
);

-- Index for faster lookups on serial_number
CREATE INDEX idx_items_serial_number ON items (serial_number);
-- Index for faster lookups on item name
CREATE INDEX idx_items_name ON items (name);
-- Apply trigger for updated_at
CREATE TRIGGER update_items_updated_at
BEFORE UPDATE ON items
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();


-- Table for Reminders related to assignments
CREATE TABLE reminders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(), -- UUID primary key, auto-generated
    assignment_id UUID NOT NULL,                   -- Foreign Key to Assignments
    reminder_date DATE NOT NULL DEFAULT CURRENT_DATE, -- Date when the reminder was generated/sent
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING', -- Status (e.g., 'PENDING', 'SENT', 'FAILED', 'DISMISSED')
    message TEXT,                                  -- Optional: The content of the reminder message
    sent_at TIMESTAMP WITH TIME ZONE,              -- Timestamp when the reminder was actually sent
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_assignment
        FOREIGN KEY (assignment_id)
        REFERENCES assignments (id)
        ON DELETE CASCADE -- If an assignment is deleted, its reminders are also deleted
);

-- Index for faster lookups on assignment_id in reminders
CREATE INDEX idx_reminders_assignment_id ON reminders (assignment_id);
-- Apply trigger for updated_at
CREATE TRIGGER update_reminders_updated_at
BEFORE UPDATE ON reminders
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
