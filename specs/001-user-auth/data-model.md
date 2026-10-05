# Data Model: User Authentication System

**Date**: 2026-10-05  
**Feature**: User Authentication System (specs/001-user-auth)

---

## Entities

### User Entity

Represents a registered user account stored in the database. Maps to the `users` table.

#### Fields

| Field | Type | Constraints | Source |
|-------|------|-------------|--------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Database schema |
| username | VARCHAR(50) | NOT NULL, UNIQUE | User input (registration/login) |
| password_hash | VARCHAR(255) | NOT NULL | BCrypt hash of user password |
| email | VARCHAR(100) | NULLABLE | Optional; not used for authentication flow |
| enabled | BOOLEAN | DEFAULT TRUE | Account status; controls login availability |

#### Validation Rules

- **username**: 3-50 characters, alphanumeric only (no special characters)
- **password_hash**: Must be valid BCrypt hash (starts with `$2a$10` or `$2y$10`)
- **email**: Optional; if provided must match standard email format
- **enabled**: Defaults to TRUE upon registration

#### State Transitions

```
New User (registration) → Active (enabled=TRUE) → Disabled (enabled=FALSE, admin action only)
Password Reset: Active → Password Updated → Active
Login Attempt: Active → Success OR Failure (no state change)
```

---

## Authentication Session Model

Represents an active login session maintained by Spring Security. Not persisted to database; stored in HTTP session.

#### Fields (Session-Scoped)

| Field | Type | Constraints | Source |
|-------|------|-------------|--------|
| sessionId | String | NOT NULL, UNIQUE | Spring Security generated |
| userId | BIGINT | NOT NULL, FOREIGN KEY → users.id | Current authenticated user |
| username | VARCHAR(50) | NOT NULL | Retrieved from users table |
| loginTime | TIMESTAMP | NOT NULL | When authentication succeeded |

#### State Transitions

```
New Session (login success) → Active → Invalidated (logout or timeout)
```

---

## Database Schema Impact Analysis

### Existing Users Table (Required Fields)

Based on the specification and existing application structure, the users table must contain:

```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### Required Migrations (if not already present)

If the existing users table schema differs, these migrations are needed:

1. **Add password_hash column** if missing:
   ```sql
   ALTER TABLE users ADD COLUMN password_hash VARCHAR(255) NOT NULL AFTER username;
   ```

2. **Ensure UNIQUE constraint on username**:
   ```sql
   ALTER TABLE users ADD CONSTRAINT uk_users_username UNIQUE (username);
   ```

3. **Add enabled flag** if missing:
   ```sql
   ALTER TABLE users ADD COLUMN enabled BOOLEAN DEFAULT TRUE AFTER email;
   ```

---

## Entity Relationships

### User → Authentication Session (One-to-Many)
- One user can have multiple concurrent sessions
- Sessions are transient and stored in HTTP session, not database

### User → User Registration (Self-referential)
- Registration creates a new User record
- No additional entities required; registration is inline with user creation

---

## Data Flow Diagrams

### Registration Flow

```
1. User submits registration form → 2. AuthService.validateRegistration()
3. Check username uniqueness → 4. Hash password with BCrypt
5. Create User entity → 6. UserRepository.save()
7. Redirect to login page
```

### Login Flow

```
1. User submits credentials → 2. SecurityFilterChain intercepts
3. UserDetailsService.loadUserByUsername() → 4. Find user in database
5. BCrypt.comparePassword() → 6. Grant session or deny access
```

### Password Reset Flow

```
1. User requests reset → 2. Verify username exists
3. Generate new password hash with BCrypt
4. Update user record in database
5. Allow immediate login with new credentials
```

---

## Assumptions Made

1. **Existing users table**: The schema.sql data model contains the users table with at minimum: id, username, and password_hash columns
2. **Unicode support**: VARCHAR fields use UTF-8 encoding to support international usernames
3. **Timestamps**: created_at and updated_at use server-local timezone (UTC recommended for production)
4. **Password length**: No explicit limit specified; implementation follows database VARCHAR(255) for password_hash

---

## Dependencies on Other Models

None at this time. Future features may add:
- Role-based authorization (users → roles, many-to-many)
- Profile information (separate profile table or extended user entity)
- Audit logging (separate events table for security events)

These are explicitly out of scope per the current specification.
