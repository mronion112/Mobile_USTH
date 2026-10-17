# B04 — RSS source management

**Owner:** Nguyễn Hoàng Minh (persistence) + Vũ Minh Hoàng (validate/add flow)  
**Depends on:** B01, B02, B03.

## Endpoints

| Method | Endpoint | Purpose |
| --- | --- | --- |
| GET | `/api/v1/sources?status?&categoryId?` | List non-disabled sources |
| POST | `/api/v1/sources` | Save validated source and request initial fetch |
| DELETE | `/api/v1/sources/{id}` | Soft-disable source |

## Create request

```json
{ "name": "Dân trí — Thế giới", "rssUrl": "https://...", "categoryId": 2 }
```

## Rules

- Duplicate normalized URL returns `409 DUPLICATE_SOURCE`.
- API validates fields and delegates feed validation/parsing to B03.
- Delete sets `DISABLED`, stops later scheduler runs and preserves article history.
- List includes category, status, last fetched time and last safe error message for S06.

## Done when

- [ ] Create/duplicate/delete/status tests pass.
- [ ] Removed source cannot be scheduled but its existing article detail still resolves.
- [ ] Contract examples are copied to `docs/api-contract.md`.
