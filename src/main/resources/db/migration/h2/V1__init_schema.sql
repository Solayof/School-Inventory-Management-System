-- H2-compatible schema for School Inventory Management System
-- UUIDs replaced with VARCHAR(36)
-- PostgreSQL-specific features removed or adapted

-- Functionality like updated_at triggers is omitted for simplicity, or can be handled in application layer

-- Table for Categories
CREATE TABLE categories (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table for Collectors
CREATE TABLE collectors (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    contact_information VARCHAR(255),
    email VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table for Assignments
CREATE TABLE assignments (
    id VARCHAR(36) PRIMARY KEY,
    assignment_date DATE DEFAULT CURRENT_DATE,
    return_due_date DATE,
    actual_return_date DATE,
    collector_id VARCHAR(36) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_collector FOREIGN KEY (collector_id) REFERENCES collectors (id)
);

-- Table for Items
CREATE TABLE items (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    description TEXT,
    category_id VARCHAR(36) NOT NULL,
    serial_number VARCHAR(255) UNIQUE NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'AVAILABLE',
    assignment_id VARCHAR(36) UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES categories (id),
    CONSTRAINT fk_item_assignment FOREIGN KEY (assignment_id) REFERENCES assignments (id)
);

-- Table for Reminders
CREATE TABLE reminders (
    id VARCHAR(36) PRIMARY KEY,
    assignment_id VARCHAR(36) NOT NULL,
    reminder_date DATE DEFAULT CURRENT_DATE,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    message TEXT,
    sent_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_assignment FOREIGN KEY (assignment_id) REFERENCES assignments (id)
);

-- Indexes
CREATE INDEX idx_assignments_collector_id ON assignments (collector_id);
CREATE INDEX idx_items_serial_number ON items (serial_number);
CREATE INDEX idx_items_name ON items (name);
CREATE INDEX idx_reminders_assignment_id ON reminders (assignment_id);
