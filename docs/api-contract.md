# RSS News Reader external API reference

> Repository này chỉ chứa Kotlin Frontend. Đây là reference để app gọi REST API bên ngoài trực tiếp; thay đổi endpoint, payload hoặc response phải được cập nhật ở đây cùng PR Frontend.

## Envelope chung

```json
{
  "success": true,
  "message": "OK",
  "data": {},
  "meta": { "page": 0, "size": 20, "totalElements": 1, "totalPages": 1 },
  "timestamp": "2026-10-17T15:00:00Z"
}
```

Error thêm `error: { "code": "RSS_FEED_INVALID", "details": "..." }`. Time dùng ISO-8601 UTC. `meta` là `null` ở endpoint không phân trang.

## Endpoints

| Method | Endpoint | Frontend screens | Query/body |
| --- | --- | --- | --- |
| GET | `/api/v1/health` | App shell diagnostic | — |
| GET | `/api/v1/categories` | S01, S03, S04, S05, S07 | — |
| GET | `/api/v1/articles` | S01 | `page,size,categoryId?,sourceId?` |
| GET | `/api/v1/articles/{id}` | S02 | — |
| GET | `/api/v1/categories/{id}/articles` | S04 | `page,size` |
| GET | `/api/v1/search` | S03 | `q,categoryId?,sourceId?,page,size` |
| GET | `/api/v1/bookmarks` | S05 | `page,size,categoryId?` |
| POST / DELETE | `/api/v1/bookmarks/{articleId}` | S01–S05 | — |
| GET | `/api/v1/sources` | S01, S03, S06 | `status?,categoryId?` |
| POST | `/api/v1/sources/validate` | S07 | `{ "rssUrl": "https://..." }` |
| POST | `/api/v1/sources` | S07 | `{ "name":"...", "rssUrl":"...", "categoryId":2 }` |
| DELETE | `/api/v1/sources/{id}` | S08 | Source is removed from app list |
| POST | `/api/v1/sources/{id}/refresh` | S06, S09 | — |
| GET | `/api/v1/sources/{id}/status` | S09 | — |

## DTO tối thiểu

`Article`: `id,title,description?,link,imageUrl?,publishedAt?,fetchedAt,source,category,isBookmarked`.

`RSSSource`: `id,name,rssUrl,category,status,lastFetchedAt?,lastError?,createdAt`.

`Category`: `id,name,slug`.

Mã lỗi dự kiến để FE map UI: `VALIDATION_ERROR`, `RESOURCE_NOT_FOUND`, `RSS_FEED_INVALID`, `RSS_FETCH_FAILED`, `DUPLICATE_SOURCE`, `INTERNAL_ERROR`.
