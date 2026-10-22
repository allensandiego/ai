---

description: "Task list for User Registration & Authentication"
---

# Tasks: User Registration & Authentication

**Input**: Design documents from `/specs/001-user-auth/`

**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/http-endpoints.md, quickstart.md

**Tests**: Included — Constitution Principle V mandates automated tests (unit + Spring Boot integration + Playwright e2e) for this feature.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions

Single Maven project at repository root: `src/main/java/com/allensandiego/ai/...`, `src/main/resources/templates/...`, `src/test/java/com/allensandiego/ai/...`.

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Confirm the existing baseline is sound before any changes; no new dependencies are required per plan.md.

- [ ] T001 Run `./mvnw test` from repository root to confirm the existing build and test suite pass as a pre-change baseline (pom.xml unchanged — security, thymeleaf, validation, data-jpa, and Playwright starters already present)
- [ ] T002 [P] Verify src/main/resources/schema.sql and src/main/resources/data.sql match the User mapping in specs/001-user-auth/data-model.md (users table: id PK auto-increment, unique username, password, unique email; seeded admin row with a Bcrypt hash) and confirm NO schema changes are required

**Checkpoint**: Baseline green — foundational work can begin.

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core persistence, authentication wiring, and post-login landing surface that MUST be complete before ANY user story is independently testable end-to-end.

**⚠️ CRITICAL**: No user story work can begin until this phase is complete.

- [ ] T003 [P] Create User JPA entity in src/main/java/com/allensandiego/ai/model/User.java mapping the existing users table exactly: id Long PK auto-increment; username String not null unique max 255 chars; password String not null max 255 chars (Bcrypt hash only — plaintext never stored); email String not null unique max 255 chars; createdAt Timestamp default current time on insert. Do NOT modify schema.sql
- [ ] T004 Create UserRepository in src/main/java/com/allensandiego/ai/repository/UserRepository.java extending JpaRepository<User, Long> with existsByUsername(String), existsByEmail(String), and findByUsernameOrEmail(String) returning Optional<User> (depends on T003)
- [ ] T005 Implement UserService in src/main/java/com/allensandiego/ai/service/UserService.java: loadUserByUsername(String identifier) → UserRepository.findByUsernameOrEmail, map to UserDetails with principal name = stored username, throw UsernameNotFoundException when no match (depends on T004)
- [ ] T006 Update SecurityConfig in src/main/java/com/allensandiego/ai/config/SecurityConfig.java: register a BCryptPasswordEncoder bean; wire UserService as the UserDetailsService for form login so the "username" form field accepts username OR email; keep the existing permitAll set (login, register, static assets) and anyRequest().authenticated(); add logoutSuccessUrl("/authentication/login") (depends on T005)
- [ ] T007 [P] Create HomeController in src/main/java/com/allensandiego/ai/controller/HomeController.java with GET / returning the "home" view; create a minimal CoreUI-styled home template showing the authenticated username via Spring Security Thymeleaf integration in src/main/resources/templates/home.html

**Checkpoint**: Foundation ready — registration and login can now be implemented and tested end-to-end.

---

## Phase 3: User Story 1 - Direct Registration (Priority: P1) 🎯 MVP

**Goal**: A new user registers directly with no email verification step; the account is immediately usable for login.

**Independent Test**: Submit a valid registration → redirect to /authentication/login?registered=true showing a success alert; log in with the just-created credentials and reach /.

### Tests for User Story 1 (write FIRST, ensure they FAIL before implementation) ⚠️

- [ ] T008 [P] [US1] Add unit tests for UserService.register() in src/test/java/com/allensandiego/ai/service/UserServiceTest.java: valid input persists a user whose stored password is Bcrypt-encoded (verify via encoder.matches); duplicate username rejected without save; duplicate email rejected without save; password under 8 chars rejected; mismatched confirmation rejected
- [ ] T009 [P] [US1] Add integration tests for registration in src/test/java/com/allensandiego/ai/controller/AuthenticationControllerTest.java: valid POST /authentication/register → 302 to /authentication/login?registered=true and user persisted; duplicate username or email → form re-rendered with "already in use" field error and no persistence; invalid fields (short password, mismatched confirmation) → per-field errors

### Implementation for User Story 1

