# Spring Boot + Thymeleaf Constitution (Non-Negotiable)

## Scope
- These rules govern new code and code changed for a feature; they do not imply that the starter scaffold already satisfies every rule.
- Current scaffold exceptions are listed below. Do not copy these exceptions into new implementations.
- When a feature touches an excepted area, follow the applicable rules here. If compliance requires broad migration work or a new dependency, clarify the scope and ask before adding the dependency.

## Current Scaffold Exceptions
- Database initialization currently uses `schema.sql` and `data.sql` with `spring.jpa.hibernate.ddl-auto=create-drop`; Flyway/Liquibase migrations are not configured.
- `spring.jpa.open-in-view` is not explicitly disabled.
- Error templates currently live in `templates/error-pages/`; a `@ControllerAdvice` error handler is not present.
- The current test suite contains Playwright smoke tests; it does not yet include `@WebMvcTest` coverage or service unit tests.

## 1. Architecture & MVC Layering
- **Strict Layering:** Web Controllers (`@Controller`) may only call Services (`@Service`). Never inject Repositories directly into Controllers.
- **View Models / Form DTOs:** Never bind JPA `@Entity` classes directly to Thymeleaf forms (`th:object`). Always use dedicated Form DTOs with Bean Validation (`@Valid`, `@NotBlank`, etc.).
- **Post-Redirect-Get (PRG):** All form submissions (`@PostMapping`) must redirect (`redirect:/...`) on success to prevent duplicate form submissions. Use `RedirectAttributes` for flash messages.
- **Constructor Injection Only:** Always use constructor injection (`private final` fields). Never use `@Autowired` field injection.

## 2. Thymeleaf & Frontend Rules
- **XSS Prevention:** Always use `th:text` for rendering user-supplied data. Never use `th:utext` unless the HTML has been explicitly sanitized on the server.
- **CSRF Protection:** Never disable Spring Security CSRF protection. Ensure all HTML `<form>` tags use `th:action` so CSRF tokens are automatically injected.
- **Layout & Fragments:** Reuse shared UI elements (headers, navbars, footers, alerts) via Thymeleaf fragments (`th:replace` / `th:insert`) instead of duplicating HTML across pages.
- **No Business Logic in Templates:** Keep Thymeleaf expressions simple. Compute complex conditions, formatting, or aggregations in Java DTOs/Services rather than inside `th:if` blocks.

## 3. Database & Transactions
- **Schema Migrations:** Never use `spring.jpa.hibernate.ddl-auto=update` or `create`. All schema changes must go through Flyway or Liquibase migrations.
- **Transaction Boundaries:** Place `@Transactional` on Service methods only. Mark read-only queries with `@Transactional(readOnly = true)`.
- **Open-In-View:** Keep `spring.jpa.open-in-view=false` and fetch all required data in the Service layer before passing view models to Thymeleaf.

## 4. Error Handling & Testing
- **Custom Error Pages:** Handle exceptions via `@ControllerAdvice` returning user-friendly error views (`error/404`, `error/500`) rather than raw stack traces.
- **Mandatory Tests:** Every new controller endpoint requires `@WebMvcTest` tests (verifying view names, model attributes, validation errors, and redirects), alongside Service unit tests.
- **Pre-Completion Check:** Run the full test suite before marking any task complete.

## 5. Agent Boundaries
- **Ask First:** Always ask before adding new dependencies (`pom.xml` / `build.gradle`), changing Spring Security rules, or introducing external JS/CSS frameworks.
