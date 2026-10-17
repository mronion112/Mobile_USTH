# 00 — App shell and navigation

**Owner:** FE Lead (Nguyễn Văn Đức)  
**Dependency:** G0 must choose exactly one: Jetpack Compose or XML Views.

## Responsibility

- Initialize one Android app module in Kotlin; configure base URL for emulator and physical device without committing secrets.
- Establish navigation routes: Home, Category, Search, Bookmarks, RSS Sources and Article Detail.
- Provide the mobile layout from mockups: top bar and four-item bottom navigation (Trang chủ, Khám phá, Đã lưu, Nguồn RSS).
- Define API client, common response envelope, ViewModel/repository convention and error mapping.
- Publish theme tokens: deep teal primary, pale teal chips, off-white background, rounded cards.

## Handover

- Feature owners receive navigation callbacks and shared `Article`, `Category`, `RSSSource` contract models.
- Each feature exposes a route/screen only; App shell owns route registration and selected bottom-nav state.

## API response objects FE must publish

All Retrofit/API calls return the same outer object. Feature screens only receive the unwrapped `data` plus pagination/error state from their repository.

```json
{
  "success": true,
  "message": "OK",
  "data": {},
  "meta": null,
  "timestamp": "2026-10-17T10:25:00Z"
}
```

For a list endpoint, `data` is an array and `meta` is present:

```json
{
  "success": true,
  "message": "OK",
  "data": [],
  "meta": { "page": 0, "size": 20, "totalElements": 0, "totalPages": 0 },
  "timestamp": "2026-10-17T10:25:00Z"
}
```

The Kotlin API/domain models must use these fields and nullability exactly:

```text
CategoryApi(id: Long, name: String, slug: String)
SourceRefApi(id: Long, name: String)
ArticleApi(
  id: Long, title: String, description: String?, link: String,
  imageUrl: String?, publishedAt: String?, fetchedAt: String,
  source: SourceRefApi, category: CategoryApi, isBookmarked: Boolean
)
RssSourceApi(
  id: Long, name: String, rssUrl: String, category: CategoryApi,
  status: String, lastFetchedAt: String?, lastError: String?, createdAt: String
)
```

`imageUrl` is the remote thumbnail/hero image URL for Coil/Glide; when null, use a local placeholder. `link` is the original Dân trí URL and is opened only in S02. Do not construct either URL from title or source name.

All non-2xx/business-error responses use this shape:

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

Repository maps `error.code` to user-facing Vietnamese copy. A screen must not assume `data` exists when `success=false`.

## Done when

- [ ] App opens to Home and all navigation destinations are reachable with placeholder content.
- [ ] Base URL works for both `10.0.2.2` emulator and configurable LAN device.
- [ ] API errors map to one common user-safe message shape.
- [ ] No business feature or RSS XML parser is placed here.

Read [`../API_NOTES.md`](../API_NOTES.md) before defining Retrofit/Kotlin DTOs; it is the FE-side view of the shared API contract.
