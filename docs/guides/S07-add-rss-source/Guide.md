# S07 — Add RSS Source

**Owner:** Vũ Minh Hoàng  
**Visual reference:** [Add RSS Source.png](../../mockups/Add%20RSS%20Source.png)

## Scope

Implement the add-source form/bottom sheet: name, URL, Validate action, detected feed preview, category selection, Save and cancel. It returns to S06 after successful initial fetch request.

## Data dependency

- On-device feed validation: fetch the entered URL and parse its RSS/Atom XML.
- Local source creation with `{ name, rssUrl, categoryId }`.
- Local `Category` list for manual selection.

## Local models used by S07

### 1. Validate RSS URL

Input: `{ "rssUrl": "https://dantri.com.vn/rss/the-gioi.rss" }`. The app fetches the URL on device and parses the feed. Preview result:

```json
{
  "valid": true,
  "detectedTitle": "Dân trí - Thế giới",
  "detectedSiteName": "Dân trí",
  "suggestedCategory": { "id": 2, "name": "Thế giới", "slug": "the-gioi" }
}
```

Display `detectedTitle` and `detectedSiteName` in the preview. Preselect `suggestedCategory.id` when non-null; it is a suggestion, so the user may choose any stored category. Do not save until validation reports `valid=true`.

### 2. Create source and initial fetch request

Input:

```json
{
  "name": "Dân trí — Thế giới",
  "rssUrl": "https://dantri.com.vn/rss/the-gioi.rss",
  "categoryId": 2
}
```

Stored result:

```json
{
  "id": 5,
  "name": "Dân trí — Thế giới",
  "rssUrl": "https://dantri.com.vn/rss/the-gioi.rss",
  "category": { "id": 2, "name": "Thế giới", "slug": "the-gioi" },
  "status": "FETCHING",
  "lastFetchedAt": null,
  "lastError": null,
  "createdAt": "2026-10-17T10:26:00Z"
}
```

After success, close form and insert/reload S06 with the stored source. `status=FETCHING` is a normal first-fetch state, not a validation error.

### Validate/create error result

```json
{
  "code": "RSS_FEED_INVALID",
  "message": "RSS URL không hợp lệ hoặc không thể truy cập"
}
```

Keep name, URL and selected category in the form. `DUPLICATE_SOURCE` is a distinct error: show that this URL is already being followed and do not erase the input.

### 3. Categories for manual selection

```json
[
  { "id": 2, "name": "Thế giới", "slug": "the-gioi" },
  { "id": 9, "name": "Công nghệ", "slug": "cong-nghe" }
]
```

Show `name`, retain the numeric `id` and submit that value as `categoryId`; do not submit `slug` or category display name in the create request.

## DESIGN2 visual requirements

- Prefer a modal bottom sheet above S06: white level-3 surface, 20dp top corners, 16–20dp padding and dimmed canvas behind it.
- Inputs are 44dp+, 12dp radius, slate subtle background; focus uses teal ring. Labels use Plus Jakarta Sans 12–14sp.
- Validate and Add Source are teal primary actions. Cancel/close is a ghost action. A valid preview uses teal/green confirmation, while invalid state uses red text plus helpful body copy.
- Keep detected feed preview in a soft white/slate card; category choices are horizontally scrollable pill chips.

## Rules

- Feed validation runs on device by fetching and parsing the feed; mobile additionally validates simple required/URL format for fast user feedback.
- Validation failure shows the safe reason and leaves entered data intact.
- Never fetch feed content outside the app-shell fetcher; screens only read the local store.

## Done when

- [x] Valid preview preselects suggested category but user may change it.
- [x] Invalid/loading/save failure states prevent double submission.
- [x] Successful save refreshes S06 list.

## Implementation files

- `app/src/main/java/vn/edu/usth/rssreader/ui/screen/rss/components/AddRssSourceSheet.kt`
- `app/src/main/java/vn/edu/usth/rssreader/ui/screen/rss/RssSourcesUiState.kt`
- `app/src/main/java/vn/edu/usth/rssreader/ui/screen/rss/RssSourcesViewModel.kt`
- `app/src/main/java/vn/edu/usth/rssreader/data/repository/RssSourceRepository.kt`
- `app/src/main/java/vn/edu/usth/rssreader/data/repository/RssFeedInspector.kt`
- `app/src/main/java/vn/edu/usth/rssreader/data/model/RssSource.kt`
- `app/src/test/java/vn/edu/usth/rssreader/data/repository/RssFeedInspectorTest.kt`
- `app/src/test/java/vn/edu/usth/rssreader/ui/screen/rss/SuggestCategoryForFeedTest.kt`
