# Data Model: User Authentication

**Feature:** User Authentication (Direct Registration)  
**Spec Location:** `specs/user-auth/spec.md`  
**Created:** 2026-10-13  

---

## Entity: User

### Current Schema (from schema.sql)

```sql
CREATE TABLE users (
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username     VARCHAR(50) NOT NULL UNIQUE,
    email        VARCHAR(100) NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Field Definitions

| Field | Type | Constraints | Purpose |
|-------|------|-------------|---------|
| `id` | BIGINT | Auto-generated identity | Primary key for database operations |
| `username` | VARCHAR(50) | NOT NULL, UNIQUE, alphanumeric 3-50 chars | User login identifier |
| `email` | VARCHAR(100) | NOT NULL, UNIQUE, valid email format | User contact address |
| `password` | VARCHAR(255) | NOT NULL | BCrypt-hashed password (never plaintext) |
| `created_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Account creation timestamp |

### State Transitions

```
┌─────────────┐     Register      ┌─────────────┐
│  Unregistered    │──────────────────►   Registered   │
└─────────────┘                    └─────────────┘
       ▲                                │
       │ Login/Logout                   │ Logout
       └────────────────────────────────┘
```

### Validation Rules

1. **Username:**
   - Must be 3-50 characters
   - Alphanumeric only (no special characters)
   - Case-sensitive
   - Unique across all users

2. **Email:**
   - Valid email format (contains @ and domain)
   - Maximum 100 characters
   - Unique across all users

3. **Password:**
   - Minimum 8 characters
   - Never stored in plaintext
   - BCrypt hashed with configurable rounds (default 10)

---

## Relationships

### None (Single Table Design)

The `users` table is a standalone entity with no foreign key relationships. This aligns with the simple authentication feature scope and the existing schema design.

---

## Indexes

| Index Name | Columns | Purpose |
|------------|---------|---------|
| PK_USERS_ID | id (primary) | Primary key lookups |
| UK_USERNAME | username | Prevent duplicate usernames, enable fast login lookups |
| UK_EMAIL | email | Prevent duplicate emails, enable fast lookups |

---

## Security Considerations

### Password Storage
- **Algorithm:** BCrypt (via Spring Security's `PasswordEncoder`)
- **Salt:** Automatic per-hash salt generation
- **Rounds:** Configurable via `spring.security.authentication.password.encoded` property (default: 10)
- **Storage Format:** `{bcrypt}$2a$10$[salt][hash_data]`

### Data Protection
- Passwords are hashed before database storage
- No plaintext passwords in logs or error messages
- Sensitive fields not exposed in API responses or template outputs

---

## Migration Notes

No schema changes required. The existing `users` table has all necessary columns for this feature:
- Use existing `username`, `email`, `password`, `created_at` columns
- No additional migration needed
- Password values will be stored as BCrypt hashes instead of plaintext

---

## References

- **Spec:** `specs/user-auth/spec.md`
- **Constitution:** `.specify/memory/constitution.md`
- **Existing Schema:** `src/main/resources/schema.sql`
