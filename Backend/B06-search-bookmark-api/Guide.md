# B06 — Search, filter and bookmarks

**Owner:** Nguyễn Hoàng Anh (search) + Nguyễn Hoàng Minh (bookmark persistence)  
**Depends on:** B01, B02, B05.

## Endpoints

```http
GET    /api/v1/search?q=&categoryId=&sourceId=&page=0&size=20
GET    /api/v1/bookmarks?page=0&size=20&categoryId=
POST   /api/v1/bookmarks/{articleId}
DELETE /api/v1/bookmarks/{articleId}
```

## Rules

- Search case-insensitively over stored `title` and `description`, never remote RSS.
- Search combines optional category/source filters; empty query behavior must be documented (recommend: browse filtered results).
- Bookmark add is idempotent; delete either idempotently succeeds or returns documented state error—choose once for FE.
- No-account MVP uses a single demo bookmark set. If user account is added, migration/ownership and contract change are required.

## Done when

- [ ] Title/description query, filters and no-result page test pass.
- [ ] Concurrent/double bookmark request cannot produce duplicates.
- [ ] S03/S05 have response examples and expected error codes.
