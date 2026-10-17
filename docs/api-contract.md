# RSS News Reader REST API contract — draft

> Chưa có implementation trong repo scaffold. Đây là interface duy nhất FE dùng để bắt đầu mock/integration; thay đổi field hoặc endpoint phải được review bởi BE owner và FE Lead.

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

| Method | Endpoint | Feature owner | Query/body |
| --- | --- | --- | --- |
| GET | `/api/v1/health` | B00 | — |
| GET | `/api/v1/categories` | B02 | — |
| GET | `/api/v1/articles` | B05 | `page,size,categoryId?,sourceId?` |
| GET | `/api/v1/articles/{id}` | B05 | — |
| GET | `/api/v1/categories/{id}/articles` | B05 | `page,size` |
| GET | `/api/v1/search` | B06 | `q,categoryId?,sourceId?,page,size` |
| GET | `/api/v1/bookmarks` | B06 | `page,size,categoryId?` |
| POST / DELETE | `/api/v1/bookmarks/{articleId}` | B06 | — |
| GET | `/api/v1/sources` | B04 | `status?,categoryId?` |
| POST | `/api/v1/sources/validate` | B03 | `{ "rssUrl": "https://..." }` |
| POST | `/api/v1/sources` | B04 | `{ "name":"...", "rssUrl":"...", "categoryId":2 }` |
| DELETE | `/api/v1/sources/{id}` | B04 | Soft remove: keep articles |
| POST | `/api/v1/sources/{id}/refresh` | B07 | — |
| GET | `/api/v1/sources/{id}/status` | B07 | — |

## DTO tối thiểu

`Article`: `id,title,description?,link,imageUrl?,publishedAt?,fetchedAt,source,category,isBookmarked`.

`RSSSource`: `id,name,rssUrl,category,status,lastFetchedAt?,lastError?,createdAt`.

`Category`: `id,name,slug`.

Mã lỗi chuẩn: `VALIDATION_ERROR`, `RESOURCE_NOT_FOUND`, `RSS_FEED_INVALID`, `RSS_FETCH_FAILED`, `DUPLICATE_SOURCE`, `INTERNAL_ERROR`.
