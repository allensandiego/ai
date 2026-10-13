# Quickstart: User Authentication Feature Validation

**Feature:** User Authentication (Direct Registration)  
**Spec Location:** `specs/user-auth/spec.md`  
**Created:** 2026-10-13  

---

## Prerequisites

### Application Setup

```bash
cd /home/allen/workspace/ai

# Build and run the application
mvn clean spring-boot:run -Dspring.profiles.active=dev

# Access the application at http://localhost:8080
```

### Database Configuration

The application uses H2 in-memory database by default for development. To use a persistent database:

```bash
# Set profile to use PostgreSQL (example)
export SPRING_PROFILES_ACTIVE=postgres

# Configure database URL, username, password in environment or application.yml
```

---

## Validation Scenarios

### Scenario 1: Successful User Registration

**Objective:** Verify new user can register and immediately log in

**Steps:**
1. Navigate to `http://localhost:8080/register`
2. Fill registration form:
   - Username: `testuser`
   - Email: `test@example.com`
   - Password: `Password123!`
   - CSRF token (auto-included in form)
3. Submit the form

**Expected Outcome:**
- Success message displayed
- User account created in database
- Can navigate to login page and log in with these credentials

**Verification Commands:**

```bash
# Check users table in H2 console
mvn spring-boot:run -Dspring.h2.console.enabled=true

# Or use browser to access H2 console at http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:testdb
# Username: sa
# Password: (none)
```

### Scenario 2: Successful User Login

**Objective:** Verify registered user can authenticate and access protected content

**Steps:**
1. Log in at `http://localhost:8080/login`
2. Enter credentials:
   - Username: `testuser`
   - Password: `Password123!`
3. Submit the form

**Expected Outcome:**
- Redirected to home page or configured welcome page
- User session created
- Can access protected resources without re-authentication

### Scenario 3: Invalid Login Attempt

**Objective:** Verify system handles invalid credentials securely

**Test Cases:**

#### Case A: Wrong Username
1. Enter any non-existent username
2. Enter any password
3. Submit

**Expected:** Generic error message "Invalid username or password"

#### Case B: Wrong Password
1. Enter valid username (`testuser`)
2. Enter incorrect password
3. Submit

**Expected:** Same generic error message (doesn't reveal account exists)

#### Case C: Invalid Credentials
1. Enter any invalid combination
2. Submit

**Expected:** Generic error message for all cases

### Scenario 4: Registration Validation

**Objective:** Verify input validation works correctly

**Test Cases:**

#### Case A: Username Too Short
- Username: `ab`
- Expected: Error "Username must be 3-50 alphanumeric characters"

#### Case B: Invalid Email Format
- Email: `invalidemail`
- Expected: Error "Please enter a valid email address"

#### Case C: Password Too Short
- Password: `12345`
- Expected: Error "Password must be at least 8 characters"

#### Case D: Duplicate Username
- Username: `testuser` (already registered)
- Email: `different@example.com`
- Expected: Error "An account with this username already exists"

#### Case E: Duplicate Email
- Username: `newuser`
- Email: `test@example.com` (already registered)
- Expected: Error "This email address is already registered"

### Scenario 5: CSRF Protection

**Objective:** Verify CSRF tokens prevent unauthorized state changes

**Test Cases:**

#### Case A: Missing CSRF Token
1. Use browser dev tools to submit form without CSRF token
2. Submit registration/login request

**Expected:** Error "Security verification failed. Please try again."

#### Case B: Expired/Stale CSRF Token
1. Clear browser cookies or use multiple tabs
2. Attempt to use old form data from different session

**Expected:** CSRF validation failure with appropriate error message

### Scenario 6: Protected Content Access Control

**Objective:** Verify only authenticated users can access protected resources

**Steps:**
1. Create a protected page (e.g., `/dashboard`)
2. Try to access without logging in

**Expected Outcome:**
- Redirected to login page with message "Please log in to access this page"
- Cannot directly access protected content

---

## Performance Validation

### Load Testing

```bash
# Use Apache Bench or similar tool
ab -n 100 -c 10 -p registration_request.txt http://localhost:8080/register

# Measure:
# - Response time (should be < 10 seconds for valid requests)
# - Success rate (should be ≥ 95% for valid inputs)
```

### Expected Metrics

| Metric | Target | Measurement Method |
|--------|--------|-------------------|
| Registration time | < 10 seconds | Average response time |
| Login time | < 5 seconds | Average response time |
| Success rate (valid input) | ≥ 95% | Test with known-good data |
| Error handling | < 2 seconds | Invalid input processing time |

---

## Security Validation

### Password Hash Verification

**Objective:** Verify passwords are stored as BCrypt hashes, not plaintext

**Steps:**
1. Register a user with password `Password123!`
2. Query the database for the user record
3. Inspect the `password` column value

**Expected Outcome:**
- Password column contains BCrypt hash format: `$2a$10$[salt][hash_data]`
- Original password NOT visible in database or logs
- Hash starts with `$2a$` prefix (BCrypt identifier)

### Session Security Verification

**Steps:**
1. Log in and inspect cookies
2. Check cookie attributes

**Expected Outcome:**
- Session cookie marked `Secure` (HTTPS only)
- Session cookie marked `HttpOnly` (no JavaScript access)
- Session ID properly scoped to domain

---

## Rollback Procedure

If validation fails and needs rollback:

```sql
-- Delete test user from database (H2 console or SQL client)
DELETE FROM users WHERE username = 'testuser';

-- Or drop entire schema for clean state
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
-- Re-run schema.sql to recreate tables
```

---

## Troubleshooting

### Issue: Registration Fails with Database Error

**Check:**
- Database connection is working
- Schema.sql has been applied
- User table exists with correct columns

**Verify:**
```bash
# Check H2 console for database status
mvn spring-boot:run -Dspring.h2.console.enabled=true
```

### Issue: CSRF Token Not Working

**Check:**
- Form includes `th:csrfField` attribute
- Spring Security CSRF filter is enabled
- Session cookies are being set

**Verify:**
- Inspect form source for `_csrf` hidden input field
- Check browser dev tools for session cookie presence

### Issue: Login Redirects Incorrectly

**Check:**
- `loginSuccessUrl` configured in security configuration
- Default redirect URL is set to `/home` or welcome page

**Fix:**
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.formLogin().loginSuccessUrl("/home").and()...;
        return http.build();
    }
}
```

---

## Next Steps

After successful validation:
1. Review `research.md` for technical decisions
2. Proceed to `/speckit.plan` or `/speckit.tasks` for implementation
3. Update README.md with setup instructions
4. Create Playwright E2E tests covering all scenarios
