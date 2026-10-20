# Frontend workflow

## Branch và PR

- Một branch theo screen/UI: `feature/s01-home-feed`, `feature/s06-rss-sources`, `fix/article-image-fallback`.
- Một PR chỉ hoàn thành một mục tiêu trong `Guide.md`; không gộp refactor lớn với thay đổi visual/API rendering.
- FE Lead review navigation, shared components, local model/nullability và DESIGN2 compliance.

## Data handover

1. Nhóm chốt danh sách RSS feed mẫu, model local dùng chung và quy tắc fetch nền.
2. FE owner cập nhật guide screen liên quan trước khi thay model.
3. App fetch feed trực tiếp trên thiết bị qua fetcher chung; dữ liệu được map thành UI state ngay trong feature/ViewModel.

## Daily integration

- G0/G1: App shell và shared editorial components.
- G2: S06–S09 sources/status.
- G3: S01–S05 reading/search/bookmarks.
- Trước demo, từng owner đối chiếu acceptance boxes + DESIGN2 visual checklist trong Guide.
