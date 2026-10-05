# Local AI Application Benchmark Template

This git repo serves as a template for benchmarking and testing the ability of local AI systems to fully implement or build a complete application from a realistic starting point.

The goal is not just to generate a few files or snippets, but to evaluate whether an agent can take a partially structured codebase, reason through the architecture, implement missing functionality, fix integration issues, and deliver a working application end-to-end.

## Why this repo exists

This repository is intentionally designed as a practical benchmark for local AI coding workflows. It combines:

- a Java 21 Spring Boot application
- persistence and security scaffolding
- a static dashboard-style frontend based on CoreUI
- a project layout that can be extended into a complete product

This makes it useful for testing whether local AI can:

- wire up dependencies and build configuration
- implement business logic and data models
- create controllers, services, and repositories
- connect frontend and backend flows
- run the app successfully
- troubleshoot errors and complete the project without outside scaffolding

## Tech stack

- Java 21
- Spring Boot 4
- Spring Web MVC
- Spring Data JPA
- Spring Security
- H2 database
- Maven
- CoreUI admin dashboard templates
- Playwright for browser automation testing

## Project structure

- `pom.xml` — Maven build configuration and dependencies
- `src/main/java` — application source code
- `src/main/resources` — configuration, templates, and static assets
- `src/test/java` — test suite and validation code
- `coreui/` — dashboard UI template assets

## Quick start

1. Clone the repository.
2. Ensure Java 21 is installed.
3. Run the application:

   ./mvnw spring-boot:run

4. Open the app in a browser at:

   http://localhost:8080

## GitHub Spec Kit commands

These are the slash commands used by Spec Kit in agent chat. In GitHub Copilot's default skills mode, the syntax is the hyphen form shown first. The reference docs also show the dot form, which is the canonical command notation used in the Spec Kit docs.

For the standard Spec-Driven Development flow in Copilot Skills mode:

- `/speckit-constitution` — establish project principles
- `/speckit-specify "feature name"` — define the product specification
- `/speckit-plan` — create the technical implementation plan
- `/speckit-tasks` — break the plan into executable tasks
- `/speckit-implement` — execute the implementation
- `/speckit-converge` — verify the result against the spec, plan, and tasks

The same commands in the canonical dot notation used by the Spec Kit docs are:

- `/speckit.constitution`
- `/speckit.specify "feature name"`
- `/speckit.plan`
- `/speckit.tasks`
- `/speckit.implement`
- `/speckit.converge`

Additional commands used in other Spec Kit workflows:

- `/speckit.clarify` — resolve ambiguity before planning
- `/speckit.checklist` — validate requirement quality
- `/speckit.analyze` — review consistency across spec, plan, and tasks
- `/speckit-bug-assess "bug description"` — assess and diagnose a bug
- `/speckit-bug-fix` — implement the bug fix
- `/speckit-bug-test` — validate the bug fix
- `/speckit-assess-intake` — start an idea assessment
- `/speckit-assess-research` — research the idea
- `/speckit-assess-define` — define the assessment
- `/speckit-assess-shape` — shape the recommendation
- `/speckit-assess-decide` — deliver a go / clarify / kill decision

The default GitHub Copilot skills pattern is: `/speckit-...`; the reference docs generally display `/speckit....` as the canonical command notation.

## Recommended constitution command

For this project, the recommended constitution command is:

```text
/speckit.constitution Build a working Spring Boot 4.1.1 application using Java 21, Thymeleaf, CoreUI Bootstrap 5, H2, JDBC authentication, and Java Playwright E2E testing. Prioritize correctness, buildability, and end-to-end functionality. Work from the actual project structure and schema.sql data model instead of inventing unrelated patterns. Keep the backend, persistence, security, and frontend integration consistent, predictable, and runnable. Prefer explicit service boundaries, schema-compatible database changes, and maintainable integration between controllers, templates, and data access. Validate every implementation with real build, runtime, and E2E evidence before claiming completion. If the repo is intentionally incomplete or scaffolded, fill the gaps without breaking the application or introducing hidden assumptions.
```

For this project, the recommended specify command is:

```text
/speckit.specify Build a login, registration, and password reset flow for the Spring Boot app using the existing authentication templates and H2 schema. Use the `users` table for JDBC authentication, keep the flow simple without email verification, and validate the pages with Playwright browser tests.
```

For this project, the recommended plan command is:

```text
/speckit.plan Implement a working Spring JDBC authentication flow for the app using the existing CoreUI authentication templates and the H2 schema. Create login, registration, and password reset pages that match the current project structure, wire in the required controllers and service logic, and validate the flow with Playwright E2E tests that capture screenshots. Ensure the application builds and runs cleanly without introducing unrelated features.
```

## Benchmark use case

This repo is meant to be used as a starting template for evaluating whether a local AI can complete a full-stack implementation. A benchmark run may include tasks such as:

- building out the missing domain model and data layer
- creating a usable authentication flow
- implementing CRUD screens and endpoints
- integrating frontend templates with Java controllers
- ensuring the project builds and runs cleanly
- fixing runtime and integration issues discovered during execution

## Success criteria

A successful local AI run should result in:

- a clean and working build
- a complete, coherent application flow
- functionality beyond the initial scaffold
- a project that is demonstrably runnable and testable
- evidence that the AI can handle real implementation work end-to-end

## Notes

This repository is a benchmark template rather than a finished product by itself. It is intended to provide the structure and tooling needed to measure how effectively local AI systems can turn a project from starter state into a functional application.
