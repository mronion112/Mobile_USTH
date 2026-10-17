# Backend — feature-first scaffold

Backend follows the project-management pattern of `Web_USTH`: every capability owns a `Guide.md` containing endpoints, data relations, request/response examples, rules and acceptance. It is intentionally an implementation plan, not pre-written Spring code.

```text
B00-platform-contracts/        bootstrapping, config, response/error/OpenAPI
B01-data-model-migrations/     schema and Flyway migrations
B02-category-api/              category read API
B03-rss-ingestion/             safe fetch, RSS/Atom parsing and dedupe
B04-source-management/         source CRUD semantics
B05-article-feed-api/          Home, Detail and Category APIs
B06-search-bookmark-api/       search/filter and bookmarks
B07-scheduler-fetch-status/    automatic fetch lifecycle/status
B08-quality-integration/       test, CI, release checklist
```

## Code layout to create in G1

After B00 initializes the one Spring Boot module, map guide ownership to packages. Do **not** create a separate Spring Boot application for every folder.

```text
Backend/app/src/main/java/<base-package>/
├── platform/    # B00
├── data/        # B01 entities, repositories, migrations in resources
├── category/    # B02
├── rss/         # B03 + B07
├── source/      # B04
├── article/     # B05
└── bookmark/    # B06
```

Each owner keeps their `Guide.md` current even after implementation moves to the compiled source package. B08 owns integration test and CI changes.

Request/response, HTTP status, DTO responsibility and test notes: [API_NOTES.md](API_NOTES.md).
