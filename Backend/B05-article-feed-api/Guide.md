# B05 — Home, article detail and category feed APIs

**Owner:** Nguyễn Hoàng Anh  
**Depends on:** B01, B02, B03 output.

## Endpoints

```http
GET /api/v1/articles?page=0&size=20&categoryId=&sourceId=
GET /api/v1/articles/{id}
GET /api/v1/categories/{categoryId}/articles?page=0&size=20
```

## Query rules

- Page is zero-based; default size 20, maximum 100.
- Newest-first using `publishedAt DESC`; define stable fallback for missing dates.
- Filters compose with AND. Invalid category/source returns defined `RESOURCE_NOT_FOUND` or documented empty result—choose one and keep consistent.
- Detail returns only RSS data retained by B03 and the original `link`; it does not scrape full article content.

## DTO

Return article fields in the common contract, including nested source/category and `isBookmarked` according to the G0 bookmark decision.

## Done when

- [ ] Pagination meta, sort and filtering are integration-tested.
- [ ] Missing optional RSS fields serialize safely as null.
- [ ] S01, S02 and S04 owners have tested JSON examples.
