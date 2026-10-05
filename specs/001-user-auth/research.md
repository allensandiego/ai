# Research Findings: User Authentication System

**Date**: 2026-10-05  
**Feature**: User Authentication System (specs/001-user-auth)

---

## Decision: Spring Security with Form Login and JDBC UserDetailsService

### What was chosen
Spring Security's form login configuration combined with a custom UserDetailsService implementation that loads user data from the database via JDBC/repository pattern. This leverages the existing SecurityConfig structure already present in the application.

### Why chosen
This approach aligns with:
1. **Constitution Principle III**: Explicitly requires JDBC authentication with Spring Security form login
2. **Existing infrastructure**: The project already has SecurityConfig configured for form login at /login
3. **Minimal changes**: Extends existing security setup without introducing new complexity
4. **Standard practice**: This is the recommended pattern for Spring Boot applications requiring database-backed authentication

### Alternatives considered

**Option A: Session-based with in-memory user store**
- *Rejected*: Would not persist credentials to database; violates requirement to store accounts in users table

**Option B: LDAP authentication**
- *Rejected*: Overkill for local application benchmark; adds complexity without benefit; violates simplicity principle

**Option C: OAuth2/Social login**
- *Rejected*: Out of scope per specification which explicitly calls for JDBC authentication only

---

## Decision: BCrypt with Cost Factor 10

### What was chosen
BCrypt password hashing algorithm with cost factor of 10 (the Spring Security default for Java 21). This provides approximately 2^34 operations per hash calculation.

### Why chosen
1. **Constitution Principle III**: Explicitly requires BCrypt hashing; forbids plaintext storage
2. **Security standard**: Cost factor 10 is the recommended baseline for modern applications
3. **Performance acceptable**: Takes ~1 second to hash a password, which is appropriate for authentication security
4. **Spring Security default**: Leverages existing configuration; no custom setup required

### Alternatives considered

**Option A: Argon2**
- *Rejected*: While more secure theoretically, adds dependency complexity; BCrypt is sufficient and already configured

**Option B: PBKDF2 with higher iterations**
- *Rejected*: More complex to configure; BCrypt provides equivalent security with simpler setup

**Option C: Cost factor 12 or higher**
- *Rejected*: Significantly slower; cost factor 10 meets security requirements while maintaining acceptable UX

---

## Decision: No Email Confirmation Flow

### What was chosen
Users can register and reset passwords immediately without email verification. Password resets update the database directly upon request.

### Why chosen
1. **Specification explicit**: User input states "Allow users to reset their password without email confirmation"
2. **Scope boundary**: Permits implementation of core authentication features without additional complexity
3. **Local application context**: Benchmark template is for local testing; email infrastructure unnecessary
4. **Faster user experience**: Immediate access after registration improves usability for testing scenarios

### Alternatives considered

**Option A: Email confirmation required for registration**
- *Rejected*: Requires SMTP configuration, email templates, and async processing; out of scope per specification

**Option B: Token-based password reset with expiration**
- *Rejected*: Specification explicitly excludes complexity features like token management and expiration

**Option C: Admin-initiated password reset**
- *Rejected*: Does not meet requirement for user-initiated self-service password reset

---

## Decision: No Account Lockout Mechanism

### What was chosen
No implementation of account lockout, failed attempt tracking, or rate limiting on authentication endpoints.

### Why chosen
1. **Specification explicit**: User input states "Do not implement account lockout"
2. **Scope clarity**: Simplifies initial implementation; security enhancements can be added later
3. **Testing focus**: Lockout mechanisms complicate E2E testing without adding to core functionality validation

### Alternatives considered

**Option A: Simple lockout after 5 failed attempts**
- *Rejected*: Explicitly excluded per specification; would require session management and persistence

**Option B: Rate limiting with token bucket algorithm**
- *Rejected*: Adds significant complexity for out-of-scope feature

**Option C: CAPTCHA integration**
- *Rejected*: Requires external service or complex implementation; not needed for local benchmark

---

## Decision: Thymeleaf Templates Following CoreUI Structure

### What was chosen
Authentication pages built with Thymeleaf templates extending CoreUI Bootstrap 5 layout patterns.

### Why chosen
1. **Existing infrastructure**: index.html uses CoreUI template structure; consistent styling expected
2. **Specification requirement**: Uses "CoreUI Bootstrap 5" per tech stack documentation
3. **Bootstrap utility classes**: Leverages existing CSS framework for responsive design
4. **Thymeleaf-Security integration**: thymeleaf-extras-springsecurity6 dependency already configured

### Alternatives considered

**Option A: Plain HTML/CSS without framework**
- *Rejected*: Would break visual consistency; CoreUI provides polished admin interface

**Option B: Separate CSS file for auth pages**
- *Rejected*: Unnecessary duplication; CoreUI style.min.css already covers authentication UI needs

---

## Research Summary

All technical decisions have been documented with rationale and alternatives. The implementation will proceed with the following confirmed choices:

1. **Authentication mechanism**: Spring Security form login with JDBC UserDetailsService
2. **Password hashing**: BCrypt with cost factor 10
3. **User storage**: Existing users table (schema alignment required)
4. **UI framework**: Thymeleaf templates using CoreUI Bootstrap 5
5. **Excluded features**: Email confirmation, account lockout, password expiration, audit logging

The implementation plan is ready for Phase 1 design and task generation.
