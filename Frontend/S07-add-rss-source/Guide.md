# S07 — Add RSS Source

**Owner:** Nguyễn Văn Đức  
**Visual reference:** [Add RSS Source.png](../../Add%20RSS%20Source.png)

## Scope

Implement the add-source form/bottom sheet: name, URL, Validate action, detected feed preview, category selection, Save and cancel. It returns to S06 after successful initial fetch request.

## API dependency

- `POST /api/v1/sources/validate` body `{ "rssUrl": "..." }`
- `POST /api/v1/sources` body `{ "name":"...", "rssUrl":"...", "categoryId":2 }`
- `GET /api/v1/categories`

## API responses used by S07

### 1. Validate RSS URL

```http
POST /api/v1/sources/validate
Content-Type: application/json

{ "rssUrl": "https://dantri.com.vn/rss/the-gioi.rss" }
```

```json
{
  "success": true,
  "message": "RSS feed hợp lệ",
  "data": {
    "valid": true,
    "detectedTitle": "Dân trí - Thế giới",
    "detectedSiteName": "Dân trí",
    "suggestedCategory": { "id": 2, "name": "Thế giới", "slug": "the-gioi" }
  },
  "meta": null,
  "timestamp": "2026-10-17T10:25:00Z"
}
```

Display `detectedTitle` and `detectedSiteName` in the preview. Preselect `suggestedCategory.id` when non-null; it is a suggestion, so the user may choose any ID returned by `GET /api/v1/categories`. Do not save until this response has `success=true` and `data.valid=true`.

### 2. Create source and initial fetch request

```http
POST /api/v1/sources
Content-Type: application/json

{
  "name": "Dân trí — Thế giới",
  "rssUrl": "https://dantri.com.vn/rss/the-gioi.rss",
  "categoryId": 2
}
```

```json
{
  "success": true,
  "message": "Đã thêm RSS source",
  "data": {
    "id": 5,
    "name": "Dân trí — Thế giới",
    "rssUrl": "https://dantri.com.vn/rss/the-gioi.rss",
    "category": { "id": 2, "name": "Thế giới", "slug": "the-gioi" },
    "status": "FETCHING",
    "lastFetchedAt": null,
    "lastError": null,
    "createdAt": "2026-10-17T10:26:00Z"
  },
  "meta": null,
  "timestamp": "2026-10-17T10:26:00Z"
}
```

After success, close form and insert/reload S06 with `data`. `status=FETCHING` is a normal first-fetch state, not a validation error.

### Validate/create error response

```json
{
  "success": false,
  "message": "RSS URL không hợp lệ hoặc không thể truy cập",
  "data": null,
  "meta": null,
  "error": { "code": "RSS_FEED_INVALID", "details": "URL phải trỏ đến RSS/Atom feed công khai." },
  "timestamp": "2026-10-17T10:25:00Z"
}
```

Keep name, URL and selected category in the form. `DUPLICATE_SOURCE` is a distinct error: show that this URL is already being followed and do not erase the input.

### 3. Categories for manual selection

```json
{
  "success": true,
  "message": "OK",
  "data": [
    { "id": 2, "name": "Thế giới", "slug": "the-gioi" },
    { "id": 9, "name": "Công nghệ", "slug": "cong-nghe" }
  ],
  "meta": null,
  "timestamp": "2026-10-17T10:25:00Z"
}
```

Show `name`, retain the numeric `id` and send that value as `categoryId`; do not submit `slug` or category display name in the create request.

## DESIGN2 visual requirements

- Prefer a modal bottom sheet above S06: white level-3 surface, 20dp top corners, 16–20dp padding and dimmed canvas behind it.
- Inputs are 44dp+, 12dp radius, slate subtle background; focus uses teal ring. Labels use Plus Jakarta Sans 12–14sp.
- Validate and Add Source are teal primary actions. Cancel/close is a ghost action. A valid preview uses teal/green confirmation, while invalid state uses red text plus helpful body copy.
- Keep detected feed preview in a soft white/slate card; category choices are horizontally scrollable pill chips.

## Rules

- Feed validation is owned by the external API; mobile only validates simple required/URL format for user feedback.
- Validation failure shows the API-safe reason and leaves entered data intact.
- Never fetch/parse XML in Android.

## Done when

- [ ] Valid preview preselects suggested category but user may change it.
- [ ] Invalid/loading/save failure states prevent double submission.
- [ ] Successful save refreshes S06 list.
