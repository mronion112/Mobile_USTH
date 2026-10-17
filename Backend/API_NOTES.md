# Backend API request & response notes

This file turns the shared [API contract](../docs/api-contract.md) into implementation responsibilities for Spring Boot. It remains documentation only; B00 creates the code after G0.

## Response convention

Every controller returns the same envelope. `data` is a DTO (never entity); `meta` is present only for a paged list.

```json
{
  "success": true,
  "message": "OK",
  "data": {},
  "meta": null,
  "timestamp": "2026-10-17T09:00:00Z"
}
```

```json
{
  "success": false,
  "message": "RSS URL không hợp lệ hoặc không thể truy cập",
  "data": null,
  "meta": null,
  "error": {
    "code": "RSS_FEED_INVALID",
    "details": "URL phải trỏ đến RSS/Atom feed công khai."
  },
  "timestamp": "2026-10-17T09:00:00Z"
}
```

## Controller input/output matrix

| Endpoint | Request DTO / query | Success DTO | HTTP |
| --- | --- | --- | --- |
| `GET /health` | — | `{ status: "UP" }` | 200 |
| `GET /categories` | — | `List<CategoryResponse>` | 200 |
| `GET /articles` | `page,size,categoryId?,sourceId?` | `PageResponse<ArticleResponse>` | 200 |
| `GET /articles/{id}` | path `id` | `ArticleResponse` | 200 |
| `GET /categories/{id}/articles` | path `id`, `page,size` | `PageResponse<ArticleResponse>` | 200 |
| `GET /search` | `q,categoryId?,sourceId?,page,size` | `PageResponse<ArticleResponse>` | 200 |
| `GET /bookmarks` | `page,size,categoryId?` | `PageResponse<ArticleResponse>` | 200 |
| `POST /bookmarks/{articleId}` | path `articleId` | `ArticleResponse` bookmarked | 201 or 200 — decide in G0 |
| `DELETE /bookmarks/{articleId}` | path `articleId` | `data: null` | 200 |
| `GET /sources` | `status?,categoryId?` | `List<RssSourceResponse>` | 200 |
| `POST /sources/validate` | `ValidateSourceRequest` | `ValidateSourceResponse` | 200 / 422 |
| `POST /sources` | `CreateSourceRequest` | `RssSourceResponse` | 201 |
| `DELETE /sources/{id}` | path `id` | `data: null` | 200 |
| `POST /sources/{id}/refresh` | path `id` | `RssSourceResponse` | 202 or 200 — decide in G0 |
| `GET /sources/{id}/status` | path `id` | `RssSourceResponse` | 200 |

## DTO checklist

### Request DTOs

```text
ValidateSourceRequest
  rssUrl: required, max 1024, HTTP(S) URL

CreateSourceRequest
  name: required, max 160
  rssUrl: required, max 1024
  categoryId: required positive ID
```

Do not accept client-controlled fetch status, last error, article source/category, published time or bookmark state in write requests.

### Response DTOs

```text
CategoryResponse
  id, name, slug

SourceRefResponse
  id, name

RssSourceResponse
  id, name, rssUrl, category: CategoryResponse,
  status: ACTIVE | FETCHING | ERROR | DISABLED,
  lastFetchedAt?, lastError?, createdAt

ArticleResponse
  id, title, description?, link, imageUrl?, publishedAt?, fetchedAt,
  source: SourceRefResponse, category: CategoryResponse, isBookmarked

ValidateSourceResponse
  valid, detectedTitle, detectedSiteName, suggestedCategory?

PageMeta
  page, size, totalElements, totalPages
```

`description`, `imageUrl`, `publishedAt`, `lastFetchedAt`, `lastError` and `suggestedCategory` are nullable intentionally. Serialize ISO-8601 UTC instants.

## Error/status policy

| Case | HTTP | Code |
| --- | --- | --- |
| Malformed request/query | 400 | `VALIDATION_ERROR` |
| Article/category/source absent | 404 | `RESOURCE_NOT_FOUND` |
| Existing normalized RSS URL | 409 | `DUPLICATE_SOURCE` |
| Invalid, private, non-RSS or inaccessible feed | 422 | `RSS_FEED_INVALID` or `RSS_FETCH_FAILED` |
| Unexpected server error | 500 | `INTERNAL_ERROR` |

Validation failures must return field-safe details. Do not return stack traces, raw XML or internal host/IP information.

## Implementation and test handover

- B00 owns envelope, exception mapper and OpenAPI examples.
- B03 owns validate endpoint payload detection and SSRF-safe error mapping.
- B04/B07 own source response and status transitions.
- B05/B06 own article/bookmark pagination and `isBookmarked` behavior.
- Every implemented endpoint needs one success and one error controller test matching the JSON above.
