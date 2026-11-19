# AGENTS.md

## Project Structure

```text
/home/allen/workspace/ai
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

## Dependencies/Build/Test Commands
- Dependency: `mvn dependency:tree`
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

## Dependency Tree

[INFO] ------------------------< com.allensandiego:ai >------------------------
[INFO] Building ai 0.0.1-SNAPSHOT
[INFO]   from pom.xml
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] --- dependency:3.10.0:tree (default-cli) @ ai ---
[INFO] Loaded 64 auto-discovered prefixes for remote repository apache.snapshots (prefixes-apache.snapshots.txt)
[INFO] com.allensandiego:ai:jar:0.0.1-SNAPSHOT
[INFO] +- org.springframework.boot:spring-boot-h2console:jar:4.1.1:compile
[INFO] |  \- org.springframework.boot:spring-boot:jar:4.1.1:compile
[INFO] |     +- org.springframework:spring-core:jar:7.0.9:compile
[INFO] |     |  \- commons-logging:commons-logging:jar:1.3.6:compile
[INFO] |     \- org.springframework:spring-context:jar:7.0.9:compile
[INFO] |        \- org.springframework:spring-expression:jar:7.0.9:compile
[INFO] +- org.springframework.boot:spring-boot-starter-actuator:jar:4.1.1:compile
[INFO] |  +- org.springframework.boot:spring-boot-starter:jar:4.1.1:compile
[INFO] |  |  +- org.springframework.boot:spring-boot-starter-logging:jar:4.1.1:compile
[INFO] |  |  |  +- ch.qos.logback:logback-classic:jar:1.5.38:compile
[INFO] |  |  |  |  \- ch.qos.logback:logback-core:jar:1.5.38:compile
[INFO] |  |  |  +- org.apache.logging.log4j:log4j-to-slf4j:jar:2.25.5:compile
[INFO] |  |  |  |  \- org.apache.logging.log4j:log4j-api:jar:2.25.5:compile
[INFO] |  |  |  \- org.slf4j:jul-to-slf4j:jar:2.0.18:compile
[INFO] |  |  +- jakarta.annotation:jakarta.annotation-api:jar:3.0.0:compile
[INFO] |  |  \- org.yaml:snakeyaml:jar:2.6:compile
[INFO] |  +- org.springframework.boot:spring-boot-starter-micrometer-metrics:jar:4.1.1:compile
[INFO] |  |  \- org.springframework.boot:spring-boot-micrometer-metrics:jar:4.1.1:compile
[INFO] |  |     \- org.springframework.boot:spring-boot-micrometer-observation:jar:4.1.1:compile
[INFO] |  +- org.springframework.boot:spring-boot-actuator-autoconfigure:jar:4.1.1:compile
[INFO] |  |  \- org.springframework.boot:spring-boot-actuator:jar:4.1.1:compile
[INFO] |  +- org.springframework.boot:spring-boot-health:jar:4.1.1:compile
[INFO] |  +- io.micrometer:micrometer-observation:jar:1.17.1:compile
[INFO] |  |  \- io.micrometer:micrometer-commons:jar:1.17.1:compile
[INFO] |  \- io.micrometer:micrometer-jakarta9:jar:1.17.1:compile
[INFO] |     \- io.micrometer:micrometer-core:jar:1.17.1:compile
[INFO] |        \- org.hdrhistogram:HdrHistogram:jar:2.2.2:runtime
[INFO] +- org.springframework.boot:spring-boot-starter-data-jpa:jar:4.1.1:compile
[INFO] |  +- org.springframework.boot:spring-boot-starter-jdbc:jar:4.1.1:compile
[INFO] |  |  \- com.zaxxer:HikariCP:jar:7.0.2:compile
[INFO] |  +- org.springframework.boot:spring-boot-data-jpa:jar:4.1.1:compile
[INFO] |  |  +- org.springframework.boot:spring-boot-data-commons:jar:4.1.1:compile
[INFO] |  |  |  +- org.springframework.boot:spring-boot-persistence:jar:4.1.1:compile
[INFO] |  |  |  \- org.springframework.data:spring-data-commons:jar:4.1.1:compile
[INFO] |  |  +- org.springframework.boot:spring-boot-hibernate:jar:4.1.1:compile
[INFO] |  |  |  +- org.springframework.boot:spring-boot-jpa:jar:4.1.1:compile
[INFO] |  |  |  |  \- jakarta.persistence:jakarta.persistence-api:jar:3.2.0:compile
[INFO] |  |  |  +- org.hibernate.orm:hibernate-core:jar:7.4.5.Final:compile
[INFO] |  |  |  |  +- jakarta.transaction:jakarta.transaction-api:jar:2.0.1:compile
[INFO] |  |  |  |  +- org.hibernate.models:hibernate-models:jar:1.1.1:runtime
[INFO] |  |  |  |  +- org.glassfish.jaxb:jaxb-runtime:jar:4.0.9:runtime
[INFO] |  |  |  |  |  \- org.glassfish.jaxb:jaxb-core:jar:4.0.9:runtime
[INFO] |  |  |  |  |     +- org.eclipse.angus:angus-activation:jar:2.0.3:runtime
[INFO] |  |  |  |  |     +- org.glassfish.jaxb:txw2:jar:4.0.9:runtime
[INFO] |  |  |  |  |     \- com.sun.istack:istack-commons-runtime:jar:4.1.2:runtime
[INFO] |  |  |  |  \- jakarta.inject:jakarta.inject-api:jar:2.0.1:runtime
[INFO] |  |  |  \- org.springframework:spring-orm:jar:7.0.9:compile
[INFO] |  |  +- org.springframework.data:spring-data-jpa:jar:4.1.1:compile
[INFO] |  |  |  +- org.springframework:spring-tx:jar:7.0.9:compile
[INFO] |  |  |  \- org.antlr:antlr4-runtime:jar:4.13.2:compile
[INFO] |  |  \- org.springframework:spring-aspects:jar:7.0.9:compile
[INFO] |  |     \- org.aspectj:aspectjweaver:jar:1.9.25.1:compile
[INFO] |  \- org.springframework.boot:spring-boot-jdbc:jar:4.1.1:compile
[INFO] |     +- org.springframework.boot:spring-boot-sql:jar:4.1.1:compile
[INFO] |     +- org.springframework.boot:spring-boot-transaction:jar:4.1.1:compile
[INFO] |     \- org.springframework:spring-jdbc:jar:7.0.9:compile
[INFO] +- org.springframework.boot:spring-boot-starter-security:jar:4.1.1:compile
[INFO] |  +- org.springframework.boot:spring-boot-security:jar:4.1.1:compile
[INFO] |  |  +- org.springframework.security:spring-security-config:jar:7.1.1:compile
[INFO] |  |  |  \- org.springframework.security:spring-security-core:jar:7.1.1:compile
[INFO] |  |  |     \- org.springframework.security:spring-security-crypto:jar:7.1.1:compile
[INFO] |  |  \- org.springframework.security:spring-security-web:jar:7.1.1:compile
[INFO] |  \- org.springframework:spring-aop:jar:7.0.9:compile
[INFO] |     \- org.springframework:spring-beans:jar:7.0.9:compile
[INFO] +- org.springframework.boot:spring-boot-starter-thymeleaf:jar:4.1.1:compile
[INFO] |  \- org.springframework.boot:spring-boot-thymeleaf:jar:4.1.1:compile
[INFO] |     \- org.springframework:spring-web:jar:7.0.9:compile
[INFO] +- org.springframework.boot:spring-boot-starter-validation:jar:4.1.1:compile
[INFO] |  \- org.springframework.boot:spring-boot-validation:jar:4.1.1:compile
[INFO] |     +- org.apache.tomcat.embed:tomcat-embed-el:jar:11.0.24:compile
[INFO] |     \- org.hibernate.validator:hibernate-validator:jar:9.1.3.Final:compile
[INFO] |        +- jakarta.validation:jakarta.validation-api:jar:3.1.1:compile
[INFO] |        +- org.jboss.logging:jboss-logging:jar:3.6.3.Final:compile
[INFO] |        \- com.fasterxml:classmate:jar:1.7.3:compile
[INFO] +- org.springframework.boot:spring-boot-starter-webmvc:jar:4.1.1:compile
[INFO] |  +- org.springframework.boot:spring-boot-starter-jackson:jar:4.1.1:compile
[INFO] |  |  \- org.springframework.boot:spring-boot-jackson:jar:4.1.1:compile
[INFO] |  |     \- tools.jackson.core:jackson-databind:jar:3.1.5:compile
[INFO] |  |        +- com.fasterxml.jackson.core:jackson-annotations:jar:2.21:compile
[INFO] |  |        \- tools.jackson.core:jackson-core:jar:3.1.5:compile
[INFO] |  +- org.springframework.boot:spring-boot-starter-tomcat:jar:4.1.1:compile
[INFO] |  |  +- org.springframework.boot:spring-boot-starter-tomcat-runtime:jar:4.1.1:compile
[INFO] |  |  |  +- org.apache.tomcat.embed:tomcat-embed-core:jar:11.0.24:compile
[INFO] |  |  |  \- org.apache.tomcat.embed:tomcat-embed-websocket:jar:11.0.24:compile
[INFO] |  |  \- org.springframework.boot:spring-boot-tomcat:jar:4.1.1:compile
[INFO] |  +- org.springframework.boot:spring-boot-http-converter:jar:4.1.1:compile
[INFO] |  \- org.springframework.boot:spring-boot-webmvc:jar:4.1.1:compile
[INFO] |     +- org.springframework.boot:spring-boot-servlet:jar:4.1.1:compile
[INFO] |     \- org.springframework:spring-webmvc:jar:7.0.9:compile
[INFO] +- org.thymeleaf.extras:thymeleaf-extras-springsecurity6:jar:3.1.5.RELEASE:compile
[INFO] |  +- org.thymeleaf:thymeleaf-spring6:jar:3.1.5.RELEASE:compile
[INFO] |  |  \- org.thymeleaf:thymeleaf:jar:3.1.5.RELEASE:compile
[INFO] |  |     +- org.attoparser:attoparser:jar:2.0.7.RELEASE:compile
[INFO] |  |     \- org.unbescape:unbescape:jar:1.1.6.RELEASE:compile
[INFO] |  \- org.slf4j:slf4j-api:jar:2.0.18:compile
[INFO] +- com.microsoft.playwright:playwright:jar:1.63.0:test
[INFO] |  +- com.google.code.gson:gson:jar:2.13.2:test
[INFO] |  |  \- com.google.errorprone:error_prone_annotations:jar:2.41.0:test
[INFO] |  +- org.jspecify:jspecify:jar:1.0.1:compile
[INFO] |  +- org.opentest4j:opentest4j:jar:1.3.0:test
[INFO] |  +- com.microsoft.playwright:driver:jar:1.63.0:test
[INFO] |  \- com.microsoft.playwright:driver-bundle:jar:1.63.0:test
[INFO] +- org.springframework.boot:spring-boot-devtools:jar:4.1.1:runtime (optional)
[INFO] |  \- org.springframework.boot:spring-boot-autoconfigure:jar:4.1.1:compile
[INFO] +- com.h2database:h2:jar:2.4.240:runtime
[INFO] +- org.projectlombok:lombok:jar:1.18.46:compile (optional)
[INFO] +- org.springframework.boot:spring-boot-starter-actuator-test:jar:4.1.1:test
[INFO] |  +- org.springframework.boot:spring-boot-starter-test:jar:4.1.1:test
[INFO] |  |  +- org.springframework.boot:spring-boot-test:jar:4.1.1:test
[INFO] |  |  +- org.springframework.boot:spring-boot-test-autoconfigure:jar:4.1.1:test
[INFO] |  |  +- com.jayway.jsonpath:json-path:jar:2.10.0:test
[INFO] |  |  +- jakarta.xml.bind:jakarta.xml.bind-api:jar:4.0.5:runtime
[INFO] |  |  |  \- jakarta.activation:jakarta.activation-api:jar:2.1.4:runtime
[INFO] |  |  +- net.minidev:json-smart:jar:2.6.0:test
[INFO] |  |  |  \- net.minidev:accessors-smart:jar:2.6.0:test
[INFO] |  |  |     \- org.ow2.asm:asm:jar:9.7.1:test
[INFO] |  |  +- org.assertj:assertj-core:jar:3.27.7:test
[INFO] |  |  |  \- net.bytebuddy:byte-buddy:jar:1.18.11:runtime
[INFO] |  |  +- org.awaitility:awaitility:jar:4.3.0:test
[INFO] |  |  +- org.hamcrest:hamcrest:jar:3.0:test
[INFO] |  |  +- org.junit.jupiter:junit-jupiter:jar:6.0.3:test
[INFO] |  |  |  +- org.junit.jupiter:junit-jupiter-api:jar:6.0.3:test
[INFO] |  |  |  |  +- org.junit.platform:junit-platform-commons:jar:6.0.3:test
[INFO] |  |  |  |  \- org.apiguardian:apiguardian-api:jar:1.1.2:test
[INFO] |  |  |  +- org.junit.jupiter:junit-jupiter-params:jar:6.0.3:test
[INFO] |  |  |  \- org.junit.jupiter:junit-jupiter-engine:jar:6.0.3:test
[INFO] |  |  |     \- org.junit.platform:junit-platform-engine:jar:6.0.3:test
[INFO] |  |  +- org.mockito:mockito-core:jar:5.23.0:test
[INFO] |  |  |  +- net.bytebuddy:byte-buddy-agent:jar:1.18.11:test
[INFO] |  |  |  \- org.objenesis:objenesis:jar:3.3:test
[INFO] |  |  +- org.mockito:mockito-junit-jupiter:jar:5.23.0:test
[INFO] |  |  +- org.skyscreamer:jsonassert:jar:1.5.3:test
[INFO] |  |  |  \- com.vaadin.external.google:android-json:jar:0.0.20131108.vaadin1:test
[INFO] |  |  +- org.springframework:spring-test:jar:7.0.9:test
[INFO] |  |  \- org.xmlunit:xmlunit-core:jar:2.11.0:test
[INFO] |  \- org.springframework.boot:spring-boot-starter-micrometer-metrics-test:jar:4.1.1:test
[INFO] |     \- org.springframework.boot:spring-boot-micrometer-metrics-test:jar:4.1.1:test
[INFO] |        \- io.micrometer:micrometer-observation-test:jar:1.17.1:test
[INFO] +- org.springframework.boot:spring-boot-starter-data-jpa-test:jar:4.1.1:test
[INFO] |  +- org.springframework.boot:spring-boot-starter-jdbc-test:jar:4.1.1:test
[INFO] |  |  \- org.springframework.boot:spring-boot-jdbc-test:jar:4.1.1:test
[INFO] |  \- org.springframework.boot:spring-boot-data-jpa-test:jar:4.1.1:test
[INFO] |     \- org.springframework.boot:spring-boot-jpa-test:jar:4.1.1:test
[INFO] +- org.springframework.boot:spring-boot-starter-security-test:jar:4.1.1:test
[INFO] |  \- org.springframework.boot:spring-boot-security-test:jar:4.1.1:test
[INFO] |     \- org.springframework.security:spring-security-test:jar:7.1.1:test
[INFO] +- org.springframework.boot:spring-boot-starter-thymeleaf-test:jar:4.1.1:test
[INFO] +- org.springframework.boot:spring-boot-starter-validation-test:jar:4.1.1:test
[INFO] \- org.springframework.boot:spring-boot-starter-webmvc-test:jar:4.1.1:test
[INFO]    +- org.springframework.boot:spring-boot-starter-jackson-test:jar:4.1.1:test
[INFO]    +- org.springframework.boot:spring-boot-webmvc-test:jar:4.1.1:test
[INFO]    |  \- org.springframework.boot:spring-boot-web-server:jar:4.1.1:compile
[INFO]    \- org.springframework.boot:spring-boot-resttestclient:jar:4.1.1:test
[INFO] ------------------------------------------------------------------------

## References

**Spring Security 7.X.X** https://docs.spring.io/spring-security/reference/api/java/index.html

**Spring Boot 4.1.1** https://docs.spring.io/spring-boot/api/java/index.html
