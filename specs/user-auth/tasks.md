# User Authentication Implementation Guide

**Feature:** User Authentication (Direct Registration)  
- **Specification:** [spec.md](./spec.md)
- **Data model:** [data-model.md](./data-model.md)
- **Authentication flow:** [contracts/auth-flow.md](./contracts/auth-flow.md)
- **Quickstart:** [quickstart.md](./quickstart.md)
- **Task documents:** [tasks/](./tasks/) · [task overview](./tasks/000-overview.md)

## How to use this guide

This page describes the implementation path and shared acceptance goals. Each numbered file in [`tasks/`](./tasks/) contains the scope and status for one task. Work through the phases below in order, using the individual files for task-level details. Update a task file's status as work progresses.

The phase order is:

**Setup → Foundational infrastructure → Registration → Login → Polish**

The task IDs follow the original plan. T016 and T017 are not defined.

## Implementation roadmap

| Phase | Focus | Task documents | Status |
|---|---|---|---|
| 1. Setup | Prepare the application structure and development environment. | [T001–T007](./tasks/) | Not started |
| 2. Foundational | Establish shared persistence, password encoding, and security configuration. | [T008–T015](./tasks/) | Not started |
| 3. Registration (US1) | Allow visitors to register and then sign in with the new account. | [T018–T027](./tasks/) | Not started |
| 4. Login (US2) | Authenticate existing users, protect content, and support logout. | [T028–T037](./tasks/) | Not started |
| 5. Polish | Harden security and improve form feedback and error handling. | [T038–T045](./tasks/) | Not started |

The user stories are delivered in priority order. Complete each phase's prerequisites before moving on; task-level dependencies and status are recorded in the linked task files.

## Acceptance goals

- A visitor can create an account with valid details and immediately sign in.
- Valid credentials grant access to protected content; invalid credentials produce a generic error that does not disclose account status.
- Users can log out and lose access to protected content.
- Authentication forms have CSRF protection and client- and server-side validation.
- Registration completes within 10 seconds and login validation within 5 seconds at the 95th percentile.
- All unit and integration tests pass, and code review/testing identifies no security vulnerabilities.

## Verification guide

Cover these behaviors with integration tests:

- Registration with valid and invalid submissions
- Successful and unsuccessful login
- Logout and session termination
- CSRF validation on authentication forms
- Access control for protected content

Cover these behaviors with unit tests:

- Passwords are stored using BCrypt hashing
- Field validation
- Authentication provider integration
- Session management

## Progress

All task files are open and no implementation work is marked complete. Treat the original checklist's completion marks as unverified; update task and phase statuses only after implementation and relevant verification.
