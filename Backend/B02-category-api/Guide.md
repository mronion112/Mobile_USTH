# B02 — Category API

**Owner:** Nguyễn Hoàng Anh  
**Depends on:** B00, B01.

## Endpoint

```http
GET /api/v1/categories
```

## Success data

```json
[
  { "id": 2, "name": "Thế giới", "slug": "the-gioi" },
  { "id": 9, "name": "Công nghệ", "slug": "cong-nghe" }
]
```

## Rules

- Return stable IDs, Vietnamese display name and immutable unique slug.
- Default order must be deliberate and documented; FE needs consistent chip order.
- This endpoint is a dependency for S01, S03, S04, S05 and S07.

## Done when

- [ ] DTO uses shared envelope and no entity serialization.
- [ ] Empty database behavior is documented.
- [ ] Integration test covers ordering and contract fields.
