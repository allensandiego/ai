# Implementation Plan: User Registration & Authentication

**Branch**: `001-user-auth` | **Date**: 2026-10-19 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/001-user-auth/spec.md`

## Summary

Add direct (no email verification) user registration and session-based authentication to the existing Spring Boot web app. A `User` JPA entity maps to the already-existing `users` table; a DB-backed `UserDetailsService` authenticates by username **or** email against Bcrypt-hashed passwords; registration is handled by a validated controller endpoint that persists an immediately usable account. All authentication pages (register, login) are reworked from static CoreUI demo markup into Thymeleaf forms inside the existing CoreUI admin layout, and a minimal authenticated home page provides the post-login destination and logout action.

## Technical Context

**Language/Version**: Java 21

**Primary Dependencies**: Spring Boot 4.1.1 (spring-boot-starter-webmvc, spring-boot-starter-security, spring-boot-starter-data-jpa, spring-boot-starter-thymeleaf, spring-boot-starter-validation), thymeleaf-extras-springsecurity6, H2, Lombok; Playwright 1.63.0 (test scope)

**Storage**: H2 file database (`jdbc:h2:file:./data/ai`), initialized via `schema.sql` / `data.sql`; existing `users` table reused unchanged

**Testing**: JUnit/Spring Boot test starters (`./mvnw test`) for unit + integration; Playwright for end-to-end authentication flows

**Target Platform**: Local web server (embedded Tomcat) with browser-rendered UI

**Project Type**: Web application (server-rendered Thymeleaf pages, Spring Security session auth)

**Performance Goals**: Standard interactive web app: registration and login requests complete in well under 2 seconds; full register→login user journey under 2 minutes (SC-001)

**Constraints**: No new frameworks or dependencies beyond the ratified Technology Baseline; deny-by-default access control retained; all pages reuse existing CoreUI layout/assets; no plaintext credentials persisted or logged

**Scale/Scope**: Small single-application admin UI; hundreds of registered users; one `users` table, three public auth endpoints plus one authenticated home page

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| # | Principle | Assessment |
|---|-----------|------------|
| I | Secure by Default | PASS — passwords stored only as Bcrypt hashes via Spring Security `PasswordEncoder`; all input validated server-side; existing deny-by-default rules retained (`anyRequest().authenticated()`); public paths limited to the narrowest set needed for login/register/static assets. |
| II | Convention-Aligned Architecture | PASS — code placed in the existing `config`, `controller`, `dto`, `model`, `repository`, `service` packages under `com.allensandiego.ai`; Spring idioms (configuration classes, DI) used; no new client-side frameworks; Thymeleaf conventions followed. The previously empty `dto/model/repository/service` packages are now justified by this feature's concrete need (entity + persistence + registration logic). |
| III | Data Integrity and Schema Care | PASS — no schema changes required: the existing `users` table (id, username unique, password, email unique, created_at) is reused as-is; entity mapping matches it exactly; `data.sql` already seeds a Bcrypt-hashed admin consistent with the new encoder. |
| IV | Usable and Accessible Server-Rendered UI | PASS — register/login pages reuse the existing CoreUI layout markup (card, form controls, labels); validation errors rendered adjacent to fields; generic login error without enumeration; no stack traces exposed; logout reachable from an authenticated page that renders meaningfully without JavaScript. |
| V | Relevant, Repeatable Testing | PASS (planned) — unit tests for service validation logic, Spring Boot integration tests for controller/security/repository behavior, Playwright e2e covering register→login→logout; H2 isolated per test; all run via `./mvnw test`. |
| VI | Focused, Maintainable Changes | PASS — scope limited to registration + authentication (+ minimal home page justified as the post-login destination and logout surface); no unrelated refactoring; no new dependencies. |

**Post-Phase 1 re-check**: PASS — design artifacts (data-model.md, contracts/, quickstart.md) introduce no schema drift, no new baseline components, and keep all public surfaces compatible with the existing `SecurityConfig` route set. No violations to justify.

## Project Structure

### Documentation (this feature)

```text
specs/001-user-auth/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
└── contracts/           # Phase 1 output (/speckit.plan command)
    └── http-endpoints.md
```

### Source Code (repository root)

```text
src/main/java/com/allensandiego/ai/
├── config/
│   └── SecurityConfig.java          # updated: BCryptPasswordEncoder bean, DB-backed UserDetailsService wiring, logout success URL
├── controller/
│   ├── AuthenticationController.java  # extended: GET+POST /authentication/register; login page already present
│   └── HomeController.java            # new: GET / authenticated home page (welcome + logout)
├── dto/
│   └── RegistrationRequest.java      # form DTO with Bean Validation constraints
├── model/
│   └── User.java                    # JPA entity mapped to existing `users` table
├── repository/
│   └── UserRepository.java          # Spring Data interface (existsByUsername, existsByEmail, findByUsernameOrEmail)
└── service/
    └── UserService.java             # registration + credential lookup logic

src/main/resources/templates/
├── authentication/
│   ├── login.html                   # reworked: Thymeleaf form posting to Spring Security form-login (identifier + password), inline error alert, register link
│   └── register.html                # reworked: Thymeleaf form (username, email, password, confirm) posting to /authentication/register, inline field errors
└── home.html                        # new: minimal CoreUI-styled authenticated page with logout action

src/test/java/com/allensandiego/ai/
├── service/UserServiceTest.java     # unit tests for validation/uniqueness logic
├── controller/AuthenticationControllerTest.java  # integration tests (MockMvc): register success/duplicate/validation, login redirect behavior
└── e2e/AuthenticationFlowIT.java    # Playwright: register → login → home → logout end-to-end
```

**Structure Decision**: Single-project layout (existing repository). All new code lives in the already-scaffolded `com.allensandiego.ai` packages per Constitution Principle II; no new modules, layers, or directories beyond one controller (`HomeController`) and one template (`home.html`), each justified by a concrete need of this feature (post-login destination + logout surface required by FR-008/SC-001).

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

No constitution violations were identified; no complexity justification is required.
