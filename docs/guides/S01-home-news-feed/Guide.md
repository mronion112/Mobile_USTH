# S01 — Home News Feed

**Owner:** Nguyễn Tiến Đạt  
**Visual reference:** [Home News Feed.png](../../../Home%20News%20Feed.png)

## Scope

Build the default newest-first feed: greeting/header, search entry point, category chips, source filter, refresh, highlight/latest article cards and load-more affordance. Selecting an article opens S02; selecting a category opens S04; bookmark uses shared component.

## Data dependency

- Local article query: newest-first, optional `categoryId` / `sourceId` filter, paged.
- Local `Category` and `RssSource` lists for the chips and source selector.
- Local bookmark flag toggle on a card.

## Local model used by S01

### 1. Latest feed

Newest-first page (`categoryId` and `sourceId` omitted when **Tất cả** is selected):

```json
{
  "items": [{
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
  "page": 0,
  "totalPages": 3
}
```

Map `items` to `ArticleList`; map `page`/`totalPages` to load-more visibility. Card image loads from `imageUrl`; if it is null, show placeholder. Detail navigation uses `id`, while original-browser action later uses `link` in S02.

### 2. Category chips and source filter

```json
{ "id": 9, "name": "Công nghệ", "slug": "cong-nghe" }
```

```json
{
  "id": 3, "name": "Dân trí — Công nghệ",
  "rssUrl": "https://dantri.com.vn/rss/suc-manh-so.rss",
  "category": { "id": 9, "name": "Công nghệ", "slug": "cong-nghe" },
  "status": "ACTIVE", "lastFetchedAt": "2026-10-17T10:25:00Z",
  "lastError": null, "createdAt": "2026-10-01T08:00:00Z"
}
```

Use only `id` and `name` for filter UI. Do not show a source whose status is `DISABLED`.

### 3. Bookmark toggle from a card

Toggling a bookmark flips the stored `isBookmarked` flag on the same complete Article object above. The card keeps its existing `imageUrl`, `link` and metadata after the toggle; only bookmark state changes. Do not replace a card with a partial update object.

## DESIGN2 visual requirements

- Screen canvas `#F8FAFC`, 16dp horizontal margin and fixed Editorial Pulse bottom navigation.
- Place the 44dp rounded search entry first, then horizontally scrollable category pills. Active chip is `#0F766E`; inactive chip is `#F1F5F9`.
- First/featured story is a white elevated card with 16:9 image, amber Trending badge only when data warrants it, bold Plus Jakarta Sans headline and two-line Inter summary.
- Latest stories use white compact cards with text left and 72×72dp image right. A null `imageUrl` shows a subtle slate placeholder at the same size.
- Use 12–16dp vertical rhythm; metadata is muted slate, never larger/more prominent than article title.

## States and events

- Loading skeleton; empty CTA **Thêm nguồn RSS** → S06/S07; retry on error.
- Refresh re-fetches subscribed feeds on device and reloads the local store.
- Keep query/filter state across article-detail back navigation.

## Done when

- [ ] Cards are newest-first and shared ArticleCard is used.
- [ ] Category/source filters produce the correct local query.
- [ ] Detail, bookmark, empty and error paths are manually tested.
