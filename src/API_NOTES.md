# Kotlin Frontend API request & response notes

This is the Android direct-consumption view of the [external API reference](../docs/api-contract.md). The app calls the REST API itself, unwraps JSON once in a shared API client and renders typed response data in the owning screen.

## Common Kotlin model shape

Model the envelope once in the app-shell/data layer and unwrap it in the repository before it reaches a screen.

```text
ApiEnvelope<T>
  success: Boolean
  message: String
  data: T?
  meta: PageMeta?
  error: ApiError?
  timestamp: String

ApiError
  code: String
  details: String?

PageMeta
  page: Int
  size: Int
  totalElements: Long
  totalPages: Int
```

`data` is nullable at envelope level because an error has no data. A repository exposes a typed success value or an application error; composables must not parse raw API JSON.

## Kotlin domain/API fields

```text
Category: id, name, slug
SourceRef: id, name
RssSource: id, name, rssUrl, category, status,
           lastFetchedAt?, lastError?, createdAt
Article: id, title, description?, link, imageUrl?, publishedAt?,
         fetchedAt, source, category, isBookmarked
ValidationPreview: valid, detectedTitle, detectedSiteName, suggestedCategory?
```

Use nullable Kotlin types for every `?` field. `publishedAt` and `lastFetchedAt` are ISO-8601 UTC strings/instants; relative display is a UI formatter concern, not a DTO field.

## Request models FE may send

```json
POST /api/v1/sources/validate
{ "rssUrl": "https://dantri.com.vn/rss/the-gioi.rss" }
```

```json
POST /api/v1/sources
{
  "name": "Dân trí — Thế giới",
  "rssUrl": "https://dantri.com.vn/rss/the-gioi.rss",
  "categoryId": 2
}
```

Bookmark, remove source and refresh requests have no body. Filters/pagination are query parameters; omit optional ID parameters instead of sending a fake `0` or empty-string ID.

## Response examples FE must handle

### Paged article list — Home/Search/Category/Bookmarks

```json
{
  "success": true,
  "message": "OK",
  "data": [{
    "id": 1024,
    "title": "Tiêu đề bài viết từ Dân trí",
    "description": "Tóm tắt từ RSS.",
    "link": "https://dantri.com.vn/example",
    "imageUrl": null,
    "publishedAt": "2026-10-17T10:20:00Z",
    "fetchedAt": "2026-10-17T10:25:00Z",
    "source": { "id": 3, "name": "Dân trí — Thế giới" },
    "category": { "id": 2, "name": "Thế giới", "slug": "the-gioi" },
    "isBookmarked": false
  }],
  "meta": { "page": 0, "size": 20, "totalElements": 1, "totalPages": 1 },
  "timestamp": "2026-10-17T10:25:00Z"
}
```

### Validate source preview — S07

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

### Source status — S06/S09

```json
{
  "success": true,
  "message": "OK",
  "data": {
    "id": 3,
    "name": "Dân trí — Thế giới",
    "rssUrl": "https://dantri.com.vn/rss/the-gioi.rss",
    "category": { "id": 2, "name": "Thế giới", "slug": "the-gioi" },
    "status": "ERROR",
    "lastFetchedAt": "2026-10-17T10:00:00Z",
    "lastError": "Không thể kết nối nguồn RSS.",
    "createdAt": "2026-10-16T10:00:00Z"
  },
  "meta": null,
  "timestamp": "2026-10-17T10:25:00Z"
}
```

## Screen → API map

| Screen | Read | Write/action |
| --- | --- | --- |
| S01 | categories, sources, articles | bookmark toggle |
| S02 | article detail | bookmark toggle, external browser intent for `link` |
| S03 | categories, sources, search | bookmark toggle |
| S04 | categories, category articles | bookmark toggle |
| S05 | categories, bookmarks | remove bookmark |
| S06 | sources | refresh source, open S07/S08 |
| S07 | categories | validate source, create source |
| S08 | selected immutable source | delete source |
| S09 | source status/list | refresh source |

## UI error and state mapping

| External API result | Kotlin repository result | UI behavior |
| --- | --- | --- |
| 2xx, empty `data` list | `Success(emptyList())` | Contextual empty state, not error |
| 400/404/409/422 | Typed `ApiError` | Explain safely; preserve entered S07 form data |
| 500/network/timeout | Retryable app error | Shared error state and retry CTA |
| `status=FETCHING` | Valid `RssSource` | Disable duplicate refresh, show progress |
| `status=ERROR` | Valid `RssSource` | Show last safe error and retry CTA |

Do not show external API `details` verbatim if it may be technical; present approved Vietnamese copy by error code. FE never reads/parses RSS XML or assumes missing description/image/time has a value.
