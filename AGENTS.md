# Agent Instructions for ai.qwen3.5-4b-q8_0

## Project Overview

Spring Boot 4.1.1 application with Java 21, Thymeleaf, CoreUI admin template, H2 in-memory database, and Spring Security authentication. No email verification—direct login after registration. BCrypt password hashing configured.

## Developer Commands

```bash
# Run application
mvnw spring-boot:run

# Build
mvnw clean package -DskipTests

# Test only
mvnw test

# Run specific test
mvnw test -Dtest=AiApplicationTests
```

## Architecture & Boundaries

- **Package structure**: `com.allensandiego.ai`
  - `controller/`: REST and Thymeleaf controllers
  - `config/`: Security, data sources, validation configs
  - `service/`: Business logic layer (create as needed)
  - `repository/`: Data access layer (create as needed)
  - `entity/`: JPA entities (create as needed)

- **Entry points**: 
  - Main: `AiApplication.java`
  - Security config: `SecurityConfig.java`
  - Auth controller: `AuthenticationController.java`

## Authentication Flow

1. **Registration** (`/authentication/register.html`) → POST to `/authentication/register`
2. **Login** (`/authentication/login.html`) → POST to `/authentication/login`
3. **Logout** (`/authentication/logout`)
4. No email verification required—users can log in immediately after registration

## Security Configuration

- Spring Security 6 with Thymeleaf extras security support
- Form login at `/authentication/login`
- H2 console enabled: `spring.h2.console.enabled=true`
- Database credentials from `application.properties`:
  ```properties
  spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
  spring.datasource.username=sa
  spring.datasource.password=
  ```

## Password Security

- BCrypt password hashing (add dependency to pom.xml if not present)
- Use `PasswordEncoder` bean from Spring Security for password encoding/decoding

## Thymeleaf Templates

Base path for templates: `/templates/`
- Authentication pages in `templates/authentication/`:
  - `login.html`, `register.html`, `check-email.html`
  - `change-password.html`, `reset-password.html`
  - `password-changed.html`

## Static Assets (CoreUI)

Vendor assets loaded from `/vendors/` directory:
- CSS: `/css/style.css`, `/js/config.js`, `/js/color-modes.js`
- Components in `templates/components/`: buttons, cards, modals, tables, etc.

## Testing

- Playwright E2E tests in `src/test/java/com/allensandiego/ai/AiApplicationTests.java`
- Tests run against random port—use `@LocalServerPort` for dynamic endpoints
- Screenshot tests save to `target/screenshots/`

## Common Patterns

- Use `@SpringBootApplication` on main class
- Security filter chain configured in `SecurityConfig.java`
- Thymeleaf uses `th:href="@{...}"` and `th:src="@{...}"` for static assets
- CoreUI components loaded from `/vendors/` directory

## Known Constraints

- H2 database is in-memory—data resets on restart
- No email verification implemented (registration → immediate login)
- Password hashing via BCrypt (add dependency if not present)
- Hibernate DDL auto set to `none`—manual schema management required
