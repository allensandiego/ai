<!--
Sync Impact Report
- Version change: (none) → 1.0.0 (initial ratification of scaffold)
- Modified principles: none (scaffold was unpopulated)
- Added sections:
  - Core Principles (I. Secure by Default, II. Convention-Aligned Architecture,
    III. Data Integrity and Schema Care, IV. Usable and Accessible
    Server-Rendered UI, V. Relevant, Repeatable Testing, VI. Focused,
    Maintainable Changes)
  - Technology Baseline
  - Quality Gates
  - Governance
- Removed sections: none
- Deferred items:
  - TODO(RATIFICATION_DATE): original adoption date unknown; pending
    maintainer approval. Update the Version line once confirmed.
-->

# AI (com.allensandiego:ai) Constitution

## Core Principles

### I. Secure by Default (NON-NEGOTIABLE)

- Spring Security MUST remain enabled for all HTTP endpoints; deny-by-default
  access rules are the baseline and any permit-all rule MUST be justified in
  code review and scoped to the narrowest path set.
- Authentication and authorization MUST be enforced server-side (Spring
  Security configuration plus method-level security where rules apply);
  client-side checks are convenience only and never a control.
- User input crossing a trust boundary MUST be validated server-side
  (Bean Validation or explicit validation in controllers/services) before
  use in logic, queries, or templates.
- Secrets, credentials, and other sensitive data MUST NOT be hardcoded or
  committed; they are supplied via configuration/environment and MUST NOT
  be logged, rendered into templates, or echoed in error messages.
- Passwords or other credentials, if stored, MUST be hashed with a supported
  algorithm via Spring Security's password encoders — never stored in plain
  text.

Rationale: this application handles authentication and user-facing data;
secure defaults prevent the most common classes of vulnerability without
depending on individual feature discipline.

### II. Convention-Aligned Architecture

- Code MUST live in the existing package layout under
  `com.allensandiego.ai` (`config`, `controller`, `dto`, `model`,
  `repository`, `service`) and follow the Spring MVC layering already in
  use: controllers handle HTTP concerns, services hold business logic,
  repositories own data access.
- New layers, packages, or abstractions MUST be justified by a concrete
  need present in the change; empty or speculative layers MUST NOT be
  created "for future use" (YAGNI).
- Server-rendered pages MUST use the existing Thymeleaf template
  conventions; new client-side frameworks MUST NOT be introduced without
  an approved baseline change (see Technology Baseline).
- Cross-cutting behavior (security, configuration) MUST use Spring's
  idioms (configuration classes, dependency injection) rather than
  ad-hoc static utilities or manual wiring.

Rationale: the project is a single Spring Boot application with a stable,
conventional structure; adhering to it keeps changes reviewable and
preserves the framework's safety nets.

### III. Data Integrity and Schema Care

- All schema changes MUST go through `schema.sql` and data seeding or
  migrations through `data.sql`, and MUST be reviewed together with any
  entity, repository, or query code that depends on them; the application
  MUST start cleanly against the updated scripts.
- Schema changes MUST be backward-compatible with existing data within a
  release unless an explicit, documented migration step is included;
  destructive changes (dropping columns/tables, narrowing types) MUST
  state the migration or recovery plan.
- Entity mappings MUST match the actual schema (column names, types,
  constraints, and relationships); schema and ORM drift MUST be fixed,
  not papered over with permissive mappings.
- Transactions MUST be used where an operation spans multiple writes or
  must be atomic; partial-update states MUST NOT be observable.

Rationale: H2 is embedded and changes are easy to lose or to break
silently; keeping schema, data scripts, and ORM mappings in lockstep
protects data integrity and repeatable startup.

### IV. Usable and Accessible Server-Rendered UI

- Every Thymeleaf page MUST render meaningful content without JavaScript
  where feasible; JavaScript (including Chart.js) enhances, but is not
  required for, core information and flows.
- Pages MUST use semantic HTML structure and provide labels for inputs,
  alt text for informative images, and sufficient heading hierarchy;
  forms MUST surface validation errors adjacent to the relevant fields.
- Page templates MUST reuse the existing CoreUI layout, CSS, and static
  asset conventions; duplicated markup or vendored asset variants MUST
  NOT be introduced without justification.
- User-facing flows (login, errors, empty states, confirmations) MUST
  provide clear feedback; raw stack traces or internal identifiers MUST
  NOT be shown to end users.

Rationale: the product surface is server-rendered HTML; accessibility and
usability discipline at the template level is the cheapest place to
enforce them.

### V. Relevant, Repeatable Testing

