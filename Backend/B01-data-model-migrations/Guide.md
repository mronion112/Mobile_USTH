# B01 — Data model and migrations

**Owner:** Nguyễn Hoàng Minh  
**Depends on:** B00.

## Tables

```text
categories
rss_sources
articles
bookmarks
```

## Relations and constraints

```text
rss_sources.category_id -> categories.id
articles.source_id      -> rss_sources.id
articles.category_id    -> categories.id
bookmarks.article_id    -> articles.id

categories.slug                         UNIQUE
rss_sources.rss_url                     UNIQUE
articles (source_id, guid)              UNIQUE when guid exists
articles (source_id, canonical_link)    unique fallback for missing guid
bookmarks.article_id                    UNIQUE for no-login MVP
```

## Required fields

- `RSSSource`: id, name, rssUrl, categoryId, status (`ACTIVE|FETCHING|ERROR|DISABLED`), lastFetchedAt, lastError, createdAt, removedAt.
- `Article`: id, title, description nullable, link, imageUrl nullable, guid nullable, publishedAt nullable, fetchedAt, sourceId, categoryId.
- `Bookmark`: id, articleId, createdAt. Add `userId` only after authentication is explicitly in scope.

## Rules

- Source removal is soft-disable. Keep old articles and bookmarks.
- Migration files are append-only and named/versioned. Do not rely on JPA to create production schema.
- Seed only verified Dân trí feed URLs; unverified category URLs must not enter shared seed data.

## Done when

- [ ] Clean database migration succeeds and rollback/forward strategy is documented.
- [ ] Foreign-key, unique-index and nullable-field tests pass.
- [ ] README states chosen DB (PostgreSQL/MySQL) and local config.
