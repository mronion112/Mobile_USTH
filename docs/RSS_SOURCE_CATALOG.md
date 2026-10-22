# RSS source catalog

The checked-in catalog is [rss_sources.json](../app/src/main/assets/rss_sources.json). It is the only list of default RSS links distributed with the app; no backend is required to render or manage S06/S08.

After this branch is pushed, collaborators can use its raw file directly:

```text
https://raw.githubusercontent.com/mronion112/Mobile_USTH/NguyenHoangAnh/app/src/main/assets/rss_sources.json
```

Use the `main` branch in that URL after the change is merged.

## Runtime behavior

1. On a fresh install, the app copies the bundled JSON asset to its private file `filesDir/rss_sources.json`.
2. S06 reads the private file through `JsonRssSourceRepository`.
3. S07 can add a validated, non-duplicate HTTP(S) RSS URL through the same repository; it becomes a `FETCHING` source and is written to the private JSON file.
4. S08 confirms a removal using the immutable source id, then writes the updated source list back to that private JSON file atomically.
5. Refresh status transitions are written to the same file, so the list survives process restart. Existing articles and bookmarks are deliberately untouched.

The bundled asset is a reproducible seed. Once a user changes their list, the local copy takes precedence so an app update never silently restores a deleted source.

## JSON schema

```json
{
  "schemaVersion": 1,
  "sources": [
    {
      "id": 1,
      "name": "Dân trí — Thời sự",
      "rssUrl": "https://dantri.com.vn/rss/thoi-su.rss",
      "category": { "id": "thoi-su", "name": "Thời sự", "colorIndex": 5 },
      "status": "ACTIVE",
      "lastFetchedAt": "2026-10-22T14:58:00Z",
      "lastError": null,
      "createdAt": "2026-10-06T09:00:00Z"
    }
  ]
}
```

`status` is one of `ACTIVE`, `FETCHING`, `ERROR`, or `DISABLED`. Timestamps are ISO-8601 UTC strings; `lastFetchedAt` and `lastError` may be `null`.
