# S04 — Category Feed

**Owner:** Nguyễn Tiến Đạt  
**Visual reference:** [Category Feed.png](../../Category%20Feed.png)

## Scope

Show the selected Dân trí category, newest-first list and category switching from side/chip navigation. Reuse article list/card; this is not a copied Home feed.

## API dependency

- `GET /api/v1/categories/{categoryId}/articles?page=0&size=20`
- `GET /api/v1/categories`
- `POST /api/v1/bookmarks/{articleId}` / `DELETE /api/v1/bookmarks/{articleId}`

## API response used by S04

```http
GET /api/v1/categories/9/articles?page=0&size=20
```

```json
{
  "success": true,
  "message": "OK",
  "data": [{
    "id": 1024,
    "title": "Tin mới nhất về công nghệ",
    "description": "Mô tả RSS.",
    "link": "https://dantri.com.vn/cong-nghe/example.htm",
    "imageUrl": "https://cdn.dantri.com.vn/example.jpg",
    "publishedAt": "2026-10-17T10:20:00Z",
    "fetchedAt": "2026-10-17T10:25:00Z",
    "source": { "id": 3, "name": "Dân trí — Công nghệ" },
    "category": { "id": 9, "name": "Công nghệ", "slug": "cong-nghe" },
    "isBookmarked": false
  }],
  "meta": { "page": 0, "size": 20, "totalElements": 8, "totalPages": 1 },
  "timestamp": "2026-10-17T10:25:00Z"
}
```

The visible screen title comes from the selected `Category` object returned by `GET /categories`, not by parsing `slug`. Each card still reads its own `data[i].category` so a backend inconsistency is observable/testable.

`GET /categories` response is `{ success, message, data: [{ id, name, slug }], meta: null, timestamp }`. Bookmark POST returns the full Article item with `isBookmarked: true`; bookmark DELETE returns `{ success: true, data: null, ... }`. The category article response itself remains the source of all card fields (`imageUrl`, `link`, source and publication time).

## Rules and done

- [ ] Screen title and selected category always match the ID queried.
- [ ] Cards open S02 and bookmark uses shared action.
- [ ] Loading, category-empty and error/retry are present.
