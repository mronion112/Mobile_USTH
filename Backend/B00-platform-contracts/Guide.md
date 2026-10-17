# B00 — Platform, conventions and API contracts

**Owner:** Backend lead + CI owner  
**Blocks:** every other Backend feature.

## Responsibility

- Initialize one Java 21 Spring Boot REST module; choose package name once.
- Add Web, Validation, Data JPA, Flyway, test and OpenAPI/Swagger dependencies.
- Set environment-based configuration and `.env.example`; never commit credentials.
- Define response envelope, pagination metadata, global exception mapper and correlation/logging convention.
- Add `GET /api/v1/health`, Swagger/OpenAPI path and CORS policy for Android local development.

## Contract

Success/error shape and endpoints are in [`../../docs/api-contract.md`](../../docs/api-contract.md). All controllers return DTOs, never JPA entities.
Backend implementation notes and request/response ownership are in [`../API_NOTES.md`](../API_NOTES.md).

## Rules adopted from Web_USTH

- `ddl-auto=validate` on shared/integration environments; schema evolves through versioned migrations only.
- Secrets exist only in local env/CI secret store. Commit an example file with empty values.
- Validation, resource-not-found and unexpected error have stable codes.

## Done when

- [ ] New contributor can run a health test with documented commands.
- [ ] Swagger exposes the response envelope and errors.
- [ ] An invalid request returns contract error, not framework HTML.
- [ ] Base module contains no RSS business logic.
