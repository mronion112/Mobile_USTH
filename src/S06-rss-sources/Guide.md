# S06 — RSS Sources

**Owner:** Nguyễn Hoàng Anh  
**Visual reference:** [RSS Sources.png](../../RSS%20Sources.png)

## Scope

Show source name, URL, category, status, last fetched time and actions: add, refresh/retry and remove. The screen is the entry to S07/S08 and visibly surfaces S09 source state.

## Data dependency

- Local source list, optional `status` / `categoryId` filter.
- On-device feed refresh for one source.
- Local source removal only after S08 confirmation.

## Local model used by S06

### Source list

Optional filters `status=ACTIVE`, `categoryId=9`. When the screen shows all sources, query without them.

```json
[
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
]
```

Mapping: name ← `name`; URL label ← `rssUrl`; category badge ← `category.name`; state pill ← `status`; relative time ← `lastFetchedAt`; error/retry message ← `lastError`. `lastFetchedAt` and `lastError` are nullable. A `DISABLED` source is hidden from the list.

### Manual refresh result

Refreshing source `id=3` re-fetches its feed on device. While fetching, the stored source reads:

```json
{
  "id": 3, "name": "Dân trí — Công nghệ",
  "rssUrl": "https://dantri.com.vn/rss/suc-manh-so.rss",
  "category": { "id": 9, "name": "Công nghệ", "slug": "cong-nghe" },
  "status": "FETCHING", "lastFetchedAt": "2026-10-17T10:25:00Z",
  "lastError": null, "createdAt": "2026-10-01T08:00:00Z"
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
- Refresh may run asynchronously in the background; disable duplicate actions while a source is fetching.

## Done when

- [ ] Active, Fetching and Error cards have distinct accessible display.
- [ ] Add, retry and remove paths reach S07/S08 correctly.
