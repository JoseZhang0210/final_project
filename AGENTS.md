# Repository Guidelines

## Project Structure & Module Organization

This is a Java 21 Spring Boot hotel-management application, packaged as a WAR. Application code lives in `src/main/java/com/hotel`, organized by responsibility: `controller` exposes HTTP endpoints, `service` contains business logic, `repository` contains Spring Data JPA access, `entity` maps database records, `dto` defines API payloads, and `config` holds application and security configuration. Static browser assets are under `src/main/resources/static` (`js/api.js`, `css/style.css`, and feature pages); legacy server templates remain in `resources/templates`. Put tests in the matching package beneath `src/test/java`. Database schema material belongs in `sql/`; diagrams and plans belong in `doc/`.

## Build, Test, and Development Commands

Use the Maven Wrapper so the project Maven version is consistent:

- `./mvnw.cmd spring-boot:run`: run locally on Windows with development reload support.
- `./mvnw.cmd test`: compile and execute the JUnit test suite.
- `./mvnw.cmd clean package`: create the deployable WAR in `target/`.
- `./mvnw.cmd clean`: remove generated build output before a clean build.

Ensure a JDK 21 installation is selected before running these commands. The runtime database driver is Microsoft SQL Server; keep local connection settings in `src/main/resources/application.properties` suitable for your environment and never commit credentials.

## Coding Style & Naming Conventions

Follow the existing Java style: tabs for indentation, one public type per file, and package names in lowercase. Use PascalCase for classes (`RoomTypeService`), camelCase for methods and fields (`findById`), and descriptive REST/resource names. Keep layers separated: controllers delegate to services, and services call repositories. Prefer the established Lombok annotations over hand-written boilerplate when extending entities. No formatter or linter is configured, so match adjacent code and keep imports tidy.

## Testing Guidelines

Tests use JUnit Jupiter through Spring Boot test starters. Name test classes `*Tests` and test methods for the behavior being verified, e.g. `createsBookingWhenRoomIsAvailable()`. Add focused tests alongside changes to controllers, services, or repositories; run `./mvnw.cmd test` before opening a pull request. There is no configured coverage threshold; prioritize new paths, validation, and authorization behavior.

## Commit & Pull Request Guidelines

Recent history uses short, imperative Chinese summaries and occasional merge commits. Keep commits scoped and describe the changed feature or fix; avoid unrelated formatting. Pull requests should state the user-facing change, affected API/UI/database areas, and verification command. Link related issues when available, and include screenshots for changes under `resources/static`.
