# S02 — Article Detail

**Owner:** Nguyễn Văn Đức  
**Visual reference:** [Article Detail.png](../../mockups/Article%20Detail.png)

## Scope

Display category, title, source/time metadata, optional image and only the description/content supplied by RSS. Include back, bookmark and **Đọc bài gốc** action.

## Data dependency

- Local article lookup by `id`.
- Local bookmark flag toggle on the detail article.

## Local model used by S02

### Article detail

```json
{
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
}
```

UI mapping: category chip ← `category.name`; source/byline ← `source.name` + `publishedAt`; hero ← `imageUrl`; body ← `description`; bookmark icon ← `isBookmarked`; browser Intent ← `link`. `description`, `imageUrl`, `publishedAt` can be null. `fetchedAt` is for optional debug/refresh metadata, not publication time.

### Bookmark action result

Bookmarking keeps the same `Article` object with `isBookmarked: true` stored locally. Unbookmarking stores `isBookmarked: false`.

After unbookmarking, update local detail state to `isBookmarked=false`; on store failure restore previous visual state.

## DESIGN2 visual requirements

- Use 16dp margins on `#F8FAFC`; top bar is minimal with back, share/utility and bookmark controls.
- Category is a teal pill; title uses Plus Jakarta Sans 26sp/800 (mobile headline) with tight tracking; byline/time uses muted Inter metadata.
- `imageUrl` hero is 16:9, 14dp radius and full content width. When null, reserve a soft card/media placeholder—do not leave a broken image icon.
- Article summary/body uses Inter 16sp with generous 26sp line height in a white/card-subtle section. Use sky-blue `#0284C7` treatment for the original-link action.
- Keep bookmark amber only for saved state; the rest of the screen remains editorial, quiet and high-contrast.

## Rules

- Never claim the description is full article text. Show a small notice when RSS only contains a summary.
- Open `article.link` using an external browser intent; validate that a resolver exists and show a safe error otherwise.
- Missing image/description/date must produce graceful fallback layout.

## Done when

- [ ] Detail renders all nullable combinations without crash.
- [ ] Bookmark state returns consistently to S01/S03/S04/S05.
- [ ] Original URL intent and back navigation are tested.
