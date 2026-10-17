# B07 — Scheduler and fetch status

**Owner:** Nguyễn Hoàng Minh + Vũ Minh Hoàng  
**Depends on:** B03, B04.

## Behavior

Every configurable 10–30 minutes, select `ACTIVE` sources, run B03 pipeline, then persist status:

```text
ACTIVE → FETCHING → ACTIVE
                 └→ ERROR (lastError retained, later scheduler may retry)
```

## Endpoints

```http
POST /api/v1/sources/{id}/refresh
GET  /api/v1/sources/{id}/status
```

Manual refresh may return `202 Accepted`; status polling/list drives S06/S09. Do not make mobile wait for all RSS sources.

## Rules

- An error in one source must never abort the batch.
- Avoid overlapping fetches for the same source (lock/status guard).
- Record safe/truncated error detail and last successful fetch time.
- Disabled sources are never selected.
- Retry has bounded policy; no tight infinite loop.

## Done when

- [ ] Tests prove one failing source does not block a second valid source.
- [ ] Disabled and duplicate-refresh behavior is tested.
- [ ] S06/S09 have documented status transitions and sample JSON.
