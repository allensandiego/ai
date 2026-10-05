# Quickstart Validation Guide: User Authentication System

**Date**: 2026-10-05  
**Feature**: User Authentication System (specs/001-user-auth)

---

## Purpose

This guide provides runnable validation scenarios to verify the user authentication feature works end-to-end. These tests confirm the implementation meets the specification requirements without including full implementation code.

---

## Prerequisites

1. **Java 21** installed and available in PATH
2. **Maven wrapper** (`mvnw`) in repository root
3. **H2 Console** enabled (included via spring-boot-starter-h2console)
4. **Database schema**: users table with columns: id, username, password_hash, email, enabled

---

## Setup Commands

### 1. Start the Application

```bash
nohup ./mvnw spring-boot:run > app.log 2>&1 &
```

Wait for application to start (check `app.log` for "Started AiApplication in... seconds")

### 2. Verify Application is Running

Open browser and navigate to:
- **Login page**: http://localhost:8080/login
- **Registration page**: http://localhost:8080/register
- **Password reset page**: http://localhost:8080/reset-password (if registered user)

---

## Validation Scenarios

### Scenario 1: User Registration (Success Path)

**Goal**: Verify new users can register and immediately log in without email confirmation.

**Steps**:
1. Navigate to http://localhost:8080/register
2. Fill registration form with:
   - Username: `testuser`
   - Password: `TestPass123!`
   - Email: `test@example.com` (optional)
3. Submit the form
4. Verify success message appears
5. Navigate to http://localhost:8080/login
6. Enter credentials:
   - Username: `testuser`
   - Password: `TestPass123!`
7. Submit login form
8. Verify redirection to dashboard (http://localhost:8080)

**Expected Outcome**: 
- Registration completes without error
- Login succeeds with same credentials
- User can access protected pages

---

### Scenario 2: Login with Invalid Credentials

**Goal**: Verify system rejects invalid credentials without revealing whether username exists.

**Steps**:
1. Navigate to http://localhost:8080/login
2. Enter any username and password (e.g., `fakeuser`, `wrongpass`)
3. Submit login form

**Expected Outcome**:
- Error message appears (e.g., "Invalid username or password")
- Message does NOT indicate whether username exists
- User remains on login page or sees generic error

---

### Scenario 3: Duplicate Username Registration

**Goal**: Verify system prevents duplicate usernames.

**Steps**:
1. Navigate to http://localhost:8080/register
2. Fill form with existing username `testuser` and new password `NewPass456!`
3. Submit the form

**Expected Outcome**:
- Error message indicates username already exists
- No new user record created in database
- User cannot proceed with registration

---

### Scenario 4: Password Reset (Success Path)

**Goal**: Verify users can reset their password without email confirmation.

**Steps**:
1. Log in as an existing user (or register a new one)
2. Navigate to password reset page (typically `/reset-password` or via profile menu)
3. Enter current username and request reset
4. Provide new password: `NewPassword789!`
5. Submit reset form
6. Log out
7. Attempt login with old password → should fail
8. Login with new password → should succeed

**Expected Outcome**:
- Password reset completes successfully
- Old password no longer works
- New password grants access

---

### Scenario 5: Non-Existent Username Request

**Goal**: Verify system does not reveal whether username exists during password reset.

**Steps**:
1. Navigate to password reset page
2. Enter a non-existent username (e.g., `nonexistent`)
3. Submit the form

**Expected Outcome**:
- Generic message indicating no action was taken
- Does NOT reveal that username doesn't exist
- No error or success indication

---

### Scenario 6: BCrypt Hash Verification

**Goal**: Verify passwords are stored as BCrypt hashes, not plaintext.

**Steps**:
1. Start H2 Console: `http://localhost:8085/h2-console`
2. Use JDBC URL: `jdbc:h2:mem:ai;DB_CLOSE_DELAY=-1`
3. Username: `sa`, Password: `sa` (or configured values)
4. Select `users` table from dropdown
5. Execute query: `SELECT username, password_hash FROM users LIMIT 1`

**Expected Outcome**:
- `password_hash` column contains BCrypt hash starting with `$2a$10` or `$2y$10`
- Does NOT contain plaintext password characters

---

## Success Criteria Checklist

After completing validation scenarios, verify:

- [ ] All six scenarios executed without application crashes
- [ ] Registration allows immediate login (no email confirmation required)
- [ ] Invalid login attempts show generic error messages
- [ ] Duplicate username registration is rejected with appropriate message
- [ ] Password reset works without email confirmation
- [ ] Database contains BCrypt-hashed passwords (not plaintext)
- [ ] Application logs show authentication events at appropriate levels

---

## Troubleshooting

### If application fails to start:
1. Check `app.log` for startup errors
2. Verify Java 21 is available: `java --version`
3. Ensure no port conflicts (default: 8080)

### If login fails despite correct credentials:
1. Check database connectivity via H2 Console
2. Verify users table has expected columns
3. Review SecurityConfig for misconfiguration

### If registration shows errors:
1. Check form validation on server side
2. Verify username uniqueness constraint exists
3. Review application logs for exception details

---

## Next Steps

Once all validation scenarios pass:
1. Proceed to `/speckit.tasks` to generate implementation tasks
2. Review generated tasks against specification requirements
3. Implement each task following the plan and data model
4. Run Playwright E2E tests to validate browser automation flows