- Behavior changes MUST be covered by automated tests: unit tests for
  service logic, Spring Boot tests for controller/security/repository
  integration, and Playwright tests for critical user-facing flows
  (at minimum authentication).
- Tests MUST be deterministic and independent: no shared mutable state
  between tests, no reliance on external network services, and the H2
  test database MUST be reset or isolated per test as needed for
  repeatability.
- Bug fixes MUST include a regression test that fails without the fix.
- Tests MUST run via `./mvnw test` and MUST pass before a change is
  considered complete; skipped or `@Disabled` tests MUST be justified.

Rationale: Spring Security, JPA mappings, and template rendering fail in
integration, not in isolation; the existing test stack is the cheapest
reliable net.

### VI. Focused, Maintainable Changes

- Changes MUST be scoped to a single concern; unrelated refactoring MUST
  be split into separate changes with their own justification.
- New dependencies MUST be justified against the existing stack (Spring
  Boot BOM-managed versions first), and MUST be recorded in the
  Technology Baseline section when adopted.
- Complexity MUST be justified: a simpler solution that meets the
  requirement is preferred over a more general or abstract one.
- Public surfaces (controller routes, DTO shapes consumed by templates,
  schema objects) MUST evolve compatibly or with a documented migration;
  silent breaking changes are not allowed.

Rationale: a small application's main long-term risk is accretion of
unjustified structure and dependencies; disciplined scope keeps the
codebase legible.

## Technology Baseline

The current, ratified technology baseline is:

- Language and build: Java 21, Maven (via `./mvnw`), Spring Boot 4.1.1
  parent.
- Backend: Spring MVC, Spring Security, Spring Data JPA with Hibernate.
- Database: H2 (embedded, initialized via `schema.sql` / `data.sql`).
- UI: Thymeleaf server-rendered templates with Spring Security
  integration; CoreUI assets and Chart.js for dashboard rendering.
- Testing: Spring Boot test starters (unit/integration) and Playwright
  for end-to-end browser tests.

Changes to this baseline (new frameworks, new databases, new build tools,
major version upgrades of listed components) MUST include:

1. A written justification covering the problem the change solves and why
   the current stack is insufficient.
2. A compatibility review covering Spring Boot/Spring Security/JPA
   interaction, template rendering, schema/data scripts, and the test
   stack.
3. An amendment to this section in the same change that introduces the
   baseline change.

Minor dependency updates within a baseline component (patch/minor
versions resolved by the Spring Boot BOM or Maven) do not require an
amendment but MUST still pass the full test suite.

## Quality Gates

Every change MUST satisfy all of the following before merge:

1. **Build and run**: `./mvnw package && ./mvnw spring-boot:run` completes without errors.
2. **Tests**: `./mvnw test` passes with no unexplained skips; new behavior
   has matching test coverage per Principle V.
3. **Security review**: no new secrets committed; access-control and
   validation changes reviewed against Principle I; new endpoints have an
   explicit authorization decision.
4. **Data review**: any change to `schema.sql`, `data.sql`, or entity
   mappings is reviewed together against Principle III.
5. **UI review**: template changes render correctly, reuse existing
   layouts/assets, and meet Principle IV accessibility expectations.
6. **Scope review**: the change is limited per Principle VI; dependency
   additions include justification.

Automated gates (build and tests) MUST pass in CI or locally before
review; human-review gates (3–6) are checked by the reviewer as part of
change approval.

## Governance

- This constitution is the root governance document for this repository;
  it supersedes informal conventions where conflicts arise.
- **Amendments**: any change to principles, the technology baseline, or
  quality gates MUST be proposed as a constitution change with a stated
  rationale, impact on existing work, and (for baseline changes) the
  compatibility review required above. Amendments require maintainer
  approval and MUST update the version line.
- **Versioning**: this document uses semantic versioning.
  - MAJOR: removal or backward-incompatible redefinition of a principle
    or governance rule.
  - MINOR: new principle/section or materially expanded guidance.
  - PATCH: clarifications, wording, and typo fixes with no change to
    requirements.
- **Exceptions**: deviations from a principle MUST be explicitly
  documented in the change that requires them, with rationale and scope;
  silent or permanent exceptions are not allowed. Repeated exceptions to
  the same principle signal a needed amendment.
- **Compliance**: authors and reviewers verify conformance at the Quality
  Gates; review comments MUST cite the relevant principle when flagging
  violations.
- **Runtime guidance**: day-to-day development conventions (commands,
  layout) live in `AGENTS.md` and MUST NOT contradict this constitution.

**Version**: 1.0.0 | **Ratified**: TODO(RATIFICATION_DATE): original
adoption date unknown; pending maintainer approval | **Last Amended**:
2026-10-16
