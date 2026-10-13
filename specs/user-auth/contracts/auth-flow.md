# Authentication Flow Contract

**Feature:** User Authentication (Direct Registration)  
**Spec Location:** `specs/user-auth/spec.md`  
**Created:** 2026-10-13  

---

## Overview

This document defines the expected behavior of user authentication operations. It describes what the system should do from a behavioral perspective, independent of implementation details.

---

## Registration Endpoint Contract

### Request Format (HTML Form)

```
POST /register
Content-Type: application/x-www-form-urlencoded

username={username}
email={email}
password={password}
_csrf={csrf_token}
```

### Response Codes

| Code | Condition | Message |
|------|-----------|---------|
| 200 | Valid input, new account created | "Registration successful. You can now log in." |
| 400 | Invalid username format | "Username must be 3-50 alphanumeric characters" |
| 400 | Invalid email format | "Please enter a valid email address" |
| 400 | Password too short | "Password must be at least 8 characters" |
| 409 | Username already exists | "An account with this username already exists" |
| 409 | Email already registered | "This email address is already registered" |
| 403 | Invalid CSRF token | "Security verification failed. Please try again." |

### Success Response (200)

```html
<div class="alert alert-success">Registration successful. You can now log in.</div>
```

---

## Login Endpoint Contract

### Request Format (HTML Form)

```
POST /login
Content-Type: application/x-www-form-urlencoded

username={username}
password={password}
remember_me={boolean}
_csrf={csrf_token}
```

### Response Codes

| Code | Condition | Message |
|------|-----------|---------|
| 302 | Valid credentials, redirect to home | Redirect to `/home` or configured welcome page |
| 403 | Invalid credentials | "Invalid username or password" |
| 403 | Invalid CSRF token | "Security verification failed. Please try again." |

### Success Response (302)

HTTP redirect with `Location: /home` header

---

## Logout Endpoint Contract

### Request Format

```
POST /logout
Content-Type: application/x-www-form-urlencoded

_csrf={csrf_token}
```

### Response Codes

| Code | Condition | Action |
|------|-----------|--------|
| 302 | Valid CSRF token, session terminated | Redirect to `/login` with "Logged out" message |
| 403 | Invalid/missing CSRF token | "Security verification failed. Please try again." |

### Success Response (302)

HTTP redirect with `Location: /login` header

---

## Protected Content Access Contract

### Request to Protected Resource

```
GET /protected-resource
```

### Response Codes

| Code | Condition | Message |
|------|-----------|---------|
| 200 | User authenticated | Return protected content |
| 302 | Not authenticated | Redirect to `/login` with "Please log in to access this page" |
| 403 | Authentication failed | "Access denied. Please log in." |

### Success Response (200)

Protected content rendered with user-specific data if applicable

---

## Security Requirements

### CSRF Protection
- All state-changing operations (POST/PUT/DELETE) require valid CSRF token
- CSRF token included as form field or hidden input
- Token validated on server before processing request
- Token regenerated on each session

### Session Management
- Sessions stored in HTTP cookies
- Cookies marked `Secure` and `HttpOnly`
- Session timeout configurable (default: 20 minutes)
- Logout immediately terminates session

### Password Security
- Passwords hashed using BCrypt with configurable rounds
- No plaintext passwords logged or exposed
- Failed login attempts tracked for rate limiting

---

## User Interface Contracts

### Registration Form

**Required Fields:**
- Username (3-50 alphanumeric characters)
- Email (valid email format)
- Password (minimum 8 characters)

**Optional Fields:**
- Remember Me checkbox (persistent login, default: false)

**Validation Feedback:**
- Real-time client-side validation
- Server-side confirmation on submit
- Error messages specific to each field

### Login Form

**Required Fields:**
- Username
- Password

**Optional Fields:**
- Remember Me checkbox

**Validation Feedback:**
- Immediate feedback for invalid credentials
- Generic error message (doesn't reveal account status)

---

## Performance Requirements

| Operation | Target Time | Notes |
|-----------|-------------|-------|
| Registration submission | < 10 seconds | End-to-end including database write |
| Login validation | < 5 seconds | Password hashing included |
| Logout processing | < 2 seconds | Session cleanup |
| Protected content load | < 3 seconds | Assuming authenticated user |

---

## Accessibility Requirements

- Forms labeled with proper HTML `for` attributes
- Error messages visible to screen readers
- Keyboard navigation supported for all form fields
- Focus management on error display
- Sufficient color contrast for all text

---

## References

- **Spec:** `specs/user-auth/spec.md`
- **Data Model:** `data-model.md`
- **Research Decisions:** `research.md`
