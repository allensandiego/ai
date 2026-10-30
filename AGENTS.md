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
    │       ├── static/        # assets/css/js/vendors
    │       └── templates/     # Thymeleaf views (auth, components, forms, etc.)
    └── test/
        └── java/com/allensandiego/ai/
            └── AiApplicationTests.java  # Playwright integration tests
```

## Overview
- Spring Boot 4.1.1 (Java 21), Maven project (`pom.xml`).
- Package: `com.allensandiego.ai`. Main: `AiApplication`.
- Web stack: Spring MVC, Thymeleaf, Spring Security, Spring Data JPA, Validation, Actuator. DB: H2 (in-memory).
- Static/Views: `src/main/resources/templates/`, `src/main/resources/static/` (css/js/vendors/assets).
- Dev-only: Spring Boot DevTools; H2 console enabled.

## Build/Test Commands
- Build: `mvn compile`
- Test: `mvn test`
- Run app: `mvn spring-boot:run`
- Package: `mvn package`

## App Config (key)
- `src/main/resources/application.properties`:
  - H2 mem: `jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1` (user sa, no pass)
  - JPA: `ddl-auto=create-drop`, `show-sql=true`, `format_sql=true`, dialect H2
  - H2 console: enabled

## Security (important)
- `config/SecurityConfig.java`:
  - Permits: `/h2-console/**`, `/authentication/**`, `/public/**`, `/css/**`, `/js/**`, `/vendors/**`
  - Form login: `loginPage=/authentication/login`, `successForwardUrl=/dashboard`
  - Logout: `/authentication/logout`
  - Headers: frameOptions `sameOrigin` (for H2 console)

## Routing
- `AuthenticationController` (`/authentication`): GET `/login` -> `authentication/login`, GET `/register` -> `authentication/register`
- `HomeController`: GET `/` -> `index`, GET `/dashboard` -> `blank`

## Tests (Playwright)
- `src/test/java/com/allensandiego/ai/AiApplicationTests.java`:
  - `@SpringBootTest(webEnvironment = RANDOM_PORT)`, `@UsePlaywright`
  - Launches headless Chromium via Playwright; screenshots saved to `target/screenshots/`
  - Tests: `shouldRegister` (register flow + login after), `shouldShowLogin`
  - Note sleeps (3s/5s) used in tests;
