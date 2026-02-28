# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Stack

- **Language:** Kotlin on Java 21
- **Framework:** Spring Boot 4.0.3
- **Build:** Gradle with Kotlin DSL (`build.gradle.kts`)
- **Test:** JUnit 5 via `spring-boot-starter-test`

## Commands

```bash
# Build
./gradlew build

# Run the application (starts on port 8081)
./gradlew bootRun

# Run all tests
./gradlew test

# Run a single test class
./gradlew test --tests "com.example.demo.DemoApplicationTests"

# Clean build
./gradlew clean build
```

## Architecture

This is a **Spring Boot OAuth2 Resource Server** — it validates JWTs issued by Keycloak and protects REST endpoints. No token issuance happens here.

**Key files:**
- `DemoApplication.kt` — entry point
- `SecurityConfig.kt` — `SecurityFilterChain` bean; defines authorization rules and enables JWT validation
- `DemoController.kt` — REST endpoints
- `application.properties` — issuer URI and server port

**Security rules (SecurityConfig.kt):**
- `GET /public/**` — open, no authentication required
- All other routes — require a valid Bearer JWT

**Endpoints (DemoController.kt):**
- `GET /public/hello` → `{"message": "Hello, world!"}`
- `GET /api/me` → `{"sub": "...", "email": "..."}` extracted from the JWT principal

**JWT validation flow:**
Spring Boot auto-discovers Keycloak's JWKS endpoint at startup via:
```
http://localhost:8080/realms/demo/.well-known/openid-configuration
```
Keycloak must be running on port 8080 before the app starts (the app itself runs on 8081).

## Kotlin Compiler Flags

Two flags are active in `build.gradle.kts`:
- `-Xjsr305=strict` — enforces nullability annotations from Java libraries
- `-Xannotation-default-target=param-property` — Kotlin 2.x default for annotation targets on constructor parameters (relevant for Spring/Jackson annotations)