- [ ] T010 [P] [US1] Create RegistrationRequest DTO in src/main/java/com/allensandiego/ai/dto/RegistrationRequest.java with Bean Validation: username @NotBlank @Size(max=255); email @Email @NotBlank @Size(max=255); password @NotBlank @Size(min=8, max=255); passwordConfirm @NotBlank
- [ ] T011 [US1] Add register(RegistrationRequest) to UserService in src/main/java/com/allensandiego/ai/service/UserService.java: pre-save uniqueness checks via existsByUsername/existsByEmail, encode the password with BCryptPasswordEncoder, save the User; surface field-level failures so the controller can re-render (depends on T010)
- [ ] T012 [US1] Extend AuthenticationController in src/main/java/com/allensandiego/ai/controller/AuthenticationController.java: GET /authentication/register renders "authentication/register"; POST /authentication/register accepts @Valid RegistrationRequest + BindingResult, calls UserService.register, redirects to /authentication/login?registered=true on success; re-renders the form with field-adjacent errors on failure (depends on T011)
- [ ] T013 [US1] Rework register.html in src/main/resources/templates/authentication/register.html as a Thymeleaf form preserving the existing CoreUI card/layout markup: fields username, email, password, passwordConfirm; th:action POST to /authentication/register with CSRF token; field-adjacent errors via th:errors; remove demo elements (name field, check-email action link)

**Checkpoint**: User Story 1 fully functional — register → login page with success alert → log in with the new account. MVP is shippable here.

---

## Phase 4: User Story 2 - Login by Username or Email (Priority: P1)

**Goal**: A registered user logs in using either identifier; failures show one generic non-enumerating error; anonymous access to protected pages redirects to login and returns after authentication.

**Independent Test**: Log in with the seeded admin account (admin@demo.com) → reach / showing authenticated state; wrong credentials or unknown identifier → /authentication/login?error with a single identical generic alert; anonymous GET / → redirect to login, then return to / after successful login.

### Tests for User Story 2 (write FIRST, ensure they FAIL before implementation) ⚠️

- [ ] T014 [P] [US2] Add unit tests for UserService.loadUserByUsername() in src/test/java/com/allensandiego/ai/service/UserServiceTest.java: lookup by username succeeds; lookup by email succeeds with principal name = stored username; unknown identifier throws UsernameNotFoundException
- [ ] T015 [P] [US2] Add integration tests for login in src/test/java/com/allensandiego/ai/controller/AuthenticationControllerTest.java: POST /authentication/login with valid seeded admin credentials → 302 to saved request or "/"; invalid credentials (unknown identifier AND wrong password) → identical 302 to /authentication/login?error; anonymous GET / → 302 to login preserving the original destination

### Implementation for User Story 2

- [ ] T016 [US2] Rework login.html in src/main/resources/templates/authentication/login.html as a Thymeleaf form preserving the CoreUI layout: single identifier input named "username" labeled "Username or email", password input; th:action POST to /authentication/login with CSRF token; generic error alert when ?error is present (never states whether the identifier exists); success alert when ?registered is present; keep the register link; remove demo elements (social login buttons, remember-me checkbox)

**Checkpoint**: User Stories 1 AND 2 both work independently — full register and login journeys verified.

---

## Phase 5: User Story 3 - Logout (Priority: P2)

**Goal**: A logged-in user can end the session via a logout action; after logout, protected pages are inaccessible until re-authentication.

**Independent Test**: Trigger logout from the home page → redirect to /authentication/login; a subsequent GET / with the same session redirects back to login (session invalidated).

### Tests for User Story 3 (write FIRST, ensure they FAIL before implementation) ⚠️

- [ ] T017 [US3] Add integration tests for logout in src/test/java/com/allensandiego/ai/controller/AuthenticationControllerTest.java: authenticated POST /authentication/logout → 302 to /authentication/login and a subsequent GET / with the same session redirects to login (session invalidated)

### Implementation for User Story 3

- [ ] T018 [US3] Add a logout form button posting to /authentication/logout with CSRF token, styled per CoreUI theme, in src/main/resources/templates/home.html

**Checkpoint**: Logout flow complete — session termination verified.

---

## Phase 6: User Story 4 - CoreUI Admin Theme Adherence (Priority: P2)

**Goal**: All authentication pages and the home page render within the shared CoreUI admin layout with consistent styling, labeled inputs, and field-adjacent errors; no duplicated or off-theme markup.

**Independent Test**: Visually inspect register, login, and home in a browser — all share the CoreUI layout, assets load, forms are functional, and validation errors appear adjacent to their fields.

### Tests for User Story 4 (write FIRST where applicable) ⚠️

