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

### Phase 1: Setup [✓ COMPLETE]
Initialize the project structure and configure development environment.

```text
- [X] T001 Create src/main/java/com/allensandiego/ai/config directory for configuration classes
- [X] T002 Create src/main/java/com/allensandiego/ai/controller directory for REST controllers
- [X] T003 Create src/main/java/com/allensandiego/ai/service directory for business logic services
- [X] T004 Create src/main/java/com/allensandiego/ai/repository directory for data access layers
- [X] T005 Create src/main/resources/templates/auth directory for authentication templates
- [X] T006 Create src/main/resources/static/css/auth.css for authentication styles
- [X] T007 Create application-dev.yml with H2 console enabled and logging configuration
```

### Phase 2: Foundational [✓ COMPLETE]
Implement shared security infrastructure required by all user stories.

```text
- [X] T008 Configure Spring Security password encoder bean using BCrypt
- [X] T009 Create User entity class extending BaseTimeEntity with username, email, password fields
- [X] T010 Create UserRepository interface extending JpaRepository for user data access
- [X] T011 Create UserService interface defining authentication and registration methods
- [X] T012 Implement UserServiceImpl with password hashing using passwordEncoder
- [X] T013 Create SecurityConfig class enabling form login, CSRF protection, and session management
- [X] T014 Configure loginSuccessUrl to /home and logoutSuccessUrl to /login in security config
- [X] T015 Add @EnableWebSecurity annotation to SecurityConfig class
```

### Phase 3: User Story 1 - Registration [✓ COMPLETE]
Enable new user registration without email verification.

**Goal:** Allow users to create accounts via a registration form  
**Test Criteria:** New account created, user can immediately log in with registered credentials

```text
- [X] T018 [US1] Create RegistrationForm DTO with username, email, password fields and validation annotations (src/main/java/com/allensandiego/ai/controller/RegistrationForm.java)
- [X] T019 [US1] Implement registration endpoint POST /register returning success/error messages (AuthController.java)
- [X] T020 [US1] Create registration form template auth/register.html with Thymeleaf CSRF field
- [X] T021 [US1] Add client-side validation using Bootstrap 5 form validation and HTMX for instant feedback
- [X] T022 [US1] Implement username uniqueness check (3-50 alphanumeric characters) in controller
- [X] T023 [US1] Implement email format validation with regex pattern matching
- [X] T024 [US1] Implement password minimum length validation (≥8 characters)
- [X] T025 [US1] Handle duplicate username conflict with appropriate error response
- [X] T026 [US1] Handle duplicate email conflict with appropriate error response
- [X] T027 [US1] Display success message on successful registration redirecting to login page
```

### Phase 4: User Story 2 - Login [✓ COMPLETE]
Enable existing users to authenticate and access protected content.

**Goal:** Allow authenticated users to log in and access protected resources  
**Test Criteria:** Valid credentials grant access, invalid credentials show generic error without revealing account status

```text
- [X] T028 [US2] Create LoginForm DTO with username, password fields and remember_me checkbox (src/main/java/com/allensandiego/ai/controller/LoginForm.java)
- [X] T029 [US2] Implement login endpoint POST /login with form login configuration (SecurityConfig.java)
- [X] T030 [US2] Create login form template auth/login.html with Thymeleaf CSRF field and Bootstrap styling
- [X] T031 [US2] Add client-side validation for required fields using HTMX instant feedback
- [X] T032 [US2] Implement username/password authentication using UserDetailsService (UserServiceImpl.java)
- [X] T033 [US2] Handle invalid credentials with generic error message "Invalid username or password"
- [X] T034 [US2] Configure remember_me cookie handling (optional, 7-day expiry)
- [X] T035 [US2] Implement logout endpoint POST /logout terminating session and redirecting to login
- [X] T036 [US2] Create protected content template layout with authentication status display (home.html, main.html)
- [X] T037 [US2] Implement access controller method checking Authentication.isAuthenticated()
```

### Phase 5: Polish & Cross-Cutting Concerns [IN PROGRESS]
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
│   ├── AuthController.java
│   ├── RegistrationForm.java
│   └── LoginForm.java
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
└── error.html (T040)

src/main/resources/templates/layout/
└── main.html

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

---

## Implementation Progress

**Completed:** Phase 1, Phase 2, US1, US2 (37 of 45 tasks)  
**In Progress:** Phase 5 - Polish tasks (8 remaining)

Files created:
- User entity with JPA configuration
- UserRepository for data access
- UserService with BCrypt password encoding
- SecurityConfig with form login and CSRF protection
- AuthController handling registration and login
- RegistrationForm DTO with validation annotations
- LoginForm DTO
- Thymeleaf templates for register, login, home pages
- Main layout template
- Authentication styles

**Next Steps:** Complete remaining polish tasks (T038-T045) in Phase 5.
