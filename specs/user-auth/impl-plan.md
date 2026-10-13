# Implementation Plan: User Authentication

**Feature:** User Authentication (Direct Registration)  
**Spec Location:** `specs/user-auth/spec.md`  
**Created:** 2026-10-13  

---

## Technical Context

### Architecture Overview
This feature will integrate with the existing Spring Boot application using:
- **Spring Security** for authentication and authorization
- **JDBC** for database access (users table already exists in schema.sql)
- **Thymeleaf** for registration/login forms and protected content
- **BCrypt** for password hashing (already configured via Spring Security)

### Technical Decisions [NEEDS CLARIFICATION]

#### Password Strength Policy
The spec mentions "password strength validation" but doesn't define specific requirements. Options:
1. **Minimal**: Length ≥ 8 characters (aligned with OWASP recommendations)
2. **Moderate**: Length ≥ 8, one number + one letter
3. **Strict**: Length ≥ 12, uppercase, lowercase, number, special character

**Decision:** Minimal approach (length ≥ 8) recommended for initial implementation to reduce friction while maintaining security. Can be enhanced later if needed.

---

## Constitution Check

### ✅ JAVA_VERSION
- Implementation uses Java 21 features available in Spring Security 4.1.1
- No violations

### ✅ SPRING_BOOT_VERSION  
- Aligns with Spring Boot 4.1.1 standard patterns
- Uses established security starters
- No violations

### ✅ SERVER_RENDERING
- Forms will use Thymeleaf templates
- HTMX can be used for partial form validation if desired
- No JavaScript dependencies required for core flows
- Compliant

### ✅ SECURITY_FRAMEWORK
- Using Spring Security's built-in authentication mechanism
- BCrypt password hashing via `PasswordEncoder` bean
- Passwords never stored in plaintext
- Compliant

### ⚠️ CSRF_PROTECTION
- **Action Required:** Ensure registration and login forms include CSRF tokens
- Spring Security will automatically add CSRF token to forms
- Forms must use Thymeleaf's `th:csrfField` attribute
- HTMX requests to state-changing endpoints require valid CSRF tokens
- Compliant (with implementation)

### ✅ DATABASE_LOCAL
- Uses existing `users` table from schema.sql
- H2 for development and tests
- Production database configured via profile
- Compliant

### ⚠️ DATABASE_SCHEMA
- **Action Required:** Verify schema.sql includes all required columns:
  - `username` varchar(50) unique ✓ (already exists)
  - `email` varchar(100) unique ✓ (already exists)
  - `password` varchar(255) ✓ (already exists, will store hash)
  - `created_at` timestamp ✓ (already exists)
- No schema changes needed
- Compliant

### ✅ LAYER_BOUNDARIES
- Controllers: Handle HTTP requests (registration, login endpoints)
- Services: Authentication logic, password hashing
- Persistence: JPA repository for user CRUD
- Security: Spring Security filter chain configuration
- Templates: Thymeleaf forms and views
- Compliant

### ✅ TESTABILITY
- All components are testable via standard Spring testing
- Integration tests with in-memory H2 database
- Unit tests for authentication logic
- Compliant

### ✅ END_TO_END_TESTS
- Playwright tests will verify registration flow
- Login success/failure scenarios
- Protected content access control
- Screenshot capture enabled
- Compliant

### ✅ SECRETS_MANAGEMENT
- No secrets in codebase (BCrypt rounds can be configured)
- Production database credentials via environment variables
- `.gitignore` protects sensitive files
- Compliant

### ✅ DOCUMENTATION_MAINTENANCE
- This plan documents implementation approach
- Will update README.md with setup instructions
- Security documentation will reference Spring Security best practices
- Compliant

---

## Phase 0: Research & Decisions

### research.md

```markdown
# Research: User Authentication Implementation

**Feature:** Direct Registration with Spring Security  
**Date:** 2026-10-13  

---

## Decision 1: Password Strength Policy

**Chosen:** Minimal approach (length ≥ 8 characters)

**Rationale:** 
- Reduces user friction during initial registration
- OWASP recommends minimum 8 characters as baseline
- Can be enhanced to moderate/strict policies in future iterations
- Aligns with the spec's assumption that password reset is out of scope

**Alternatives Considered:**
- **Moderate (length ≥ 8, one number + letter):** More secure but may increase abandonment
- **Strict (complexity requirements):** Maximum security but significantly increases friction

---

## Decision 2: Session Management Strategy

**Chosen:** Spring Security's default session-based approach with configurable timeout

**Rationale:**
- Simple to implement using existing Spring Security configuration
- Aligns with assumption #2 in spec
- Easy to configure timeout via properties
- Standard approach for web applications

**Alternatives Considered:**
- **Token-based (JWT):** More complex, requires additional infrastructure
- **Remember Me:** Out of scope per spec, would complicate initial implementation

---

## Decision 3: Authentication Provider

**Chosen:** Spring Security's DefaultAuthenticationProvider with JDBC userDetailsService

**Rationale:**
- Matches constitution requirement for JDBC-backed authentication
- Leverages existing users table structure
- Standard Spring Security pattern
- Well-tested and secure

**Alternatives Considered:**
- **Custom AuthenticationProvider:** Unnecessary complexity, no security benefit
- **LDAP/Active Directory:** Not required per spec, would need additional infrastructure

---

## Decision 4: Error Message Strategy

**Chosen:** Generic error messages that don't reveal account existence

**Rationale:**
- Prevents user enumeration attacks
- Aligns with FR-05 requirement for non-revealing error messages
- "Invalid username or password" for both cases

**Alternatives Considered:**
- **Specific errors:** Would leak information about account status (not secure)
- **No errors:** Poor user experience, doesn't help legitimate users

---

## Decision 5: Form Validation Approach

**Chosen:** Client-side Thymeleaf validation + Server-side Spring Validation

**Rationale:**
- Best of both worlds: immediate feedback and security
- Thymeleaf provides instant UX improvements
- Server validation is the source of truth
- Aligns with usability principle in constitution

**Alternatives Considered:**
- **Server-only validation:** More secure but worse UX
- **JavaScript-only validation:** Insecure, doesn't match "no JS where practical" principle
```

