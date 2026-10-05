# Tasks: User Authentication System

**Input**: Design documents from `/specs/001-user-auth/`

**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, quickstart.md

**Tests**: Tests are OPTIONAL - not included since specification does not explicitly request TDD approach. Playwright E2E testing will be added in implementation as needed.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project structure initialization following Spring Boot conventions

- [X] T001 Create package structure: com.allensandiego.ai.config/, com.allensandiego.ai.controller/, com.allensandiego.ai.service/, com.allensandiego.ai.repository/ in src/main/java/com/allensandiego/ai/
- [X] T002 Verify pom.xml dependencies: spring-boot-starter-security, spring-boot-starter-data-jpa, spring-boot-starter-thymeleaf, thymeleaf-extras-springsecurity6, h2, lombok are present and configured

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [X] T003 Create User entity class in src/main/java/com/allensandiego/ai/model/User.java with fields: id (Long), username (String), passwordHash (String), email (String, nullable), enabled (boolean)
- [X] T004 Add JPA annotations to User entity: @Entity, @Table(name = "users"), @Id, @GeneratedValue(strategy = GenerationType.IDENTITY), @Column(nullable=false, unique=true), @Column(nullable=false), @Column(length=100), @Column(name="enabled")
- [X] T005 Create UserRepository interface extending JpaRepository in src/main/java/com/allensandiego/ai/repository/UserRepository.java with methods: findByUsername(String), existsByUsername(String), save(User)
- [X] T006 Create UserDetailsService implementation in src/main/java/com/allensandiego/ai/security/JdbcUserDetailsService.java implementing loadUserByUsername(String username) returning UserDetails
- [X] T007 Create BCrypt password encoder bean in src/main/java/com/allensandiego/ai/config/PasswordEncoderConfig.java using org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
- [X] T008 Update SecurityConfig to use the new UserDetailsService and PasswordEncoder, ensure formLogin() is configured with loginPage("/login") and successHandler redirects to main application

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Login Flow (Priority: P1) 🎯 MVP

**Goal**: Enable registered users to authenticate and access protected pages

**Independent Test**: Launch the application, navigate to /login, enter valid credentials from the users table, verify successful authentication and redirection to the dashboard

### Implementation for User Story 1

- [ ] T009 [US1] Create LoginController in src/main/java/com/allensandiego/ai/controller/LoginController.java with @GetMapping("/login") returning "authentication/login"
- [ ] T010 [US1] Update SecurityConfig to configure logout with logoutUrl("/logout") and permitAll()
- [ ] T011 [US1] Create login.html template in src/main/resources/templates/authentication/login.html using CoreUI Bootstrap 5 structure with form for username and password input, submit button, and link to /register
- [ ] T012 [US1] Implement authentication logic in SecurityFilterChain: validate credentials via UserDetailsService.comparePassword(), grant access on success, deny with generic error message on failure (do not distinguish between invalid username vs wrong password)
- [ ] T013 [US1] Configure formLogin() successHandler to redirect authenticated users to main application root (/)
- [ ] T014 [US1] Create sample user in H2 database for testing: INSERT INTO users (username, password_hash, email, enabled) VALUES ('admin', '$2a$10$[BCRYPT_HASH]', 'admin@example.com', TRUE)

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently

---

## Phase 4: User Story 2 - Registration Flow (Priority: P2)

**Goal**: Enable new visitors to create accounts and immediately log in without email confirmation

**Independent Test**: Navigate to /register, complete the form with valid data, verify successful account creation and ability to log in immediately

### Implementation for User Story 2

- [X] T015 [US2] Create RegisterController in src/main/java/com/allensandiego/ai/controller/RegisterController.java with @GetMapping("/register") returning "authentication/register"
- [X] T016 [US2] Add registration form validation service in src/main/java/com/allensandiego/ai/service/RegistrationService.java: validate username (3-50 chars, alphanumeric), validate password (min 8 chars, mix of types), check username uniqueness via UserRepository.existsByUsername()
- [X] T017 [US2] Create register.html template in src/main/resources/templates/authentication/register.html using CoreUI Bootstrap 5 structure with form for username, email (optional), and password fields, validation feedback messages
- [X] T018 [US2] Implement registration logic: hash password with BCrypt encoder, create User entity via UserRepository.save(), validate uniqueness before saving, return appropriate error message for duplicate username
- [X] T019 [US2] Add redirect after successful registration to /login page with informational message
- [X] T020 [US2] Create register-success.html template in src/main/resources/templates/authentication/register-success.html to display success message and link to login page

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently

