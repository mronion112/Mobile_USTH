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
3. The add-source sheet validates an HTTP(S) RSS/Atom URL on the device before it can be saved. A valid, non-duplicate source becomes `FETCHING`, is written to the private JSON file, then completes an on-device XML fetch to `ACTIVE` or `ERROR`.
4. S08 confirms a removal using the immutable source id, then writes the updated source list back to that private JSON file atomically.
5. Manual refresh first persists `FETCHING`, makes a real HTTP/XML RSS or Atom request on the device, then persists `ACTIVE` with a timestamp or `ERROR` with a safe message. An interrupted `FETCHING` request is resumed at the next app launch.
6. A corrupt local JSON file is shown as a recoverable error rather than an empty source list. The user can retry reading it or explicitly restore the bundled default catalog (which replaces their local source changes).

The manifest keeps cleartext traffic enabled solely so legacy `http://` RSS feeds can be validated and refreshed; new feeds should prefer `https://` whenever the publisher provides it.

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
