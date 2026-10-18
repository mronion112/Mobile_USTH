# S06 — RSS Sources

**Owner:** Nguyễn Văn Đức  
**Visual reference:** [RSS Sources.png](../../RSS%20Sources.png)

## Scope

Show source name, URL, category, status, last fetched time and actions: add, refresh/retry and remove. The screen is the entry to S07/S08 and visibly surfaces S09 source state.

## API dependency

- `GET /api/v1/sources?status?&categoryId?`
- `POST /api/v1/sources/{id}/refresh`
- `DELETE /api/v1/sources/{id}` only after S08 confirmation

## API response used by S06

### Source list

```http
GET /api/v1/sources?status=ACTIVE&categoryId=9
```

Both query parameters are optional. When the screen shows all sources, call `GET /api/v1/sources` without them.

```json
{
  "success": true,
  "message": "OK",
  "data": [
    {
      "id": 3,
      "name": "Dân trí — Công nghệ",
      "rssUrl": "https://dantri.com.vn/rss/suc-manh-so.rss",
      "category": { "id": 9, "name": "Công nghệ", "slug": "cong-nghe" },
      "status": "ACTIVE",
      "lastFetchedAt": "2026-10-17T10:25:00Z",
      "lastError": null,
      "createdAt": "2026-10-01T08:00:00Z"
    },
    {
      "id": 4,
      "name": "Dân trí — Thế giới",
      "rssUrl": "https://dantri.com.vn/rss/the-gioi.rss",
      "category": { "id": 2, "name": "Thế giới", "slug": "the-gioi" },
      "status": "ERROR",
      "lastFetchedAt": "2026-10-17T08:00:00Z",
      "lastError": "Không thể kết nối nguồn RSS.",
      "createdAt": "2026-10-01T08:00:00Z"
    }
  ],
  "meta": null,
  "timestamp": "2026-10-17T10:25:00Z"
}
```

Mapping: name ← `name`; URL label ← `rssUrl`; category badge ← `category.name`; state pill ← `status`; relative time ← `lastFetchedAt`; error/retry message ← `lastError`. `lastFetchedAt` and `lastError` are nullable. A `DISABLED` source should not normally be returned by the list endpoint.

### Manual refresh result

```http
POST /api/v1/sources/3/refresh
```

The server may return `202 Accepted` if fetch continues in background, or `200 OK` if its team chose synchronous refresh. In both cases success `data` is a single object with the exact `RssSource` shape above, for example:

```json
{
  "success": true,
  "message": "Đã yêu cầu đồng bộ RSS source",
  "data": {
    "id": 3, "name": "Dân trí — Công nghệ",
    "rssUrl": "https://dantri.com.vn/rss/suc-manh-so.rss",
    "category": { "id": 9, "name": "Công nghệ", "slug": "cong-nghe" },
    "status": "FETCHING", "lastFetchedAt": "2026-10-17T10:25:00Z",
    "lastError": null, "createdAt": "2026-10-01T08:00:00Z"
  }, "meta": null, "timestamp": "2026-10-17T10:26:00Z"
}
```

Replace the matching source by `id` in local list, then refresh/poll using S09 rules; never assume a refresh immediately added articles.

## DESIGN2 visual requirements

- Use white source cards with 16dp radius, subtle border/shadow and 16dp padding on the slate canvas.
- Leading favicon/source icon is 36×36dp; feed name uses Plus Jakarta Sans 14sp/600, URL uses muted Inter caption and is visually truncated, not wrapped into a noisy block.
- Status uses semantic dot + text: green `#16A34A` Active, teal spinner Fetching, red `#DC2626` Error. Never convey status by color alone.
- “Thêm nguồn” is the 44–48dp teal primary action. Refresh/remove are compact ghost/icon controls with content descriptions.

## Rules

- Delete means stop future fetches; the UI must not promise article deletion.
- Refresh accepts asynchronous status (`202` may be used); disable duplicate actions while a source is fetching.

## Done when

- [ ] Active, Fetching and Error cards have distinct accessible display.
- [ ] Add, retry and remove paths reach S07/S08 correctly.
