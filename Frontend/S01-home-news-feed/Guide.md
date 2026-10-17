# S01 — Home News Feed

**Owner:** Nguyễn Tiến Đạt  
**Visual reference:** [Home News Feed.png](../../Home%20News%20Feed.png)

## Scope

Build the default newest-first feed: greeting/header, search entry point, category chips, source filter, refresh, highlight/latest article cards and load-more affordance. Selecting an article opens S02; selecting a category opens S04; bookmark uses shared component.

## API dependency

- `GET /api/v1/articles?page=0&size=20&categoryId?&sourceId?`
- `GET /api/v1/categories`
- `GET /api/v1/sources` for the source selector
- `POST /api/v1/bookmarks/{articleId}` / `DELETE /api/v1/bookmarks/{articleId}` for card bookmark toggle

## API response used by S01

### 1. Latest feed

```http
GET /api/v1/articles?page=0&size=20&categoryId=9&sourceId=3
```

`categoryId` and `sourceId` are omitted when **Tất cả** is selected. Response:

```json
{
  "success": true,
  "message": "OK",
  "data": [{
    "id": 1024,
    "title": "Việt Nam định hình tương lai hạ tầng tính toán đám mây",
    "description": "Tóm tắt hiển thị trên card đến từ RSS.",
    "link": "https://dantri.com.vn/cong-nghe/example.htm",
    "imageUrl": "https://cdn.dantri.com.vn/example.jpg",
    "publishedAt": "2026-10-17T10:20:00Z",
    "fetchedAt": "2026-10-17T10:25:00Z",
    "source": { "id": 3, "name": "Dân trí — Công nghệ" },
    "category": { "id": 9, "name": "Công nghệ", "slug": "cong-nghe" },
    "isBookmarked": false
  }],
  "meta": { "page": 0, "size": 20, "totalElements": 48, "totalPages": 3 },
  "timestamp": "2026-10-17T10:25:00Z"
}
```

Map `data` to `ArticleList`; map `meta.page`/`meta.totalPages` to load-more visibility. Card image loads from `imageUrl`; if it is null, show placeholder. Detail navigation uses `id`, while original-browser action later uses `link` in S02.

### 2. Category chips and source filter

```json
GET /api/v1/categories
{ "success": true, "message": "OK", "data": [
  { "id": 9, "name": "Công nghệ", "slug": "cong-nghe" }
], "meta": null, "timestamp": "2026-10-17T10:25:00Z" }
```

```json
GET /api/v1/sources
{ "success": true, "message": "OK", "data": [
  {
    "id": 3, "name": "Dân trí — Công nghệ",
    "rssUrl": "https://dantri.com.vn/rss/suc-manh-so.rss",
    "category": { "id": 9, "name": "Công nghệ", "slug": "cong-nghe" },
    "status": "ACTIVE", "lastFetchedAt": "2026-10-17T10:25:00Z",
    "lastError": null, "createdAt": "2026-10-01T08:00:00Z"
  }
], "meta": null, "timestamp": "2026-10-17T10:25:00Z" }
```

Use only `id` and `name` for filter UI. Do not show or request a source whose status is `DISABLED`.

### 3. Bookmark toggle from a card

`POST /api/v1/bookmarks/1024` returns `data` as the same complete Article object above, except `"isBookmarked": true`. `DELETE /api/v1/bookmarks/1024` returns:

```json
{ "success": true, "message": "Đã bỏ lưu bài viết", "data": null, "meta": null, "timestamp": "2026-10-17T10:26:00Z" }
```

The card keeps its existing `imageUrl`, `link` and metadata after a bookmark response; only bookmark state changes. Do not replace a card with a partial action response.

## States and events

- Loading skeleton; empty CTA **Thêm nguồn RSS** → S06/S07; retry on error.
- Refresh reloads the database feed; it must not fetch RSS XML directly from mobile.
- Keep query/filter state across article-detail back navigation.

## Done when

- [ ] Cards are newest-first and shared ArticleCard is used.
- [ ] Category/source filters form the correct API query.
- [ ] Detail, bookmark, empty and error paths are manually tested.
