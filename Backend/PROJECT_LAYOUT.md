# Implementation layout after G1

The root `Bxx-*` folders are permanent ownership/guide folders. Once B00 creates the Spring Boot application, implementation belongs in a single module so the app remains straightforward to build:

```text
Backend/app/src/main/java/<base-package>/
├── platform/    # B00: config, API envelope, exception handler
├── data/        # B01: entities, repositories
├── category/    # B02: controller/service/DTO
├── rss/         # B03 and B07: client, parser, scheduler
├── source/      # B04
├── article/     # B05
└── bookmark/    # B06
```

Keep controllers → services → repositories and separate request/response DTOs. Do not place code in `Guide.md` folders simply to satisfy ownership: link each package back to its `Bxx` guide in PR description.
