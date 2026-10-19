# S05 — Bookmarks

**Owner:** Nguyễn Thị Mai Linh

## Scope

Display saved articles, optional category filter, open detail and allow removal of a bookmark. Match the **Đã lưu** bottom tab shown in the mockups.

## API dependency

- `GET /api/v1/bookmarks?page=0&size=20&categoryId?`
- `DELETE /api/v1/bookmarks/{articleId}`
- `GET /api/v1/categories`

## API response used by S05

```http
GET /api/v1/bookmarks?page=0&size=20&categoryId=9
```

```json
{
  "success": true,
  "message": "OK",
  "data": [{
    "id": 1024,
    "title": "Bài đã lưu để đọc sau",
    "description": "Tóm tắt RSS.",
    "link": "https://dantri.com.vn/cong-nghe/example.htm",
    "imageUrl": "https://cdn.dantri.com.vn/example.jpg",
    "publishedAt": "2026-10-16T10:20:00Z",
    "fetchedAt": "2026-10-16T10:25:00Z",
    "source": { "id": 3, "name": "Dân trí — Công nghệ" },
    "category": { "id": 9, "name": "Công nghệ", "slug": "cong-nghe" },
    "isBookmarked": true
  }],
  "meta": { "page": 0, "size": 20, "totalElements": 1, "totalPages": 1 },
  "timestamp": "2026-10-17T10:25:00Z"
}
```

Every returned row should have `isBookmarked=true`. Render image/title/source/category/time exactly as the shared ArticleCard. After `DELETE /api/v1/bookmarks/{articleId}`, expect `data: null`; remove that item from the current list only after success, or reload the list after failure.

Exact successful delete response:

```json
{ "success": true, "message": "Đã bỏ lưu bài viết", "data": null, "meta": null, "timestamp": "2026-10-17T10:26:00Z" }
```

For the category filter, `GET /api/v1/categories` returns `data: [{ "id": 9, "name": "Công nghệ", "slug": "cong-nghe" }]`; send only the selected numeric `id` as `categoryId`.

## DESIGN2 visual requirements

- The saved tab uses the same white compact article cards and slate canvas as Home, with amber bookmark icon only to signify a saved item.
- Category filter is a pill/rounded selector; keep tap target 44dp minimum.
- Empty state feels editorial and calm: centered muted bookmark icon, Plus Jakarta Sans message, Inter supporting copy and teal browse CTA.
- Removing a bookmark should not cause the card layout to jump; use a short confirmation/snackbar then update list with 12–16dp rhythm retained.

## Rules

- Removing a bookmark should update the current list deterministically; handle API failure by restoring/refreshing state.
- Empty state CTA **Khám phá tin tức** opens S01 or S03.

## Done when

- [ ] A saved card uses the shared filled bookmark indicator.
- [ ] Remove, filter, empty, loading and error cases are tested.
