# 00 — App shell and navigation

**Owner:** FE Lead (Nguyễn Văn Đức)  
**Dependency:** G0 must choose exactly one: Jetpack Compose or XML Views.

## Responsibility

- Initialize one Android app module in Kotlin with a local store (Room) as the single source of truth; no backend, no remote service, no committed secrets.
- Establish navigation routes: Home, Category, Search, Bookmarks, RSS Sources and Article Detail.
- Provide the mobile layout from mockups: edge-to-edge content with a four-item bottom navigation (Trang chủ, Khám phá, Đã lưu, Nguồn RSS) and no top bar.
- Own the on-device RSS fetcher (HTTP + XML parser), the local repository, ViewModel/state-holder convention and error mapping.
- Publish theme tokens from `DESIGN2.md`: Editorial Pulse teal/slate palette, Plus Jakarta Sans/Inter hierarchy, rounded cards and elevated surfaces.

## Handover

- Feature owners receive navigation callbacks and shared `Article`, `Category`, `RSSSource` local models.
- Each feature exposes a route/screen only; App shell owns route registration and selected bottom-nav state.
- The app-shell fetcher is the only place that performs network I/O and feed parsing. Screens read their ViewModel/state holder, which renders local-store data directly; there is no in-repo server layer.

## DESIGN2 shell requirements

- Canvas is `#F8FAFC`. Screens render edge to edge with 16dp mobile margins; there is no top bar.
- Bottom bar is fixed, white, elevated and `64dp + safe-area`; destinations are Home, Explore, Bookmarks and RSS Sources.
- Active nav uses teal; inactive icon/label uses `#94A3B8`. Labels use Plus Jakarta Sans 11sp/700.
- Use 16dp mobile margins and preserve insets when a screen scrolls below the bottom bar.

## Local models FE must share

Feature screens receive plain repository models, never raw XML or network payloads.

```kotlin
data class Category(val id: String, val name: String, val slug: String)
data class SourceRef(val id: Long, val name: String)

data class Article(
  val id: Long,
  val title: String,
  val description: String?,
  val link: String,
  val imageUrl: String?,
  val publishedAt: String?,
  val fetchedAt: String,
  val source: SourceRef,
  val category: Category,
  val isBookmarked: Boolean,
)

data class RssSource(
  val id: Long,
  val name: String,
  val rssUrl: String,
  val category: Category,
  val status: String,
  val lastFetchedAt: String?,
  val lastError: String?,
  val createdAt: String,
)
```

`imageUrl` is the remote thumbnail/hero image URL for Coil/Glide; when null, use a local placeholder. `link` is the original Dân trí URL and is opened only in S02. Do not construct either URL from title or source name.

All local fetch failures map to this shape:

```kotlin
data class FetchError(val code: String, val message: String)
```

Known codes: `RSS_FEED_INVALID`, `RSS_FETCH_FAILED`, `DUPLICATE_SOURCE`, `RESOURCE_NOT_FOUND`, `INTERNAL_ERROR`. Repository maps each code to user-facing Vietnamese copy. A screen must not assume stored data exists when the fetch reports failure.

## Done when

- [ ] App opens to Home and all navigation destinations are reachable with placeholder content.
- [ ] Subscribed feeds refresh on device (emulator and physical device) and persist locally.
- [ ] Fetch errors map to one common user-safe message shape.
- [ ] No server-side concern is placed here.

Read [`../DESIGN2_IMPLEMENTATION.md`](../DESIGN2_IMPLEMENTATION.md) before defining theme/components.