- [ ] T019 [P] [US4] Add Playwright e2e tests in src/test/java/com/allensandiego/ai/e2e/AuthenticationFlowIT.java covering the full journey: register → login by username AND by email → home shows the authenticated username → logout → redirected to login; assert CoreUI layout markers are present on each page and field-adjacent errors render on a failed registration
- [ ] T020 [P] [US4] Verify all three templates reuse the shared CoreUI layout markup and asset links consistent with the existing coreui/ references, fixing any duplicated or off-theme markup in src/main/resources/templates/authentication/register.html, src/main/resources/templates/authentication/login.html, and src/main/resources/templates/home.html

**Checkpoint**: All user stories independently functional; UI adherence verified end-to-end.

---

## Phase 7: Polish & Cross-Cutting Concerns

**Purpose**: Final validation across all stories before sign-off.

- [ ] T021 Run the quickstart.md validation scenarios 1–6 against the running app (`./mvnw spring-boot:run` from repository root) and confirm each expected outcome (registration, duplicates/validation, generic login error, anonymous redirect, logout, UI adherence)
- [ ] T022 Run `./mvnw verify` from repository root; all unit, integration, and Playwright tests pass with no unexplained skips

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies — can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion — BLOCKS all user stories
- **User Stories (Phases 3–6)**: All depend on Foundational phase completion; US1 and US2 are independent of each other, US3 depends only on the home page existing (T007), US4 is cross-cutting verification over all pages
- **Polish (Phase 7)**: Depends on all user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational — no dependencies on other stories
- **User Story 2 (P1)**: Can start after Foundational — independent of US1 (shares only foundational components)
- **User Story 3 (P2)**: Can start after Foundational — needs T007 home page; otherwise independent
- **User Story 4 (P2)**: Runs after the pages it verifies exist (T013, T016, T007/T018)

### Within Each User Story

- Tests MUST be written and FAIL before implementation
- DTO/model before service, service before controller, controller before template wiring
- Story complete before moving to next priority

### Parallel Opportunities

- Phase 1: T002 [P] alongside T001
- Phase 2: T003 [P] and T007 [P] run in parallel; the chain T004 → T005 → T006 is sequential (same-file/compile dependencies)
- US1: T008, T009, T010 all [P] together (different files); then T011 → T012 → T013 sequentially
- US2: T014 and T015 [P] together; then T016
- US4: T019 and T020 [P] together
- Once Foundational completes, US1 and US2 can be worked in parallel by different developers

---

## Parallel Example: User Story 1

```text
# Launch together (different files, no dependencies):
Task: "T008 Unit tests for UserService.register() in src/test/java/com/allensandiego/ai/service/UserServiceTest.java"
Task: "T009 Integration tests for registration in src/test/java/com/allensandiego/ai/controller/AuthenticationControllerTest.java"
Task: "T010 RegistrationRequest DTO in src/main/java/com/allensandiego/ai/dto/RegistrationRequest.java"

# Then sequentially (compile dependencies):
Task: "T011 UserService.register() in src/main/java/com/allensandiego/ai/service/UserService.java"
Task: "T012 Register endpoints in src/main/java/com/allensandiego/ai/controller/AuthenticationController.java"
Task: "T013 Rework register.html in src/main/resources/templates/authentication/register.html"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL — blocks all stories; includes auth wiring and home landing page so US1 is testable end-to-end)
3. Complete Phase 3: User Story 1
4. **STOP and VALIDATE**: register a new account, follow the redirect to login, log in with the new credentials, reach /
5. Deploy/demo if ready

### Incremental Delivery

1. Setup + Foundational → foundation ready
2. Add US1 (registration) → test independently → demo (MVP!)
3. Add US2 (login hardening/templates) → test independently → demo
4. Add US3 (logout) → test independently → demo
5. Add US4 (theme verification + e2e) → full validation
6. Each story adds value without breaking previous stories

### Parallel Team Strategy

1. One developer completes Setup + Foundational
2. Once Foundational is done:
   - Developer A: User Story 1
   - Developer B: User Story 2
3. US3 and US4 follow once their prerequisite pages exist

---

## Notes

- [P] tasks = different files, no dependencies on incomplete work
- [Story] label maps each task to a specific user story for traceability
- Same-file edits (UserService.java, UserServiceTest.java, AuthenticationControllerTest.java) are deliberately sequential across phases — never parallelize them
- Constitution Principle III: schema.sql/data.sql must NOT be modified; the User entity maps the existing table as-is
- Constitution Principle IV: no JavaScript dependencies in templates; errors render server-side adjacent to fields
- Commit after each task or logical group; stop at any checkpoint to validate the story independently
