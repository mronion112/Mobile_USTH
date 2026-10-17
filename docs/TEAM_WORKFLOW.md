# Team workflow

## Branch và PR

- Một branch theo feature: `feature/s01-home-feed`, `feature/b03-rss-parser`, `fix/b06-bookmark-state`.
- Một PR chỉ nên làm một mục tiêu trong `Guide.md`; không gộp refactor lớn với thay đổi contract.
- FE Lead review mọi PR Frontend. Backend owner của feature review thay đổi API/schema.
- CI phải pass trước merge khi B08 đã được triển khai.

## Contract-first handover

1. BE owner mở PR thay đổi `docs/api-contract.md`, kèm JSON success/error thật.
2. FE Lead xác nhận nullable field, pagination và error behavior.
3. Sau khi merge, FE thay mock repository bằng API repository; không tự đổi tên field ở mobile.

## Database

- Migration là append-only, có version và reviewer.
- Không xóa source/article để sửa dữ liệu demo; test/seed dùng fixture có chủ đích.
- `rssUrl` và dữ liệu RSS là external/untrusted input: validate trước khi fetch.

## Daily integration

- G1: integrate B00/B01/B02 + app shell.
- G2: integrate source flow S06–S09 with B03/B04/B07.
- G3: integrate reader flow S01–S05 with B05/B06.
- Before demo, each owner walks the acceptance boxes in their `Guide.md`.
