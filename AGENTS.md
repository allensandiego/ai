# AGENTS.md

## Project overview

This is a Java 21 Spring Boot application built with Maven. It uses Spring MVC,
Thymeleaf, Spring Security, Spring Data JPA, and H2.

## Tech stack

- **Language and build:** Java 21, Maven.
- **Backend:** Spring Boot 4.1.1, Spring MVC, Spring Security, Spring Data JPA,
  and Hibernate.
- **Database:** H2.
- **Server-rendered UI:** Thymeleaf with Spring Security integration.
- **Frontend assets:** CoreUI 5.6.1, CSS, and JavaScript; dashboard charts use
  Chart.js.
- **Testing:** Spring Boot test starters and Playwright 1.63.0.
- **Development support:** Spring Boot DevTools and Lombok.

## Project layout

```text
src/                              # Application source and tests
├── main/                         # Production application
│   ├── java/                     # Java source code
│   │   └── com/                  # Java package namespace
│   │       └── allensandiego/    # Organization package
│   │           └── ai/           # Application package and entry point
│   │               ├── config/   # Spring and security configuration
│   │               ├── controller/ # Web request handlers
│   │               ├── dto/      # Data transfer objects
│   │               ├── model/    # Domain and persistence models
│   │               ├── repository/ # Data access interfaces
│   │               └── service/  # Application business logic
│   └── resources/                # Runtime configuration and web resources
│       ├── static/               # Public static assets
│       │   ├── css/              # Stylesheets
│       │   │   └── vendors/      # Third-party stylesheets
│       │   └── js/               # Browser-side JavaScript
│       └── templates/            # Thymeleaf page templates
│           └── authentication/   # Authentication-related pages
└── test/                         # Test source code and resources
    └── java/                     # Java tests
        └── com/                  # Test Java package namespace
            └── allensandiego/    # Test organization package
                └── ai/           # Application tests
```

## Build and test

Use the Maven wrapper from the repository root:

- `./mvnw test` runs the test suite.
- `./mvnw package` builds the application.
- `./mvnw spring-boot:run` runs the application.

Build output and generated screenshots belong under `target/`.

## Development guidelines

- Follow the existing Java package layout and Spring MVC conventions.
- Keep tests alongside the existing test package and add or update tests for
  behavior changes.
- Check `application.properties`, `schema.sql`, and `data.sql` before changing
  database behavior.
- Keep templates, static assets, configuration, and Java code in their
  corresponding existing directories.
