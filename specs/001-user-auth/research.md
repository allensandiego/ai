# Research & Resolved Decisions: User Registration & Authentication

**Feature**: [spec.md](./spec.md) | **Date**: 2026-10-19

No `NEEDS CLARIFICATION` markers remained in the Technical Context; every open question was resolved from the existing codebase and established Spring Security patterns. Findings below are consolidated as Decision / Rationale / Alternatives considered.

## R1: Authentication backend — DB-backed UserDetailsService

**Decision**: Implement a `UserDetailsService` that loads users from `UserRepository`, matching the submitted identifier against **username OR email**, and let Spring Security's standard form-login pipeline verify the password with a Bcrypt `PasswordEncoder`.

**Rationale**: The spec (FR-005) requires login by username *or* email; the existing `users` table already stores exactly what is needed (unique username, unique email, hashed password). A single lookup method (`findByUsernameOrEmail`) satisfies both identifiers without two separate code paths. Spring Security's form-login + DaoAuthenticationProvider handles session creation, failure handling, and saved-request redirect with no custom success/failure handlers required.

**Alternatives considered**:
- Two separate login forms (one for username, one for email) — rejected: doubles UI surface, violates the single generic-error requirement (FR-006).
- Custom authentication provider bypassing form-login — rejected: unnecessary complexity; the standard pipeline already meets FR-006/FR-007.

## R2: Password hashing — explicit BCryptPasswordEncoder bean

**Decision**: Register a `BCryptPasswordEncoder` bean in `SecurityConfig` and use it for both registration (encoding) and authentication (verification).

**Rationale**: The feature input mandates Bcrypt; the seeded admin hash in `data.sql` is already a `$2a$10$...` bcrypt string, so an explicit encoder keeps seeding and runtime behavior consistent. An explicit bean removes ambiguity about which algorithm encodes new registrations (FR-004) instead of relying on the default delegating encoder's negotiation.

**Alternatives considered**:
- Rely on Spring Security's default `DelegatingPasswordEncoder` — rejected: it would still verify the seeded hash, but new hashes would be stored in a prefixed format and the "Bcrypt" requirement would only hold implicitly; explicit is simpler to reason about and test.
- PBKDF2/Argon2 encoders — rejected: input explicitly mandates Bcrypt.

## R3: Registration endpoint behavior — validate, persist, redirect to login (no auto-login)

**Decision**: `POST /authentication/register` accepts a validated `RegistrationRequest` DTO; on success it persists the user and redirects to `/authentication/login?registered=true`, where the login page shows a "registration successful" alert. No session is created at registration time.

**Rationale**: The spec requires direct registration with *immediate usability for login* (FR-002, SC-002), not immediate authentication. Redirecting to login keeps one canonical entry point into authenticated state, avoids creating a session from an unauthenticated request, and makes the success path trivially testable end-to-end (SC-001).

**Alternatives considered**:
- Auto-login after registration — rejected: adds session-creation complexity on an anonymous endpoint and is not required by any acceptance scenario.
- Render a confirmation page instead of redirecting — rejected: extra template with no user value; the login page already exists and carries the next action.

## R4: Duplicate handling and error presentation

**Decision**: Uniqueness checks (`existsByUsername`, `existsByEmail`) run server-side before save; duplicates re-render the registration form with field-adjacent errors (FR-003, FR-009). Login failures use Spring Security's default failure redirect to `/authentication/login?error`, and the login template renders one generic alert that never states whether the identifier exists (FR-006, SC-005).

**Rationale**: Constitution Principle I requires server-side validation at trust boundaries; Principle IV requires errors adjacent to fields and no internal detail leakage. The `?error`/`?registered` query-parameter convention is the standard Spring Security + Thymeleaf pattern and keeps templates free of JavaScript dependencies.

**Alternatives considered**:
- JSON error responses — rejected: this is a server-rendered app; form re-render with inline errors matches Principle IV.
- Distinct "user not found" vs "bad password" messages — rejected: violates FR-006/SC-005 (enumeration).

## R5: Post-login destination and logout surface

**Decision**: Add a minimal authenticated `GET /` home page (`home.html`, CoreUI-styled) showing the authenticated username and a logout form button posting to `/authentication/logout`; configure `logoutSuccessUrl("/authentication/login")` for deterministic post-logout behavior. Successful login returns to the originally requested URL via Spring Security's default saved-request handler, falling back to `/`.

**Rationale**: FR-008 requires a reachable logout action and SC-001/SC-004 require an observable authenticated state; with no existing home controller/template, `/` currently 404s after login. A single small page is the minimal surface that satisfies both (Constitution Principle VI: justified by concrete need).

**Alternatives considered**:
- No home page; rely only on saved-request redirects — rejected: a fresh login has no prior request to return to, leaving users with nowhere to land and no logout control.
- Reuse an existing CoreUI dashboard template as the home page — rejected: out of scope for this feature (charts/widgets are unrelated); a minimal page keeps the change focused.

## R6: Template rework scope

**Decision**: Convert `login.html` and `register.html` from static CoreUI demo markup into Thymeleaf forms that keep the existing CoreUI layout/card/form-control styling verbatim, but replace non-functional demo elements (social login buttons, "remember me" checkbox, `method="get"` actions, hardcoded `.html` links) with working form posts and CSRF-protected actions.

**Rationale**: FR-009 mandates adherence to the CoreUI admin theme; the existing templates already provide that layout — reusing their markup satisfies Principle IV (no duplicated/off-theme markup) while making the forms functional. Removing dead demo elements is required for correctness (they currently submit GET requests to non-endpoints).

**Alternatives considered**:
- Build new pages from `coreui/blank.html` base — rejected: duplicates existing layout markup, violating Principle IV's no-duplication rule.
- Keep social-login buttons as decorative links — rejected: dead UI that misleads users; out of scope.

## R7: Testing strategy

**Decision**: Three test layers per Constitution Principle V: (1) unit tests for `UserService` validation/uniqueness logic with a mocked repository; (2) Spring Boot integration tests via MockMvc covering register success/duplicate/validation failures, login redirect on failure (`?error`), and anonymous→login redirects for protected paths; (3) Playwright e2e test driving the full register → login → home → logout journey in a real browser. H2 is isolated/reset per test for repeatability.

**Rationale**: Principle V explicitly requires unit + Spring Boot integration + Playwright coverage "at minimum authentication"; all run under `./mvnw test` (Quality Gate 1–2).

**Alternatives considered**:
- Integration tests only — rejected: service validation logic is cheap to unit-test and gives faster regression signal.
- Manual-only verification — rejected: violates Principle V and Quality Gates.
