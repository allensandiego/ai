# Data Model: User Registration & Authentication

**Feature**: [spec.md](./spec.md) | **Date**: 2026-10-19

## Entity: User

Represents a registered account. Maps to the existing `users` table (no schema changes required — see Constitution Check, Principle III).

| Field | Type | Constraints | Notes |
|-------|------|-------------|-------|
| id | Long | Primary key, auto-increment | Internal identifier; never shown to users |
| username | String | Not null, unique, max 255 chars | Login identifier (option A) |
| password | String | Not null, max 255 chars | Bcrypt hash only — plaintext MUST never be stored or logged (FR-004) |
| email | String | Not null, unique, max 255 chars | Login identifier (option B); syntactically validated at registration |
| createdAt | Timestamp | Default current time on insert | Audit metadata; not user-editable |

## Relationships

None. `User` is the sole entity for this feature; no foreign keys or associations are introduced.

## Validation Rules (from spec requirements)

Applied server-side before persistence (FR-003, Constitution Principle I):

| Rule | Requirement source | Failure behavior |
|------|--------------------|------------------|
| Username required, non-blank, ≤ 255 chars | FR-003 / edge case "long inputs" | Re-render registration form; error adjacent to username field |
| Email required, syntactically valid, unique, ≤ 255 chars | FR-003 | Re-render; error adjacent to email field (duplicates: "already in use") |
| Password ≥ 8 characters, ≤ 255 chars | FR-003 / Assumptions | Re-render; error adjacent to password field |
| Password confirmation matches password | FR-001/FR-003 (Assumptions) | Re-render; error adjacent to confirmation field |
| Username unique across existing users | FR-003, SC-002 | No account created; inline duplicate error; no other data revealed |

## State Transitions

None. Registration is direct: a `User` row exists either not at all or as immediately usable for login (FR-002). There is no pending/verified/suspended state — explicitly no email verification step of any kind.

## Invariants

1. At most one user per username and at most one user per email (enforced by unique columns + pre-save checks).
2. `password` always contains a Bcrypt hash; the encoder used for registration is the same family as the seeded admin hash in `data.sql`.
3. Authentication state is derived exclusively from successful credential verification against this entity — no separate session-store user record is maintained.
