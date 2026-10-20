# S05 — Bookmarks

**Owner:** Nguyễn Thị Mai Linh

## Scope

Display saved articles, optional category filter, open detail and allow removal of a bookmark. Match the **Đã lưu** bottom tab shown in the mockups.

## Data dependency

- Local bookmarked-article query, optional `categoryId` filter, paged.
- Local bookmark removal, restoring the flag to `false`.
- Local `Category` list for the filter.

## Local model used by S05

Bookmarked articles with `categoryId=9`, page 0:

```json
{
  "items": [{
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
  "page": 0,
  "totalElements": 1,
  "totalPages": 1
}
```

Every returned row should have `isBookmarked=true`. Render image/title/source/category/time exactly as the shared ArticleCard. After removing a bookmark, the row stores `isBookmarked=false`; remove that item from the current list only after the store confirms, or reload the list after failure.

For the category filter, the stored category list holds `{ "id": 9, "name": "Công nghệ", "slug": "cong-nghe" }`; filter by the selected numeric `id`.

## DESIGN2 visual requirements

- The saved tab uses the same white compact article cards and slate canvas as Home, with amber bookmark icon only to signify a saved item.
- Category filter is a pill/rounded selector; keep tap target 44dp minimum.
- Empty state feels editorial and calm: centered muted bookmark icon, Plus Jakarta Sans message, Inter supporting copy and teal browse CTA.
- Removing a bookmark should not cause the card layout to jump; use a short confirmation/snackbar then update list with 12–16dp rhythm retained.

## Rules

- Removing a bookmark should update the current list deterministically; handle store failure by restoring/refreshing state.
- Empty state CTA **Khám phá tin tức** opens S01 or S03.

## Done when

- [ ] A saved card uses the shared filled bookmark indicator.
- [ ] Remove, filter, empty, loading and error cases are tested.
