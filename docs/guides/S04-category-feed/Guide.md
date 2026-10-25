# S04 — Category Feed

**Owner:** Nguyễn Tiến Đạt  
**Visual reference:** [Category Feed.png](../../mockups/Category%20Feed.png)

## Scope

Show the selected Dân trí category, newest-first list and category switching from side/chip navigation. Reuse article list/card; this is not a copied Home feed.

## Data dependency

- Local article query by `categoryId`, newest-first, paged.
- Local `Category` list for the header and tab switching.
- Local bookmark flag toggle on a card.

## Local model used by S04

Category `id=9`, page 0:

```json
{
  "items": [{
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
  "page": 0,
  "totalElements": 8,
  "totalPages": 1
}
```

The visible screen title comes from the selected stored `Category` object, not by parsing `slug`. Each card still reads its own `category` so a stored inconsistency is observable/testable.

The category list holds `{ id, name, slug }`. Bookmarking stores the full Article item with `isBookmarked: true`; unbookmarking stores it with `isBookmarked: false`. The category article query itself remains the source of all card fields (`imageUrl`, `link`, source and publication time).

## DESIGN2 visual requirements

- Make the selected category an editorial screen anchor using Plus Jakarta Sans 20sp/700, then a short muted subtitle or article count.
- Category tabs are pill chips; preserve horizontal scroll and teal active state rather than wrapping into a cramped multi-line grid.
- Reuse the S01 compact/featured ArticleCard untouched: same white surface, 16dp radius, thumbnail, metadata and image fallback.
- Use the editorial slate canvas and 12–16dp card gaps; no separate visual theme for categories.

## Rules and done

- [ ] Screen title and selected category always match the ID queried.
- [ ] Cards open S02 and bookmark uses shared action.
- [ ] Loading, category-empty and error/retry are present.
