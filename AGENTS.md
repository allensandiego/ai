# AGENTS.md

## Project Structure
```text
.
├── pom.xml
├── opencode.jsonc
├── .mvn/wrapper/maven-wrapper.properties
├── .vscode/settings.json
└── src/
    ├── main/
    │   ├── java/com/allensandiego/ai/
    │   │   ├── AiApplication.java
    │   │   ├── config/
    │   │   │   └── SecurityConfig.java
    │   │   └── controller/
    │   │       ├── AuthenticationController.java
    │   │       └── HomeController.java
    │   └── resources/
    │       ├── application.properties
    │       ├── data.sql
    │       ├── schema.sql
    │       ├── static/        # assets, css, js, vendors (CoreUI template assets)
    │       └── templates/     # Thymeleaf views (authentication, components, error-pages, forms, blank.html, index.html)
    └── test/
        └── java/com/allensandiego/ai/
            └── AiApplicationTests.java  # Playwright integration tests
```

## Overview
- Spring Boot 4.1.1 (Java 21), Maven project (`pom.xml`).
- Package: `com.allensandiego.ai`. Main class: `AiApplication`.
- Web stack: Spring MVC, Thymeleaf, Spring Security, Spring Data JPA, Bean Validation, Actuator, Lombok.
- Database: H2 in-memory database with SQL schema and seed data.
- UI: CoreUI Bootstrap template assets in `src/main/resources/static/`, Thymeleaf templates in `src/main/resources/templates/`.
- Dev-only: Spring Boot DevTools, H2 console enabled.
- Testing: JUnit 5, Spring Boot Test, Playwright 1.63.0 for end-to-end browser integration tests.

## Build/Test Commands
- Build: `mvn compile`
- Test: `mvn test`
- Run app: `mvn spring-boot:run`
- Package: `mvn package`

## Database & SQL Config
- `src/main/resources/application.properties`:
  - App name: `ai`
  - SQL init: `spring.sql.init.mode=always`, `spring.sql.init.schema-locations=classpath:schema.sql`
  - H2 mem: `jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1` (user `sa`, no password, driver `org.h2.Driver`)
  - JPA / Hibernate: `ddl-auto=create-drop`, `show-sql=true`, `format_sql=true`
  - Logging levels: `DEBUG` for datasource init and Hibernate SQL, `TRACE` for JDBC parameter binding
  - H2 console: `spring.h2.console.enabled=true`
- Schema (`src/main/resources/schema.sql`):
  - Tables: `roles` (id, code, name, description, enabled, timestamps), `users` (id, username, password, enabled, authorities, timestamps), `user_roles` (id, user_id, role_id, enabled, timestamps)
- Seed Data (`src/main/resources/data.sql`):
  - Initial roles: `ROLE_ADMIN`, `ROLE_USER`

## Security (important)
- `config/SecurityConfig.java`:
  - Permits without authentication:
    - Static resources: `/assets/**`, `/css/**`, `/js/**`, `/vendors/**`
    - H2 console: `/h2-console/**`
    - Auth endpoints: `/authentication/login`, `/authentication/register`
    - Error pages: `/error`
  - All other requests require authentication (`anyRequest().authenticated()`).
  - Form login:
    - `loginPage="/authentication/login"`
    - `usernameParameter="username"`
    - `successForwardUrl="/dashboard"`
  - Logout:
    - `logoutUrl="/authentication/logout"`, `permitAll()`
  - Headers:
    - Frame options set to `sameOrigin` (required for H2 console iframes).

## Routing & Controllers
- `AuthenticationController` (`/authentication`):
  - `GET /authentication/login` -> view `authentication/login`
  - `GET /authentication/register` -> view `authentication/register`
  - `POST /authentication/register` -> redirects to `/authentication/login`
- `HomeController`:
  - `GET /` -> view `index`
  - `GET /dashboard` -> view `blank`

## Tests (Playwright)
- `src/test/java/com/allensandiego/ai/AiApplicationTests.java`:
  - `@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)`, `@UsePlaywright`
  - Headless Chromium launched via Playwright in `@BeforeAll`, closed in `@AfterAll`.
  - Screenshots output path: `target/screenshots/`
  - Test cases:
    - `shouldShowLogin`: Navigates to `/authentication/login`, waits 3s, captures `login.png`.
    - `shouldRegister`: Navigates to `/authentication/register` (`register.png`), fills registration fields (`register-filled.png`), submits form, waits 5s (`register-after.png`), fills login form (`login-filled.png`), submits form, captures final view (`login-after.png`).
