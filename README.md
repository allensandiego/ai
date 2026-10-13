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

Resolve the repository root with `git rev-parse --show-toplevel` from the active
workspace; do not assume a machine-specific path. Repository-wide agent
boundaries are defined in `AGENTS.md`.

The current application layout is:

```text
<repository-root>/
├── AGENTS.md                 # Repository-wide agent instructions
├── .opencode/commands/       # Repository slash-command definitions
├── pom.xml                   # Maven configuration and dependencies
├── mvnw                      # Maven wrapper
└── src/
    ├── main/
    │   ├── java/com/allensandiego/ai/
    │   │   ├── AiApplication.java
    │   │   ├── config/
    │   │   └── controller/
    │   └── resources/
    │       ├── application.properties
    │       ├── schema.sql
    │       ├── data.sql
    │       ├── static/
    │       └── templates/
    └── test/java/com/allensandiego/ai/
```

This tree describes the current checkout, not a requirement to create every
listed or anticipated layer. Inspect the source, configuration, tests, and
`schema.sql` before changing them. Add new code and directories only as needed,
following the existing Java package and Maven layout. Keep generated files,
including Playwright screenshots, inside the repository (prefer `target/`).

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
/speckit.constitution

Establish the principles for building and maintaining this application:

- Use Java 21 and Spring Boot 4.1.1. Keep dependencies and configuration compatible with those versions; do not silently downgrade or substitute the requested stack.
- Build a server-rendered web application with Thymeleaf. Use CoreUI Admin components styled with Bootstrap 5, and HTMX for focused interactions and partial-page updates. Preserve accessible, usable behavior without JavaScript where practical.
- Use Spring Security with JDBC-backed authentication. Rely on Spring Security’s established authentication and password-encoding mechanisms; do not create custom authentication or store plaintext passwords. Preserve CSRF protection, including for HTMX requests.
- Use H2 only for local development and local tests. Keep database configuration profile-based and externalizable so non-local environments can supply their own database configuration. Do not treat H2 as the production database.
- Use the repository’s existing structure, conventions, and `schema.sql` data model when present. Inspect them before changing persistence or inventing new entities and tables. Keep schema, JDBC access, services, security, and UI behavior consistent. If required project structure or schema is absent, establish only what the requested feature needs.
- Treat the active Git repository root as the project boundary. Resolve its absolute path from the workspace/Git metadata; do not hard-code a machine-specific path or access sibling projects and unrelated directories. Follow the existing layout documented above.
- Keep clear boundaries between controllers, services, persistence, security, and templates. Prefer simple, explicit code and established Spring patterns over unnecessary abstractions or custom frameworks.
- Make the application reproducibly buildable and runnable. Add or update focused tests for meaningful behavior and run the relevant build and test checks after changes. Do not hide errors behind silent fallbacks.
- Use Java Playwright for end-to-end browser tests. Verify key user flows against the running application, and capture screenshots under `target/playwright/screenshots/` with predictable names. Keep tests reliable and avoid dependence on external services.
- Protect user data and credentials. Do not commit secrets, default production credentials, or environment-specific sensitive configuration.
- Update documentation when setup, configuration, database behavior, or test workflows change. Document the commands needed to run locally, test, and produce Playwright screenshots.
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
