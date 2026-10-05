# Implementation Plan: User Authentication System

**Branch**: `001-user-auth` | **Date**: 2026-10-05 | **Spec**: [./spec.md](./spec.md)

**Input**: Feature specification from `/specs/001-user-auth/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command; its definition describes the execution workflow.

## Summary

Implement complete user authentication system with login, registration, and password reset flows using Spring Security form login with JDBC UserDetailsService. Store passwords hashed with BCrypt in the existing users table. Provide Thymeleaf-based pages following CoreUI Bootstrap 5 conventions for a secure, user-friendly authentication experience.

## Technical Context

<!--
  ACTION REQUIRED: Replace the content in this section with the technical details
  for the project. The structure here is presented in advisory capacity to guide
  the iteration process.
-->

**Language/Version**: Java 21 LTS with all language features enabled (records, pattern matching, text blocks)

**Primary Dependencies**: Spring Boot 4.1.1, Spring Security 6.x, Spring Data JPA, Thymeleaf 3.x, BCryptPasswordEncoder, H2 Database Console

**Storage**: H2 in-memory database via spring-boot-starter-h2console for development; users table with existing schema

**Testing**: JUnit 5, @DataJpaTest for repository tests, @SpringBootTest for integration tests, Playwright for E2E browser automation

**Target Platform**: Java SE application running on any HTTP server (Tomcat/Jetty) with web browser access

**Project Type**: Web application (Spring Boot MVC with Thymeleaf templates and CoreUI Bootstrap 5 frontend)

**Performance Goals**: Login < 30 seconds from page load to dashboard, Registration < 60 seconds for valid submissions, Password reset immediate availability

**Constraints**: No account lockout per scope definition, BCrypt cost factor minimum 10, no email confirmation required, must integrate with existing users table schema

**Scale/Scope**: Single application supporting local user management; initial scope covers authentication flows only (no authorization roles, no social login)

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

### Gate I: Spring Boot First ✓ PASSED
**Requirement**: Every feature must use Spring Boot conventions with Controllers, Services, and Repositories.
**Status**: PASSED - Implementation will follow standard MVC pattern with LoginController, AuthService, UserRepository following established architecture in SecurityConfig and existing application structure.

### Gate II: Database Schema Alignment ✓ PASSED
**Requirement**: All data models MUST match the schema.sql data model exactly.
**Status**: PASSED - User entity will map directly to existing users table; any additions (password_hash column if not present) will be documented as migrations per principle II.

### Gate III: JDBC Authentication ✓ PASSED
**Requirement**: Use JDBC-based UserDetailsService with BCrypt password hashing; no plaintext passwords.
**Status**: PASSED - Implementation will create UserDetailsService implementing loadUserByUsername(), using BCryptPasswordEncoder for both storage and validation per principle III.

### Gate IV: Integration Testing ✓ PASSED
**Requirement**: All new features MUST include integration tests with Playwright E2E testing.
**Status**: PASSED - Tests will cover login, registration, password reset flows with Playwright browser automation as required by principle IV.

### Gate V: Observability ✓ PASSED
**Requirement**: Structured logs with appropriate severity levels; health endpoints for monitoring.
**Status**: PASSED - Authentication events will be logged at INFO (successful login), WARN (failed attempts), ERROR (exceptions); existing application structure supports observability per principle V.

**All gates passed. Proceeding to Phase 0 research.**

## Project Structure

### Documentation (this feature)

```text
specs/[###-feature]/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command)
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)
<!--
  ACTION REQUIRED: Replace the placeholder tree below with the concrete layout
  for this feature. Delete unused options and expand the chosen structure with
  real paths (e.g., apps/admin, packages/something). The delivered plan must
  not include Option labels.
-->

```text
# [REMOVE IF UNUSED] Option 1: Single project (DEFAULT)
src/
├── models/
├── services/
├── cli/
└── lib/

tests/
├── contract/
├── integration/
└── unit/

# [REMOVE IF UNUSED] Option 2: Web application (when "frontend" + "backend" detected)
backend/
├── src/
│   ├── models/
│   ├── services/
│   └── api/
└── tests/

frontend/
├── src/
│   ├── components/
│   ├── pages/
│   └── services/
└── tests/

# [REMOVE IF UNUSED] Option 3: Mobile + API (when "iOS/Android" detected)
api/
└── [same as backend above]

ios/ or android/
└── [platform-specific structure: feature modules, UI flows, platform tests]
```

**Structure Decision**: [Document the selected structure and reference the real
directories captured above]

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| [e.g., 4th project] | [current need] | [why 3 projects insufficient] |
| [e.g., Repository pattern] | [specific problem] | [why direct DB access insufficient] |
