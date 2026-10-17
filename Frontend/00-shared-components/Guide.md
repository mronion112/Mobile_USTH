# 00 — Shared components

**Owner:** FE Lead; all FE contributors consume and may improve through review.

## Build once, reuse everywhere

- `ArticleCard`: optional thumbnail, title, truncated description, source/category/time and bookmark action.
- `ArticleList`: loading/load-more slot and click callback.
- `BookmarkButton`: optimistic state only after a defined repository policy; no duplicate request on double tap.
- `ScreenState`: Loading, Empty, Error/retry and content states.
- `CategoryChip`, source selector and source status pill.
- Date/time and null-value fallbacks.

## Exact Article fields consumed by shared UI

`ArticleCard` receives one already-unwrapped Article object:

```json
{
  "id": 1024,
  "title": "Tiêu đề bài viết từ Dân trí",
  "description": "Tóm tắt hoặc mô tả thực tế có trong RSS.",
  "link": "https://dantri.com.vn/example-article.htm",
  "imageUrl": "https://cdn.example.vn/article.jpg",
  "publishedAt": "2026-10-17T10:20:00Z",
  "fetchedAt": "2026-10-17T10:25:00Z",
  "source": { "id": 3, "name": "Dân trí — Thế giới" },
  "category": { "id": 2, "name": "Thế giới", "slug": "the-gioi" },
  "isBookmarked": false
}
```

Use `title`, `description`, `imageUrl`, `source.name`, `category.name`, `publishedAt` and `isBookmarked` in the list card. `link` is deliberately not opened from the card; navigate to S02 first. `description`, `imageUrl` and `publishedAt` may be `null`.

## Rules

- S01, S03, S04 and S05 must compose `ArticleCard`/`ArticleList`, not copy them.
- RSS `imageUrl`, `description` and `publishedAt` are nullable; components must remain usable with all three missing.
- Follow the supplied PNGs: teal primary action, subtle card borders, whitespace and concise Vietnamese labels.

## Done when

- [ ] Component preview/sample covers image and no-image cards.
- [ ] Accessibility labels exist for icon-only bookmark, refresh, remove and navigation controls.
- [ ] Empty/error copy and CTA are reusable rather than hard-coded per screen.
