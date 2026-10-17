# B03 — RSS ingestion, validation and deduplication

**Owner:** Vũ Minh Hoàng  
**Depends on:** B00, B01; B04 calls this feature.

## Responsibility

```text
RSS/Atom URL → safe HTTP fetch → parse XML → normalize article
→ dedupe by source+guid (fallback link) → persist/upsert → source status
```

Support RSS 2.0 and Atom fields where present: title, description/content, link, image, guid/id and published date. Absence of image/description/date is valid.

## Validation endpoint

```http
POST /api/v1/sources/validate
Content-Type: application/json

{ "rssUrl": "https://dantri.com.vn/rss/the-gioi.rss" }
```

Return `valid`, detected feed title/site name and optional suggested category; failures use `RSS_FEED_INVALID` or `RSS_FETCH_FAILED` without raw stack traces.

## Security and reliability rules

- Reject non-HTTP(S), localhost, loopback, private/link-local and cloud metadata addresses (SSRF protection), including redirects.
- Set connect/read timeout and maximum response-size limit.
- Use secure XML configuration: disable DTD/external entity resolution (XXE).
- One malformed item may be skipped/logged; distinguish it from a feed-level failure.
- Never scrape the original article page; only parse the submitted RSS/Atom XML.

## Done when

- [ ] RSS, Atom, missing optional field, malformed XML, XXE and duplicate tests exist.
- [ ] Re-fetch cannot create duplicate article rows.
- [ ] Client test uses mock HTTP server, not live Dân trí network.
