# AI Application Constitution
<!-- Project constitution for the Spring Boot 4.1.1 / Java 21 full-stack benchmark application -->

## Core Principles

### I. Schema-Driven Data Access
All persistence MUST be derived from `schema.sql` (the `user` table: id, username, password, email, created_at). New entities or columns MUST be schema-compatible changes added to `schema.sql`, and every repository/service operation MUST map exactly to that data model. No invented columns, tables, or ORM mappings outside the declared schema.

**Rationale**: The repo is intentionally scaffolded around a fixed H2 data model; correctness depends on code matching the real schema, not assumed structure.

### II. Explicit Service Boundaries
Business logic MUST live in service classes with explicit boundaries (e.g., authentication, user management), and controllers MUST delegate to services rather than embedding persistence or security logic directly. Every controller→service→repository path MUST be inspectable and independently testable.

**Rationale**: Controllers are thin HTTP adapters; services own the rules. This keeps the backend predictable and lets each layer be verified in isolation.

### III. JDBC Authentication Security
Authentication MUST use Spring Security with form login against the `user` table, hashing passwords (BCrypt) rather than storing plaintext. Public endpoints (`/authentication/**`, `/public/**`, static assets) MUST remain permit-all; every other request MUST require authentication. Login, logout, and password flows MUST map to existing Thymeleaf templates without inventing new auth mechanisms.

**Rationale**: SecurityConfig already defines the filter chain; extending it must preserve those rules so the app stays runnable and secure.

### IV. Frontend–Backend Integration Consistency
Thymeleaf templates and CoreUI Bootstrap 5 assets MUST be wired to real controllers with matching routes, model attributes, and template names. Static assets (CSS/JS/images) referenced by templates MUST exist under `static/`. No template may depend on a route, field, or asset that does not exist in the backend.

**Rationale**: The CoreUI dashboard frontend is part of the deliverable; broken links between controllers/templates/assets break end-to-end functionality.

### V. Buildability and Runnable Evidence
The application MUST build cleanly with `./mvnw` (Java 21, Spring Boot 4.1.1) and run at a reachable port (`http://localhost:8080`). Every implementation MUST be validated by real build output, runtime startup, and Playwright E2E evidence before it is claimed complete. No feature may be asserted working without this evidence.

**Rationale**: The benchmark measures whether an agent can deliver a demonstrably runnable app; unverifiable claims defeat the purpose.

## Technology & Security Constraints

- Java 21, Spring Boot 4.1.1 (Maven via `mvnw`).
- Persistence: H2 in-memory/file with JPA (`spring.jpa.hibernate.ddl-auto=create`); data seeded from `data.sql`.
- Security: Spring Security form login, BCrypt password storage, permit-all only for auth/public/static paths.
- Frontend: Thymeleaf templates + CoreUI Bootstrap 5 static assets; no client-side frameworks beyond what ships in the repo.
- E2E: Java Playwright (`com.microsoft.playwright`) with headless Chromium against a live random-port server.

## Development Workflow & Quality Gates

1. **Build gate**: `./mvnw clean compile` must succeed before any runtime claim.
2. **Runtime gate**: `./mvnw spring-boot:run` must start and serve the login page at the configured port.
3. **Test gate**: unit/integration tests plus Playwright E2E (`AiApplicationTests`) must pass; screenshots/artifacts are produced as evidence.
4. **Change gate**: any schema change MUST be reflected in `schema.sql`; any new route MUST have a matching controller and template.
5. **Review expectation**: PRs/reviews verify compliance with every principle above before merge.

## Governance

- This constitution supersedes all other project practices; where conflicts arise, the principles above win.
- Amendments require written documentation in this file, an explicit version bump (MAJOR/MINOR/PATCH per semantic versioning), and a rationale note.
- Backward-incompatible principle removals or redefinitions MUST be MAJOR; new principles/sections are MINOR; clarifications/wording fixes are PATCH.
- Compliance review: every implementation change is checked against Principles I–V before completion is claimed.

**Version**: 0.2.0 | **Ratified**: TODO(RATIFICATION_DATE): original adoption date not recorded in repo history | **Last Amended**: 2026-10-10
<!-- Sync Impact Report: v0.1.0 -> v0.2.0 (MINOR). Modified placeholders -> concrete principles I-V; added Technology & Security Constraints and Development Workflow & Quality Gates sections; Governance expanded with amendment/versioning policy. No sections removed. Deferred: RATIFICATION_DATE (unknown, marked TODO). -->
