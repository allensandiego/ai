# Interface Contracts: User Registration & Authentication

**Feature**: [spec.md](./spec.md) | **Date**: 2026-10-19

This application exposes a server-rendered web UI; its public interface is the set of HTTP endpoints below. All form posts are CSRF-protected (hidden `_csrf` token rendered by Thymeleaf). Field names in parentheses are the exact request parameter names.

## Public (unauthenticated) endpoints

### GET /authentication/login
- **Purpose**: Render the login page (CoreUI admin layout).
- **Query params**: `error=true` → show generic "invalid credentials" alert; `registered=true` → show "registration successful, please sign in" alert.
- **Form fields on render**: identifier input named `username` (label: "Username or email"), password input named `password`.

### POST /authentication/login
- **Purpose**: Spring Security form-login authentication.
- **Request params**: `username` (identifier — matched against username OR email), `password`, `_csrf`.
- **Success**: 302 redirect to the originally requested protected URL (saved-request behavior); if none, `/`. Session established; user is authenticated thereafter.
- **Failure**: 302 redirect to `/authentication/login?error`. The rendered alert is a single generic message identical for unknown identifiers and wrong passwords (FR-006, SC-005).

### GET /authentication/register
- **Purpose**: Render the registration page (CoreUI admin layout).
- **Form fields on render**: `username`, `email`, `password`, `passwordConfirm`.

### POST /authentication/register
- **Purpose**: Create a new account directly — no email verification step (FR-002).
- **Request params**: `username` (required, unique, ≤ 255), `email` (required, valid format, unique, ≤ 255), `password` (≥ 8 chars, ≤ 255), `passwordConfirm` (must equal `password`), `_csrf`.
- **Success**: User persisted with Bcrypt-hashed password; 302 redirect to `/authentication/login?registered=true`. Account is immediately usable for login.
- **Failure (validation or duplicate)**: 400 response re-rendering the registration form with field-adjacent error messages; no account created; existing-account details are not revealed beyond "already in use" on the conflicting field(s).

### POST /authentication/logout
- **Purpose**: Terminate the current session.
- **Request params**: `_csrf`.
- **Behavior**: 302 redirect to `/authentication/login` (configured `logoutSuccessUrl`). Subsequent requests for protected pages are redirected to login until re-authentication (FR-008).

## Authenticated endpoints

### GET /
- **Purpose**: Minimal authenticated home page (CoreUI admin layout) showing the authenticated username and a logout action.
- **Access**: requires authentication; anonymous requests are 302-redirected to `/authentication/login` with the original destination preserved for post-login return (FR-007, SC-004).

## Access-control contract (deny-by-default)

| Path | Anonymous | Authenticated |
|------|-----------|---------------|
| /authentication/login, /authentication/register | 200 | 200 |
| /css/**, /js/**, /images/**, other static assets | 200 | 200 |
| / (home) and all other paths | 302 → login (destination preserved) | 200 |

## Error-surface contract

- No raw stack traces, internal identifiers, or sensitive data in any user-facing response (FR-010).
- Login failure message is generic and non-enumerating; registration errors are specific per field but reveal nothing about other accounts.
