# Quickstart Validation Guide: User Registration & Authentication

**Feature**: [spec.md](./spec.md) | **Plan**: [plan.md](./plan.md) | **Date**: 2026-10-19

Runnable scenarios that prove the feature works end-to-end. Endpoint and field details are defined in [contracts/http-endpoints.md](./contracts/http-endpoints.md); entity/validation rules in [data-model.md](./data-model.md).

## Prerequisites

- JDK 21 on `PATH`
- Maven (use the wrapper: `./mvnw`)
- A modern browser for manual scenarios and Playwright browsers for e2e tests (`npx playwright install chromium` if not already installed)

## Setup & Run

```bash
# from repository root
./mvnw spring-boot:run
```

Application starts on `http://localhost:8080`. The H2 file database is created/reset per start via `schema.sql`/`data.sql` (documented dev-time exception, Constitution Principle III); the seeded `admin` account (`admin@demo.com`) is available for login tests.

## Scenario 1 — Direct registration → immediate login (SC-001, SC-002)

1. Open `http://localhost:8080/authentication/register`.
2. Submit a unique username, a valid unique email, and a password of ≥ 8 characters with matching confirmation.
3. **Expected**: redirect to the login page showing a "registration successful" alert; no email or external verification step occurs at any point.
4. Log in with the just-created credentials (identifier may be the username *or* the email).
5. **Expected**: redirected to `http://localhost:8080/` showing an authenticated home page with the username and a logout action — total journey well under 2 minutes.

## Scenario 2 — Registration validation & duplicates (FR-003, FR-009)

1. Submit registration with an already-used username or email → **Expected**: form re-renders with "already in use" adjacent to that field; no account created.
2. Submit with a < 8-character password or mismatched confirmation → **Expected**: per-field validation errors, no account created.
3. Submit empty/whitespace fields or inputs > 255 chars → **Expected**: per-field validation errors.

## Scenario 3 — Login failure is generic and non-enumerating (FR-006, SC-005)

1. Log in with a non-existent identifier + any password, then with an existing identifier + wrong password.
2. **Expected**: both produce the identical single generic error alert on `/authentication/login?error`; no message reveals which part was wrong or whether the account exists.

## Scenario 4 — Anonymous access to protected pages (FR-007, SC-004)

1. While logged out, open `http://localhost:8080/` directly.
2. **Expected**: redirect to `/authentication/login`; after logging in, the user is returned to `/`.

## Scenario 5 — Logout terminates the session (FR-008)

1. From the authenticated home page, trigger logout.
2. **Expected**: redirect to `/authentication/login`; requesting `/` again redirects back to login until re-authentication.

## Scenario 6 — UI adherence (FR-009, SC-006)

Visually inspect `register`, `login`, and the home page: all render inside the shared CoreUI admin layout with consistent card/form-control styling, labeled inputs, semantic markup, and field-adjacent errors; no duplicated or off-theme markup.

## Automated verification

```bash
# full build + test suite (unit, integration, Playwright e2e) — Quality Gate 1–2
./mvnw verify
```

Expected: all tests pass with no unexplained skips; the Playwright e2e covers Scenario 1 and Scenario 5 in a real browser. Individual layers can be targeted via standard Maven surefire/failsafe test selection if needed during development.
