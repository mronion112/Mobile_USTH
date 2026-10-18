# S03 — Search & Filter

**Owner:** Nguyễn Thị Mai Linh  
**Visual reference:** [Search & Filter.png](../../Search%20%26%20Filter.png)

## Scope

Create keyword input, Category and Source filters, newest-first sort label, clear filters and result list. Search includes title and description only; S02 opens from a result.

## API dependency

- `GET /api/v1/search?q=&categoryId?&sourceId?&page=0&size=20`
- `GET /api/v1/categories`, `GET /api/v1/sources`
- `POST /api/v1/bookmarks/{articleId}` / `DELETE /api/v1/bookmarks/{articleId}`

## API response used by S03

```http
GET /api/v1/search?q=tr%C3%AD%20tu%E1%BB%87%20nh%C3%A2n%20t%E1%BA%A1o&categoryId=9&sourceId=3&page=0&size=20
```

```json
{
  "success": true,
  "message": "OK",
  "data": [{
    "id": 1024,
    "title": "Ứng dụng Trí tuệ nhân tạo giúp nâng cao năng suất",
    "description": "Tóm tắt từ RSS có chứa từ khóa tìm kiếm.",
    "link": "https://dantri.com.vn/cong-nghe/example.htm",
    "imageUrl": null,
    "publishedAt": "2026-10-17T10:20:00Z",
    "fetchedAt": "2026-10-17T10:25:00Z",
    "source": { "id": 3, "name": "Dân trí — Công nghệ" },
    "category": { "id": 9, "name": "Công nghệ", "slug": "cong-nghe" },
    "isBookmarked": false
  }],
  "meta": { "page": 0, "size": 20, "totalElements": 12, "totalPages": 1 },
  "timestamp": "2026-10-17T10:25:00Z"
}
```

Use `meta.totalElements` in “12 bài viết phù hợp”. `data=[]` with `success=true` is the no-result state, not an API error. Each item is the same Article object used by S01; `imageUrl=null` must render a placeholder, while `id` opens S02.

Categories and sources return the exact lightweight/filter objects documented in S01. Retain the selected IDs, not display strings, when making the search call.

For this screen, `GET /categories` returns `data: [{ id, name, slug }]`; `GET /sources` returns `data: [{ id, name, rssUrl, category, status, lastFetchedAt, lastError, createdAt }]`. Only `id`/`name` are needed in selectors. Bookmark POST returns the full matching Article with `isBookmarked: true`; bookmark DELETE returns a successful envelope with `data: null`.

## DESIGN2 visual requirements

- Search field is 44dp, 12dp radius, `#F1F5F9`, leading search icon and teal 2dp focus ring on white background.
- Filter chips/selectors remain horizontal and compact; use teal filled selection, never a dense form-like stack on mobile.
- Render results as white compact cards, 16dp radius, title hierarchy and 72×72dp thumbnail behavior from shared components.
- The result count is subdued Inter metadata; empty state has generous breathing space, muted illustration/icon and one ghost/primary recovery action.

## Rules

- Debounce while typing or search explicitly on submit; do not send a request per keystroke without debounce.
- Empty state repeats the keyword and provides **Xóa bộ lọc**.
- Preserve filters while navigating to/from detail in the same session.

## Done when

- [ ] Query, category and source serialize correctly.
- [ ] Clear returns controls and results to default state.
- [ ] No-result/loading/network-error states match shared state UI.