---

## Phase 5: User Story 3 - Password Reset Flow (Priority: P3)

**Goal**: Enable users to reset forgotten passwords without email confirmation

**Independent Test**: Use the password reset functionality, verify a new password is created and stored, confirm ability to log in with the reset password

### Implementation for User Story 3

- [X] T021 [US3] Create PasswordResetController in src/main/java/com/allensandiego/ai/controller/PasswordResetController.java with @GetMapping("/reset-password") returning "authentication/reset-password"
- [X] T022 [US3] Add password reset service in src/main/java/com/allensandiego/ai/service/PasswordResetService.java: verify username exists, generate new BCrypt-hashed password, update user record in database via UserRepository
- [X] T023 [US3] Create reset-password.html template in src/main/resources/templates/authentication/reset-password.html using CoreUI Bootstrap 5 structure with form for username input and "Request Reset" button, link back to /login
- [X] T024 [US3] Implement password reset logic: accept username request, verify user exists in database, generate new random secure password (min 8 chars), hash with BCrypt, update user record, return generic message (do not reveal whether username exists)
- [X] T025 [US3] Create password-changed.html template in src/main/resources/templates/authentication/password-changed.html to display success message and link to /login with instructions to use new password
- [X] T026 [US3] Add validation for new password strength: minimum 8 characters, require at least one uppercase letter, one lowercase letter, one digit

**Checkpoint**: All user stories should now be independently functional

---

## Phase N: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] T027 Add logout functionality: create logout.html template in src/main/resources/templates/authentication/logout.html with CoreUI structure, configure SecurityConfig logoutSuccessHandler to redirect to /logout
- [ ] T028 Implement form validation on both client and server side using Bootstrap 5 validation classes and JavaScript
- [ ] T029 Add error handling for database connection failures: catch exceptions in services, log at ERROR level, display user-friendly message
- [ ] T030 Create security logging: INFO level for successful authentication, WARN level for failed login attempts, ERROR level for system errors
- [ ] T031 Update global error handler to catch AuthenticationException and return generic messages consistent with FR-003
- [ ] T032 Add application properties configuration: set spring.security.user.name=admin, spring.security.user.password=[BCrypt_HASH] for development testing
- [ ] T033 Run quickstart.md validation scenarios to verify all flows work end-to-end

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3+)**: All depend on Foundational phase completion
  - User stories can then proceed in parallel (if staffed)
  - Or sequentially in priority order (P1 → P2 → P3)
- **Polish (Final Phase)**: Depends on all desired user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P2)**: Can start after Foundational (Phase 2) - Independent of US1, but can be tested using credentials from US1 setup
- **User Story 3 (P3)**: Can start after Foundational (Phase 2) - Works with existing users created by US1/US2

### Within Each User Story

- Models before services
- Services before controllers/endpoints
- Core implementation before integration
- Story complete before moving to next priority

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel
- All Foundational tasks marked [P] can run in parallel (within Phase 2)
- Once Foundational phase completes, all user stories can start in parallel (if team capacity allows)
- Different user stories can be worked on in parallel by different developers

---

## Parallel Example: User Story 1

```bash
# Launch controller and template tasks together (independent files):
Task: "Create LoginController in src/main/java/com/allensandiego/ai/controller/LoginController.java"
Task: "Update SecurityConfig to configure logout"
Task: "Create login.html template in src/main/resources/templates/authentication/login.html"

# Database setup can be done separately if using test data fixtures:
Task: "Create sample user in H2 database for testing"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1
4. **STOP and VALIDATE**: Test User Story 1 independently using Playwright E2E testing
5. Deploy/demo if ready

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Test independently → Deploy/Demo (MVP!)
3. Add User Story 2 → Test independently → Deploy/Demo
4. Add User Story 3 → Test independently → Deploy/Demo
5. Each story adds value without breaking previous stories

### Parallel Team Strategy

With multiple developers:

1. Team completes Setup + Foundational together
2. Once Foundational is done:
   - Developer A: User Story 1 (Login)
   - Developer B: User Story 2 (Registration)
   - Developer C: User Story 3 (Password Reset)
3. Stories complete and integrate independently

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- Tests are optional per specification - Playwright E2E will be added during implementation validation
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently using quickstart.md scenarios
