# School Inventory Management Backend System

## Table of Contents

 1. [Introduction](https://www.google.com/search?q=%231-introduction)

 2. [Project Goal](https://www.google.com/search?q=%232-project-goal)

 3. [Target Audience](https://www.google.com/search?q=%233-target-audience)

 4. [Pain Points Addressed](https://www.google.com/search?q=%234-pain-points-addressed)

 5. [Features](https://www.google.com/search?q=%235-features)

 6. [Technical Stack](https://www.google.com/search?q=%236-technical-stack)

 7. [Database Schema](https://www.google.com/search?q=%237-database-schema)

 8. [API Endpoints](https://www.google.com/search?q=%238-api-endpoints)

 9. [Getting Started](https://www.google.com/search?q=%239-getting-started)

    * [Prerequisites](https://www.google.com/search?q=%23prerequisites)

    * [Database Setup](https://www.google.com/search?q=%23database-setup)

    * [Project Configuration](https://www.google.com/search?q=%23project-configuration)

    * [Building and Running the Application](https://www.google.com/search?q=%23building-and-running-the-application)

    * [Authentication](https://www.google.com/search?q=%23authentication)

10. [Future Enhancements](https://www.google.com/search?q=%2310-future-enhancements)

11. [Contributing](https://www.google.com/search?q=%2311-contributing)

12. [License](https://www.google.com/search?q=%2312-license)

## 1. Introduction

This project is a robust backend system developed using Spring Boot for managing inventory within a school environment. It provides a centralized, automated, and secure platform to track school assets, manage their assignments to individuals (collectors), and ensure timely returns through automated reminders. The system aims to enhance overall inventory control, improve accountability, and reduce asset loss.

## 2. Project Goal

To develop a robust backend system for managing school inventory, including item tracking, collector assignment, and automated return reminders, thereby enhancing inventory control and accountability.

## 3. Target Audience

This system is primarily designed for:

* **School Administrators**: For overall oversight and reporting.

* **Inventory Managers**: For day-to-day management of items, categories, and assignments.

* **Authorized Personnel**: Such as teachers or staff who borrow items and need to be reminded of return dates.

## 4. Pain Points Addressed

The system addresses several critical challenges commonly faced in school inventory management:

* **Lack of Centralized Tracking**: Replaces manual, fragmented, or non-existent inventory records with a single, authoritative database.

* **Poor Accountability**: Establishes clear responsibility for assigned items, reducing instances of loss or unreturned assets.

* **Inefficient Manual Reminders**: Automates the time-consuming and error-prone process of tracking due dates and sending return reminders.

* **Difficulty in Generating Reports**: Provides easy access to real-time data and comprehensive reports on inventory status and collector assignments.

* **Inconsistent Item Status Management**: Ensures accurate and up-to-date information on item availability (e.g., available, assigned, returned).

* **Security Concerns**: Implements robust access control to protect sensitive inventory data from unauthorized access or modification.

## 5. Features

The backend system provides the following key functionalities:

* **Inventory Item Management**:

  * Add, view, update, and delete individual inventory items (e.g., laptops, textbooks, lab equipment).

  * Each item has a unique serial number and a current status.

* **Category Management**:

  * Define and manage categories for inventory items (e.g., "Electronics", "Books", "Equipment").

* **Collector Management**:

  * Manage information about individuals who borrow items, including their name and contact details (email).

* **Item Assignment and Tracking**:

  * Assign specific inventory items to collectors with designated return due dates.

  * Track the status of items (e.g., `AVAILABLE`, `ASSIGNED`, `RETURNED`).

  * Mark items as returned, updating their status and recording the actual return date.

* **Automated Return Reminders**:

  * A scheduled task automatically identifies overdue assignments daily.

  * Sends email reminders to collectors for items that are past their due date.

  * An API endpoint to manually trigger reminders for specific assignments.

* **Reporting**:

  * Generate reports on current inventory levels (counts by status, counts by category).

  * Generate reports on collector assignments, including a summary of overdue items per collector.

* **Security**:

  * User authentication and role-based authorization using Spring Security.

  * Secure API endpoints with roles like `ADMIN`, `INVENTORY_MANAGER`, and `COLLECTOR`.

  * Password hashing for secure user credentials.

## 6. Technical Stack

* **Backend Framework**: Spring Boot (Java 17)

* **Database**: PostgreSQL

* **ORM**: JPA / Hibernate

* **Data Access**: Spring Data JPA

* **Security**: Spring Security

* **Utility**: Lombok (for boilerplate code reduction)

* **Email Sending**: Spring Boot Starter Mail (JavaMail Sender)

* **Build Tool**: Maven

## 7. Database Schema

The PostgreSQL database schema is designed with the following tables, utilizing `UUID` for primary and foreign keys for scalability and uniqueness:

* **`categories`**: `id` (UUID, PK), `name` (VARCHAR, UNIQUE), `description` (TEXT), `created_at`, `updated_at`

* **`collectors`**: `id` (UUID, PK), `name` (VARCHAR), `contact_information` (VARCHAR), `email` (VARCHAR, UNIQUE), `created_at`, `updated_at`

* **`assignments`**: `id` (UUID, PK), `assignment_date` (DATE), `return_due_date` (DATE), `actual_return_date` (DATE), `collector_id` (UUID, FK), `created_at`, `updated_at`

* **`items`**: `id` (UUID, PK), `name` (VARCHAR, UNIQUE), `description` (TEXT), `category_id` (UUID, FK), `serial_number` (VARCHAR, UNIQUE), `status` (VARCHAR), `assignment_id` (UUID, FK, UNIQUE), `created_at`, `updated_at`

* **`reminders`**: `id` (UUID, PK), `assignment_id` (UUID, FK), `reminder_date` (DATE), `status` (VARCHAR), `message` (TEXT), `sent_at` (TIMESTAMP WITH TIME ZONE), `created_at`, `updated_at`

**Relationships:**

* `Category` 1:N `Item`

* `Collector` 1:N `Assignment`

* `Assignment` 1:N `Reminder`

* `Item` 1:1 `Assignment` (An item can have at most one active assignment, with `items.assignment_id` being the foreign key.)

## 8. API Endpoints

The following RESTful API endpoints are exposed:

| **Endpoint** | **HTTP Method** | **Description** | **Roles Required** |
| `/api/categories` | `POST` | Create a new category | `ADMIN`, `INVENTORY_MANAGER` |
| `/api/categories` | `GET` | Get all categories | `ADMIN`, `INVENTORY_MANAGER`, `COLLECTOR` |
| `/api/categories/{id}` | `GET` | Get category by ID | `ADMIN`, `INVENTORY_MANAGER`, `COLLECTOR` |
| `/api/categories/{id}` | `PUT` | Update category by ID | `ADMIN`, `INVENTORY_MANAGER` |
| `/api/categories/{id}` | `DELETE` | Delete category by ID | `ADMIN` |
| `/api/items` | `POST` | Create a new item (requires `categoryId` as `@RequestParam`) | `ADMIN`, `INVENTORY_MANAGER` |
| `/api/items` | `GET` | Get all items | `ADMIN`, `INVENTORY_MANAGER`, `COLLECTOR` |
| `/api/items/{id}` | `GET` | Get item by ID | `ADMIN`, `INVENTORY_MANAGER`, `COLLECTOR` |
| `/api/items/serial/{serialNumber}` | `GET` | Get item by serial number | `ADMIN`, `INVENTORY_MANAGER`, `COLLECTOR` |
| `/api/items/status/{status}` | `GET` | Get items by status (e.g., `AVAILABLE`, `ASSIGNED`) | `ADMIN`, `INVENTORY_MANAGER`, `COLLECTOR` |
| `/api/items/category/{categoryId}` | `GET` | Get items by category ID | `ADMIN`, `INVENTORY_MANAGER`, `COLLECTOR` |
| `/api/items/{id}` | `PUT` | Update item by ID (requires `categoryId` as `@RequestParam`) | `ADMIN`, `INVENTORY_MANAGER` |
| `/api/items/{id}` | `DELETE` | Delete item by ID | `ADMIN` |
| `/api/collectors` | `POST` | Create a new collector | `ADMIN`, `INVENTORY_MANAGER` |
| `/api/collectors` | `GET` | Get all collectors | `ADMIN`, `INVENTORY_MANAGER`, `COLLECTOR` |
| `/api/collectors/{id}` | `GET` | Get collector by ID | `ADMIN`, `INVENTORY_MANAGER`, `COLLECTOR` |
| `/api/collectors/email/{email}` | `GET` | Get collector by email | `ADMIN`, `INVENTORY_MANAGER`, `COLLECTOR` |
| `/api/collectors/{id}` | `PUT` | Update collector by ID | `ADMIN`, `INVENTORY_MANAGER` |
| `/api/collectors/{id}` | `DELETE` | Delete collector by ID | `ADMIN` |
| `/api/assignments` | `POST` | Create a new assignment (requires `itemId`, `collectorId`, `returnDueDate` (optional)) | `ADMIN`, `INVENTORY_MANAGER` |
| `/api/assignments` | `GET` | Get all assignments | `ADMIN`, `INVENTORY_MANAGER`, `COLLECTOR` |
| `/api/assignments/{id}` | `GET` | Get assignment by ID | `ADMIN`, `INVENTORY_MANAGER`, `COLLECTOR` |
| `/api/assignments/collector/{collectorId}` | `GET` | Get assignments by collector ID | `ADMIN`, `INVENTORY_MANAGER`, `COLLECTOR` |
| `/api/assignments/item/{itemId}` | `GET` | Get assignments by item ID | `ADMIN`, `INVENTORY_MANAGER`, `COLLECTOR` |
| `/api/assignments/overdue` | `GET` | Get all overdue assignments | `ADMIN`, `INVENTORY_MANAGER` |
| `/api/assignments/{id}/update-due-date` | `PUT` | Update assignment return due date | `ADMIN`, `INVENTORY_MANAGER` |
| `/api/assignments/{id}/return` | `PUT` | Mark an item as returned for a given assignment | `ADMIN`, `INVENTORY_MANAGER` |
| `/api/assignments/{id}` | `DELETE` | Delete assignment by ID | `ADMIN` |
| `/api/reminders/manual-send/{assignmentId}` | `POST` | Manually send a reminder for an assignment (optional `message`) | `ADMIN`, `INVENTORY_MANAGER` |
| `/api/reminders` | `GET` | Get all reminders | `ADMIN`, `INVENTORY_MANAGER` |
| `/api/reminders/{id}` | `GET` | Get reminder by ID | `ADMIN`, `INVENTORY_MANAGER` |
| `/api/reminders/assignment/{assignmentId}` | `GET` | Get reminders by assignment ID | `ADMIN`, `INVENTORY_MANAGER`, `COLLECTOR` |
| `/api/reminders/{id}/status` | `PUT` | Update reminder status | `ADMIN`, `INVENTORY_MANAGER` |
| `/api/reminders/{id}` | `DELETE` | Delete reminder by ID | `ADMIN` |
| `/api/reports/inventory-levels` | `GET` | Get report on inventory levels (counts by status and category) | `ADMIN`, `INVENTORY_MANAGER` |
| `/api/reports/collector-assignments` | `GET` | Get report on collector assignments (active and overdue summaries) | `ADMIN`, `INVENTORY_MANAGER` |

## 9. Getting Started

Follow these steps to set up and run the School Inventory Management Backend System locally.

### Prerequisites

Before you begin, ensure you have the following installed:

* **Java Development Kit (JDK) 17 or higher**: [Download Oracle JDK](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/install/)

* **Apache Maven**: [Download Maven](https://maven.apache.org/download.cgi)

* **PostgreSQL Database**: [Download PostgreSQL](https://www.postgresql.org/download/)

* **Email Account for SMTP**: An email account (e.g., Gmail) configured for SMTP. If using Gmail with 2-Factor Authentication, you'll need to generate an "App password" for use in `application.properties`.

### Database Setup

1. **Create Database**:
   Create a new PostgreSQL database. For example, using `psql`:

   ```sql
   CREATE DATABASE school_inventory_db;
   ```

## 10. Project Configuration

1. Clone the Repository

    ```bash
    git clone <repository-url>
    cd school-inventory-management
    ```

2. Update application.properties

    ```properties
    # Database Configuration (PostgreSQL)
    spring.datasource.url=jdbc:postgresql://localhost:5432/school_inventory_db
    spring.datasource.username=your_postgresql_username
    spring.datasource.password=your_postgresql_password

    # Email Configuration (for JavaMail Sender)
    # Replace with your SMTP server details
    spring.mail.host=smtp.gmail.com
    spring.mail.port=587
    spring.mail.username=your_email@example.com
    spring.mail.password=your_email_app_password # Or your regular password if 2FA is off
    ```

## 11. Building and Running the Application

1. Clean and Build:
Open your terminal or command prompt, navigate to the root directory of the project (where pom.xml is located), and run:

    ```bash
    mvn clean install
    ```

    This command cleans any previous builds, compiles the code, runs tests, and packages the application.

2. Run the Application:
After a successful build, run the Spring Boot application:

    ```bash
    mvn spring-boot:run
    ```

The application will start, typically on `http://localhost:8080.` You will see logs indicating the application startup.
