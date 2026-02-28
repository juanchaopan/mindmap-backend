# CLAUDE.md

## Stack

- **Language:** Kotlin / Java 21
- **Framework:** Spring Boot 4.0.3
- **Build:** Gradle Kotlin DSL (`build.gradle.kts`)
- **DB:** PostgreSQL (`localhost:5432/mindmap`, user `postgres`, pass `postgres`)
- **Auth:** OAuth2 Resource Server — validates JWTs from Keycloak (`localhost:8080/realms/demo`)
- **Test:** JUnit 5 + Mockito-Kotlin 5.4.0

## Commands

```bash
./gradlew bootRun        # starts on port 8081
./gradlew test           # run all tests
./gradlew clean build
```

## Architecture

Package root: `com.p8499.mindmap`

```
DemoApplication.kt       — entry point
SecurityConfig.kt        — SecurityFilterChain; JWT validation; /public/** open, all else requires auth
JacksonConfig.kt         — ObjectMapper bean: JavaTimeModule, ISO-8601 dates (no timestamps)
DemoController.kt        — GET /public/hello, GET /api/me

workspace/
  Workspace.kt           — JPA entity: id (UUID), name, owner, createdAt, updatedAt (OffsetDateTime)
  WorkspaceRepository.kt — JpaRepository; findAllByOwner, findByIdAndOwner
  WorkspaceService.kt    — CRUD; all ops scoped to owner; 404 on missing/unowned
  WorkspaceController.kt — REST /api/workspaces; owner always from jwt.subject
```

## Key Patterns

- Owner is always sourced from `jwt.subject` — never from the request body
- `kotlin("plugin.jpa")` generates the no-arg constructor required by Hibernate
- `spring.jpa.hibernate.ddl-auto=update` — Hibernate auto-creates/migrates tables
- Controller tests use `MockMvcBuilders.standaloneSetup` + `SecurityContextHolder` (no Spring context; `@WebMvcTest` is broken in Spring Boot 4)
- Jackson configured via `ObjectMapper` `@Bean` directly (`Jackson2ObjectMapperBuilderCustomizer` removed in Spring Boot 4)
