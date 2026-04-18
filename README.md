# Gym Management Backend API

A production-style Spring Boot REST API for managing gym operations, including memberships, customer check-ins, access cards, lockers, and staff administration.

Built with a strong focus on clean architecture, transactional consistency, security, and maintainable backend design.

---

## Tech Stack

- **Language:** Java 25
- **Framework:** Spring Boot 4.0.1
- **Modules:** Spring Web MVC, Spring Data JPA, Spring Security, Validation
- **Build Tool:** Maven
- **Database:** PostgreSQL
- **Object Mapping:** MapStruct
- **Authentication:** JWT
- **Utilities:** Lombok
- **Testing:** JUnit, Mockito
- **Containerization:** Docker

---

## Project Goals

This project was built to demonstrate practical backend engineering skills through a realistic business system.

Core goals included:

- Designing a production-style REST API with clean layered architecture.
- Implementing real-world business workflows beyond basic CRUD operations.
- Building secure authentication and authorization using JWT and Spring Security.
- Ensuring transactional consistency for time-sensitive operations such as check-ins and membership activation.
- Creating scalable search, filtering, and pagination endpoints for administrative use cases.
- Applying strong validation and fail-fast error handling to protect data integrity.
- Designing maintainable relational data models with PostgreSQL and JPA.
- Writing clean, extensible backend code following professional development practices.
- Providing frontend-ready APIs for integration with a React client application.
- Demonstrating deployment readiness through Docker-based local setup.

---
## Key Features

### Membership Lifecycle Management
- Supports time-based and visit-based memberships.
- Automatically expires finished memberships.
- When a visit-based membership is exhausted during check-in, the next pending membership is activated automatically.
- Prevents check-ins when no valid membership is available.

### Member Check-In System
- Tracks gym visits with consistent transaction timestamps.
- Ensures membership activation and visit creation occur in the same business flow.

### Access & Facility Management
- Locker assignment management.
- Access card management.
- Staff administration endpoints.

### Validation & Error Handling
- Input validation using Spring Validation.
- Fail-fast business rules to prevent invalid operations.
- Clear exception handling for predictable API responses.

### Security
- Stateless authentication using JWT.
- Custom request filter for token validation.
- Role-based authorization for protected staff operations.
- BCrypt password hashing.
- CORS configuration for frontend integration.

### Data Access & Performance
- Spring Data JPA repositories.
- Custom database queries where needed.
- Optimized entity relationships and persistence flow.

### Search, Filtering & Pagination
- Implemented advanced search endpoints for core entities such as visits, customers, staff, lockers, and access cards.
- Supports optional multi-parameter filtering (email, status, date ranges, locker number, assigned staff, etc.).
- Built paginated responses using Spring Pageable for scalable result browsing.
- Uses optimized JPQL queries with projections to return lightweight response models.
- Designed for real operational use cases such as visit history lookup, reporting, and staff-side administration.

---

## Architecture

Structured using a layered backend architecture:

- **Controller Layer** – REST endpoints
- **Service Layer** – Business logic
- **Repository Layer** – Database access
- **DTO Layer** – Request/response models
- **Mapper Layer** – Entity/DTO conversion via MapStruct

---
## Business Flow Examples

### New Customer Registration

1. Staff creates a new customer profile and assigns an access card.
2. System validates the provided data and stores the customer record.
3. Customer purchases a selected membership plan.
4. Staff performs the first check-in, activating the membership.

### Membership Check-In Process

1. Customer presents an access card for check-in.
2. System validates whether an active membership exists.
3. If the current membership has no remaining visits:
    - marks it as finished
    - activates the next pending membership automatically
4. If no valid membership is available:
    - check-in is rejected
5. System creates a visit record and returns updated membership status.

### Locker Reassignment

1. Customer reports that the assigned locker is unavailable or damaged.
2. Staff assigns a replacement locker either automatically or manually.
3. Staff changes locker status to `OUT_OF_ORDER`.
4. Out-of-order lockers remain unavailable until someone changes their status back to `AVAILABLE`.
---

## Installation

### Requirements

- JDK 25
- Maven 3.x
- PostgreSQL

### Setup

1. Clone the repository
    ```bash
    git clone <repository-url>
    cd <project-folder>
    ```
2. Configure:
    - application.properties
    - .env

3. Build the project
    ```
   mvn clean install
   ```

4. Run the application
    ```
   mvn spring-boot:run
   ```
   
### Alternative setup (Docker)
1. Clone the repository
    ```bash
    git clone <repository-url>
    cd <project-folder>
    ```
2. Run docker compose
    ```bash
   docker-compose up --build
   ```
3. To clean up, including the database, run
    ```bash
   docker-compose down -v
   ```
   
## Frontend Integration
- Example frontend for this API can be found in my [Gym Management Frontend](https://github.com/floatyserve/gym_app_frontend) repository.
