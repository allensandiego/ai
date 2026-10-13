# Tasks: User Authentication Implementation

**Feature:** User Authentication (Direct Registration)  
**Spec Location:** `specs/user-auth/spec.md`  
**Data Model:** `specs/user-auth/data-model.md`  
**Contracts:** `specs/user-auth/contracts/auth-flow.md`  
**Quickstart:** `specs/user-auth/quickstart.md`  
**Created:** 2026-10-13  

---

## Overview

This document defines the implementation tasks for the user authentication feature. Tasks are organized by priority and execution order to enable incremental delivery and independent testing.

---

## Dependencies

Tasks must be executed in this order:

```
Phase 1 (Setup) → Phase 2 (Foundational) → Phase 3 (US1: Registration) → Phase 4 (US2: Login) → Phase 5 (Polish)
```

User Stories are independent but follow priority order (P1 before P2).

---

## Parallel Execution Examples

### Within Phase 2 (Foundational)
- T006 and T007 can run in parallel (SecurityConfig and UserDetailsService don't depend on each other)
- T008 can run after both T006 and T007 complete

### Within Phase 3 (Registration)
- Client-side validation (T012) and server-side validation logic (T013) can be developed in parallel
- Form template (T015) depends on validation logic completion

### Within Phase 4 (Login)
- Login form (T020) and login controller (T021) can run in parallel after shared security config is complete

---

## Task Checklist

### Phase 1: Setup
Initialize the project structure and configure development environment.

```text
- [ ] T001 Create src/main/java/com/allensandiego/ai/config directory for configuration classes
- [ ] T002 Create src/main/java/com/allensandiego/ai/controller directory for REST controllers
- [ ] T003 Create src/main/java/com/allensandiego/ai/service directory for business logic services
- [ ] T004 Create src/main/java/com/allensandiego/ai/repository directory for data access layers
- [ ] T005 Create src/main/resources/templates/auth directory for authentication templates
- [ ] T006 Create src/main/resources/static/css/auth.css for authentication styles
- [ ] T007 Create application-dev.yml with H2 console enabled and logging configuration
```

### Phase 2: Foundational
Implement shared security infrastructure required by all user stories.

```text
- [ ] T008 Configure Spring Security password encoder bean using BCrypt
- [ ] T009 Create User entity class extending BaseTimeEntity with username, email, password fields
- [ ] T010 Create UserRepository interface extending JpaRepository for user data access
- [ ] T011 Create UserService interface defining authentication and registration methods
- [ ] T012 Implement UserServiceImpl with password hashing using passwordEncoder
- [ ] T013 Create SecurityConfig class enabling form login, CSRF protection, and session management
- [ ] T014 Configure loginSuccessUrl to /home and logoutSuccessUrl to /login in security config
- [ ] T015 Add @EnableWebSecurity annotation to SecurityConfig class
- [ ] T016 Create AuthSuccessHandler for post-login redirection with welcome message
- [ ] T017 Implement session timeout configuration (20 minutes default)
```

### Phase 3: User Story 1 - Registration
Enable new user registration without email verification.

**Goal:** Allow users to create accounts via a registration form  
**Test Criteria:** New account created, user can immediately log in with registered credentials

```text
- [ ] T018 [US1] Create RegistrationForm DTO with username, email, password fields and validation annotations
- [ ] T019 [US1] Implement registration endpoint POST /register returning success/error messages
- [ ] T020 [US1] Create registration form template auth/register.html with Thymeleaf CSRF field
- [ ] T021 [US1] Add client-side validation using Bootstrap 5 form validation and HTMX for instant feedback
- [ ] T022 [US1] Implement username uniqueness check (3-50 alphanumeric characters) in controller
- [ ] T023 [US1] Implement email format validation with regex pattern matching
- [ ] T024 [US1] Implement password minimum length validation (≥8 characters)
- [ ] T025 [US1] Handle duplicate username conflict with appropriate error response
- [ ] T026 [US1] Handle duplicate email conflict with appropriate error response
- [ ] T027 [US1] Display success message on successful registration redirecting to login page
```

### Phase 4: User Story 2 - Login
Enable existing users to authenticate and access protected content.

**Goal:** Allow authenticated users to log in and access protected resources  
**Test Criteria:** Valid credentials grant access, invalid credentials show generic error without revealing account status

```text
- [ ] T028 [US2] Create LoginForm DTO with username, password fields and remember_me checkbox
- [ ] T029 [US2] Implement login endpoint POST /login with form login configuration
- [ ] T030 [US2] Create login form template auth/login.html with Thymeleaf CSRF field and Bootstrap styling
- [ ] T031 [US2] Add client-side validation for required fields using HTMX instant feedback
- [ ] T032 [US2] Implement username/password authentication using UserDetailsService
- [ ] T033 [US2] Handle invalid credentials with generic error message "Invalid username or password"
- [ ] T034 [US2] Configure remember_me cookie handling (optional, 7-day expiry)
- [ ] T035 [US2] Implement logout endpoint POST /logout terminating session and redirecting to login
- [ ] T036 [US2] Create protected content template layout with authentication status display
- [ ] T037 [US2] Implement access controller method checking Authentication.isAuthenticated()
```

### Phase 5: Polish & Cross-Cutting Concerns
Enhance user experience, security, and application stability.

```text
- [ ] T038 Add CSRF token regeneration on session creation in SecurityConfig
- [ ] T039 Implement rate limiting for login attempts (max 10 attempts per minute) using HttpSession
- [ ] T040 Create error page templates auth/error.html with generic messages and retry links
- [ ] T041 Add loading states to forms using HTMX htmx-indicator during validation
- [ ] T042 Implement password strength indicator on registration form (visual feedback)
- [ ] T043 Configure session fixation protection with Spring Security's sessionFixation() configuration
- [ ] T044 Add security headers (X-Content-Type-Options, X-Frame-Options) in a Filter or WebMvcConfigurer
- [ ] T045 Ensure all error messages avoid revealing sensitive information about account status
```

---

## User Story Mapping

| User Story | Tasks | Independent Test Criteria |
|------------|-------|---------------------------|
| **US1: Registration** | T018-T027 | New user can register with valid data and immediately log in |
| **US2: Login** | T028-T037 | Registered user can authenticate; invalid credentials show generic error |

---

## Implementation Strategy

### MVP Scope (Minimum Viable Product)
**Phase 1 + Phase 2 + US1 (Registration only)** - Enables account creation but no login yet

**Recommended Full MVP:**
- **Phase 1** (Setup)
- **Phase 2** (Foundational security infrastructure)
- **US1** (Registration)
- **US2** (Login and logout)

### Incremental Delivery Order
1. **Week 1:** Setup + Foundational (Phases 1-2) - Security infrastructure ready
2. **Week 2:** Registration (US1) - Users can create accounts
3. **Week 3:** Login/Logout (US2) - Full authentication cycle complete
4. **Week 4:** Polish - Security hardening and UX improvements

### Parallel Development Paths
- **Path A (Frontend):** Forms, templates, HTMX integration → Can run independently
- **Path B (Backend):** Services, repositories, controllers → Requires security config first
- **Path C (Security):** Config classes, session management → Must be done first

---

## Test Requirements

### Integration Tests Needed
- Registration form submission with valid/invalid data
- Login success and failure scenarios
- Logout session termination
- CSRF token validation on all forms
- Protected content access control

### Unit Tests Needed
- UserService password hashing (verify BCrypt format)
- Validation logic for each field
- Authentication provider integration
- Session management behavior

---

## File Structure Summary

```
src/main/java/com/allensandiego/ai/
├── config/
│   └── SecurityConfig.java
├── controller/
│   └── AuthController.java
├── service/
│   ├── UserService.java
│   └── UserServiceImpl.java
├── repository/
│   └── UserRepository.java
└── entity/
    └── User.java

src/main/resources/templates/auth/
├── register.html
├── login.html
└── error.html

src/main/resources/static/css/
└── auth.css
```

---

## Success Metrics

- All tasks completed with passing unit and integration tests
- Registration completes within 10 seconds (95th percentile)
- Login validates credentials within 5 seconds (95th percentile)
- No security vulnerabilities identified in code review or testing
- All forms include CSRF protection and client/server validation
