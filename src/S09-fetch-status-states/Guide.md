# S09 — Fetch status, empty and error states

**Owner:** Nguyễn Hoàng Minh

## Scope

Define visual behavior for `ACTIVE`, `FETCHING`, `ERROR`, last-successful-fetch and retry. Also review loading/empty/error across S01–S08.

## Status contract

- Source fields: `status`, `lastFetchedAt`, `lastError`
- Local status lookup for one source by `id`
- On-device feed refresh for one source by `id`

## Local model used by S09

### Poll/check one source

Source `id=3`:

```json
{
  "id": 3,
  "name": "Dân trí — Công nghệ",
  "rssUrl": "https://dantri.com.vn/rss/suc-manh-so.rss",
  "category": { "id": 9, "name": "Công nghệ", "slug": "cong-nghe" },
  "status": "ACTIVE",
  "lastFetchedAt": "2026-10-17T10:25:00Z",
  "lastError": null,
  "createdAt": "2026-10-01T08:00:00Z"
}
```

Refreshing source `id=3` produces the same stored object shape. The only fields required to render S09 are `status`, `lastFetchedAt` and `lastError`; retain the rest so S06 can display the same source without a second model.

### Exact status mapping

| `status` | `lastFetchedAt` / `lastError` | FE behavior |
| --- | --- | --- |
| `ACTIVE` | timestamp / `null` | Display **Hoạt động** and relative last fetched time. |
| `FETCHING` | prior timestamp may exist / `null` | Display progress; disable refresh/remove duplicate action. |
| `ERROR` | last successful timestamp may exist / safe message | Display error and **Thử lại**. |
| `DISABLED` | any / any | Do not offer refresh; reload S06 because it should no longer list the source. |

`lastFetchedAt=null` is normal immediately after adding a source. Never parse `lastError` as a status; use the explicit `status` enum.

## DESIGN2 visual requirements

- Status appears in a compact white source card, with dot/spinner and readable label—not a large alert panel.
- Active uses green `#16A34A`, fetching uses teal spinner, error uses red `#DC2626`; warning/queued metadata may use amber `#D97706`.
- “Thử lại” is a teal 44dp button or clear tertiary action; it must not look like a destructive/error button.
- Loading skeletons use slate-100/200 shimmer blocks matching the real card’s 16dp radius and thumbnail geometry. Empty/error screens retain the same calm slate canvas and spacing.

## Required copy/behavior

- Active: green/teal status and relative last fetched time.
- Fetching: non-blocking progress; no duplicate refresh.
- Error: visible but non-technical error summary and **Thử lại** CTA.
- Empty feed/bookmark/source: contextual CTA, never a blank page.

## Done when

- [ ] All S01–S08 owners link their state behavior in PR description.
- [x] Error copy does not expose stack trace, URLs with credentials, or raw XML.

## Implementation status

Implemented locally with Jetpack Compose:

- Shared `ScreenState` contract plus reusable `LoadingState`, `EmptyState` and
  `ErrorState` composables.
- Reusable `SourceFetchStatusCard` for `ACTIVE`, `FETCHING` and `ERROR`;
  `DISABLED` is intentionally hidden. Fetching has no retry action, preventing
  duplicate refresh requests.
- `RssSource`/`FetchError` shared models and presentation mapping for relative
  fetch time, safe Vietnamese errors and retry visibility.
- Android Studio previews cover loading, empty, error and all visible source
  statuses at 390dp width.

Automated checks run:

- `testDebugUnitTest`: status mapping, relative time, disabled visibility,
  known error-code copy and technical-detail redaction.
- `assembleDebug`: passed.
- `lintDebug`: passed.

Still pending: S01–S08 owners must connect their repositories/ViewModels to the
shared state components and document those integrations in their PRs.
