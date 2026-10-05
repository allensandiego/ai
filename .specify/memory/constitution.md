<!-- Sync Impact Report -->
<!-- Version Change: N/A → 1.1.0 (initial constitution for Spring Boot benchmark application) -->
<!-- Added Sections: Core Principles (I-V), Technical Standards, Development Workflow, Governance -->
<!-- Purpose: Define principles for building a working Spring Boot 4.1.1 application with JDBC authentication and Playwright E2E testing -->

# Local AI Application Benchmark Constitution

## Core Principles

### I. Spring Boot First (NON-NEGOTIABLE)
Every feature must be implemented using Spring Boot conventions and patterns. Controllers handle HTTP routing, Services contain business logic, Repositories manage persistence. The application MUST follow the standard MVC architecture with clear separation of concerns between presentation, business logic, and data access layers.

### II. Database Schema Alignment (NON-NEGOTIABLE)
All data models MUST match the schema.sql data model exactly. Any database changes must be explicitly versioned and documented as migrations that preserve backward compatibility. Entity classes MUST map directly to database tables with proper JPA annotations for relationships, constraints, and column definitions.

### III. JDBC Authentication (NON-NEGOTIABLE)
User authentication MUST use JDBC-based UserDetailsService with Spring Security form login. Passwords MUST be hashed using BCrypt before storage; plaintext passwords are strictly forbidden. The authentication mechanism MUST validate credentials against the users table without email confirmation or account lockout features.

### IV. Integration Testing (NON-NEGOTIABLE)
All new features MUST include integration tests that verify end-to-end functionality. E2E testing with Playwright is required for user-facing flows including login, registration, password reset, and dashboard navigation. Unit tests alone are insufficient; integration coverage must validate database interactions, security constraints, and frontend-backend communication.

### V. Observability and Debuggability
The application MUST produce structured logs with sufficient detail to trace requests through the system. Error conditions MUST be logged at appropriate severity levels (ERROR for failures, WARN for recoverable issues). Health endpoints MUST expose application status for monitoring and operational visibility.

## Technical Standards

### Technology Stack Requirements
- Java 21 with all language features enabled
- Spring Boot 4.1.1 as the application framework
- Thymeleaf for server-side HTML templating with CoreUI Bootstrap 5 components
- H2 database for development/testing via spring-boot-starter-h2console
- Maven as the build system with dependency management
- Playwright for browser automation and E2E testing

### Security Requirements
- Spring Security form login with session-based authentication
- Passwords hashed with BCrypt (minimum cost factor 10)
- Public endpoints only for static assets (/css/**, /js/**, /images/**) and public pages
- Logout functionality with proper session invalidation
- CSRF protection enabled by default with Thymeleaf integration

### Code Organization Standards
- Controllers must reside in a dedicated controller package with clear naming
- Services must contain business logic separate from data access concerns
- Repositories must extend Spring Data JPA interfaces with custom queries where needed
- Templates must follow CoreUI structure for consistent layout and navigation
- Configuration classes must use @Configuration annotations with appropriate bean definitions

## Development Workflow

### Implementation Process
1. Define data models matching schema.sql before implementing features
2. Create repository interfaces for data access
3. Implement service layer business logic
4. Wire controllers to services with proper request/response handling
5. Create Thymeleaf templates using CoreUI components
6. Write integration tests covering the complete user flow

### Testing Requirements
- Integration tests MUST verify database operations via @DataJpaTest or @SpringBootTest
- Security tests MUST validate authentication and authorization flows
- E2E tests MUST cover critical user journeys with Playwright
- Tests MUST be deterministic and use test data fixtures

### Quality Gates
- All new code must compile successfully with Maven
- Application MUST start without errors using ./mvnw spring-boot:run
- Integration tests MUST pass before deployment consideration
- Console output MUST not show unhandled exceptions or warnings in normal operation

## Governance

This constitution supersedes all other practices and conventions for this project. Amendments require:
1. Documentation of changes with rationale
2. Verification of schema compatibility if data models change
3. Security review if authentication/authorization is affected
4. Version increment following semantic versioning rules

The constitution serves as the single source of truth for development decisions and MUST be referenced when evaluating feature implementations, architectural changes, or security modifications.

**Version**: 1.1.0 | **Ratified**: 2026-10-05 | **Last Amended**: 2026-10-05
