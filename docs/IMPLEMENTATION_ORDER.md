# Thứ tự triển khai và điểm bàn giao

## G0 — Chốt contract

FE Lead + BE Lead chốt Compose/XML, database, bookmark storage, pagination và `docs/api-contract.md`. Không tích hợp network trước khi xong G0.

## G1 — Nền tảng song song

- FE: `Frontend/00-app-shell`, `Frontend/00-shared-components`.
- BE: `Backend/B00-platform-contracts`, `Backend/B01-data-model-migrations`, `Backend/B02-category-api`.

**Bàn giao:** navigation shell, health API, migration chạy được, categories có JSON example.

## G2 — RSS source pipeline

- BE: `B03-rss-ingestion`, `B04-source-management`, `B07-scheduler-fetch-status`.
- FE: S06–S09 làm mock theo contract.

**Bàn giao:** validate/add/refresh/remove; parser dedupe; Active/Fetching/Error state.

## G3 — Luồng đọc

- BE: `B05-article-feed-api`, `B06-search-bookmark-api`.
- FE: S01–S05 tích hợp API thật.

**Bàn giao:** Home → Detail → link gốc; Search/Filter; Category; Bookmark.

## G4 — Nghiệm thu

`B08-quality-integration` kiểm thử invalid RSS, duplicate, source lỗi không chặn nguồn khác, loading/empty/error state, cấu hình chạy và demo screenshots.

## Rules dependency

S01/S03/S04/S05 bắt buộc tái sử dụng `ArticleCard` và `ArticleList` của `00-shared-components`. S06–S08 dùng source/status DTO thống nhất. FE chỉ gọi REST API; RSS XML chỉ có ở B03.
