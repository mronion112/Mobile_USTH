# S02 — Article Detail

**Owner:** Nguyễn Văn Đức  
**Visual reference:** [Article Detail.png](../../Article%20Detail.png)

## Scope

Display category, title, source/time metadata, optional image and only the description/content supplied by RSS. Include back, bookmark and **Đọc bài gốc** action.

## API dependency

- `GET /api/v1/articles/{id}`
- `POST /api/v1/bookmarks/{articleId}` / `DELETE /api/v1/bookmarks/{articleId}`

## API response used by S02

### Article detail

```http
GET /api/v1/articles/1024
```

```json
{
  "success": true,
  "message": "OK",
  "data": {
    "id": 1024,
    "title": "Việt Nam định hình tương lai hạ tầng tính toán đám mây và AI",
    "description": "Nội dung mô tả thực tế mà RSS cung cấp. Có thể chỉ là tóm tắt.",
    "link": "https://dantri.com.vn/cong-nghe/example.htm",
    "imageUrl": "https://cdn.dantri.com.vn/example.jpg",
    "publishedAt": "2026-10-17T10:20:00Z",
    "fetchedAt": "2026-10-17T10:25:00Z",
    "source": { "id": 3, "name": "Dân trí — Công nghệ" },
    "category": { "id": 9, "name": "Công nghệ", "slug": "cong-nghe" },
    "isBookmarked": true
  },
  "meta": null,
  "timestamp": "2026-10-17T10:25:00Z"
}
```

UI mapping: category chip ← `data.category.name`; source/byline ← `data.source.name` + `data.publishedAt`; hero ← `data.imageUrl`; body ← `data.description`; bookmark icon ← `data.isBookmarked`; browser Intent ← `data.link`. `description`, `imageUrl`, `publishedAt` can be null. `fetchedAt` is for optional debug/refresh metadata, not publication time.

### Bookmark action response

`POST /api/v1/bookmarks/1024` returns the same `Article` object in `data`, with `isBookmarked: true`. `DELETE /api/v1/bookmarks/1024` returns:

```json
{ "success": true, "message": "Đã bỏ lưu bài viết", "data": null, "meta": null, "timestamp": "2026-10-17T10:26:00Z" }
```

After DELETE, update local detail state to `isBookmarked=false`; on API failure restore previous visual state.

## Rules

- Never claim the description is full article text. Show a small notice when RSS only contains a summary.
- Open `article.link` using an external browser intent; validate that a resolver exists and show a safe error otherwise.
- Missing image/description/date must produce graceful fallback layout.

## Done when

- [ ] Detail renders all nullable combinations without crash.
- [ ] Bookmark state returns consistently to S01/S03/S04/S05.
- [ ] Original URL intent and back navigation are tested.
