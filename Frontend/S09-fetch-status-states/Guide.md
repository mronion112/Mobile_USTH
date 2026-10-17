# S09 — Fetch status, empty and error states

**Owner:** FE Lead defines shared pattern; every screen owner applies it.

## Scope

Define visual behavior for `ACTIVE`, `FETCHING`, `ERROR`, last-successful-fetch and retry. Also review loading/empty/error across S01–S08.

## Backend contract

- Source fields: `status`, `lastFetchedAt`, `lastError`
- `GET /api/v1/sources/{id}/status`
- `POST /api/v1/sources/{id}/refresh`

## API response used by S09

### Poll/check one source

```http
GET /api/v1/sources/3/status
```

```json
{
  "success": true,
  "message": "OK",
  "data": {
    "id": 3,
    "name": "Dân trí — Công nghệ",
    "rssUrl": "https://dantri.com.vn/rss/suc-manh-so.rss",
    "category": { "id": 9, "name": "Công nghệ", "slug": "cong-nghe" },
    "status": "ACTIVE",
    "lastFetchedAt": "2026-10-17T10:25:00Z",
    "lastError": null,
    "createdAt": "2026-10-01T08:00:00Z"
  },
  "meta": null,
  "timestamp": "2026-10-17T10:25:00Z"
}
```

`POST /api/v1/sources/3/refresh` has the same `data` object shape. The only fields required to render S09 are `status`, `lastFetchedAt` and `lastError`; retain the rest so S06 can display the same source without a second model.

### Exact status mapping

| `data.status` | `lastFetchedAt` / `lastError` | FE behavior |
| --- | --- | --- |
| `ACTIVE` | timestamp / `null` | Display **Hoạt động** and relative last fetched time. |
| `FETCHING` | prior timestamp may exist / `null` | Display progress; disable refresh/remove duplicate action. |
| `ERROR` | last successful timestamp may exist / safe message | Display error and **Thử lại**. |
| `DISABLED` | any / any | Do not offer refresh; reload S06 because it should no longer list the source. |

`lastFetchedAt=null` is normal immediately after adding a source. Never parse `lastError` as a status; use the explicit `status` enum.

## Required copy/behavior

- Active: green/teal status and relative last fetched time.
- Fetching: non-blocking progress; no duplicate refresh.
- Error: visible but non-technical error summary and **Thử lại** CTA.
- Empty feed/bookmark/source: contextual CTA, never a blank page.

## Done when

- [ ] All S01–S08 owners link their state behavior in PR description.
- [ ] Error copy does not expose stack trace, URLs with credentials, or raw XML.
