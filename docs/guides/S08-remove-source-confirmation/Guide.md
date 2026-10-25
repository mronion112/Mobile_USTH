# S08 — Remove RSS Source Confirmation

**Owner:** Nguyễn Hoàng Anh  
**Visual reference:** [Remove Confirmation.png](../../../Remove%20Confirmation.png)

## Scope

Build a modal confirmation before any source deletion. It names the selected source and explains that new fetches stop while existing articles are kept.

## Data dependency

- Local source removal by immutable `id`.

## Local model used by S08

Removing source `id=3`:

```json
{ "removedId": 3, "message": "Đã xóa RSS source khỏi danh sách" }
```

There is no deleted-source object in the result. Use the immutable `id` and `name` passed from the tapped S06 card to show the dialog. On success remove/refresh the source list and show the message; do **not** remove article cards/bookmarks because removal only disables future fetching.

If the source is already missing, the result is:

```json
{ "code": "RESOURCE_NOT_FOUND", "message": "Không tìm thấy RSS source" }
```

Close/reload the dialog/list safely for this stale-state case; never present raw error internals directly to the user.

## DESIGN2 visual requirements

- Use a centered level-3 dialog or a destructive bottom sheet with 20dp radius, white surface and ambient shadow.
- Source name is Plus Jakarta Sans headline-sm; explanatory text uses Inter 14sp/22sp slate.
- Cancel is a slate ghost button. Remove is an explicit error-red destructive action with white label and 44dp minimum height.
- Destructive color is reserved only for confirmed removal; closing/back/cancel always remains neutral.

## Rules and done

- [ ] Cancel is the non-destructive default and closes without store change.
- [ ] Confirm disables controls, handles failure in the dialog and then updates S06 on success.
- [ ] The user can never remove a different source because stale list state was reused; pass immutable source id/name.
