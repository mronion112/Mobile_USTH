# S08 — Remove RSS Source Confirmation

**Owner:** Nguyễn Văn Đức  
**Visual reference:** [Remove Confirmation.png](../../Remove%20Confirmation.png)

## Scope

Build a modal confirmation before any source deletion. It names the selected source and explains that new fetches stop while existing articles are kept.

## API dependency

- `DELETE /api/v1/sources/{id}`

## API response used by S08

```http
DELETE /api/v1/sources/3
```

```json
{
  "success": true,
  "message": "Đã xóa RSS source khỏi danh sách",
  "data": null,
  "meta": null,
  "timestamp": "2026-10-17T10:30:00Z"
}
```

There is no deleted-source object in `data`. Use the immutable `id` and `name` passed from the tapped S06 card to show the dialog. On success remove/refresh the source list and show the message; do **not** remove article cards/bookmarks because backend only disables future fetching.

If the source is already missing, error is:

```json
{
  "success": false,
  "message": "Không tìm thấy RSS source",
  "data": null,
  "meta": null,
  "error": { "code": "RESOURCE_NOT_FOUND", "details": "sourceId=3" },
  "timestamp": "2026-10-17T10:30:00Z"
}
```

Close/reload the dialog/list safely for this stale-state case; never present raw `details` directly to the user.

## Rules and done

- [ ] Cancel is the non-destructive default and closes without network call.
- [ ] Confirm disables controls, handles failure in the dialog and then updates S06 on success.
- [ ] The user can never remove a different source because stale list state was reused; pass immutable source id/name.
