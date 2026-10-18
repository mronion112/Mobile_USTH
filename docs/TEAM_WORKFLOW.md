# Frontend workflow

## Branch và PR

- Một branch theo screen/UI: `feature/s01-home-feed`, `feature/s06-rss-sources`, `fix/article-image-fallback`.
- Một PR chỉ hoàn thành một mục tiêu trong `Guide.md`; không gộp refactor lớn với thay đổi visual/API rendering.
- FE Lead review navigation, shared components, API model/nullability và DESIGN2 compliance.

## External API handover

1. Người phụ trách API ngoài cung cấp base URL, auth requirement và JSON response thật.
2. FE owner cập nhật `docs/api-contract.md`, `Frontend/API_NOTES.md` và guide screen liên quan trước khi thay model.
3. App gọi API trực tiếp qua client chung; response được map thành UI state ngay trong feature/ViewModel.

## Daily integration

- G0/G1: App shell và shared editorial components.
- G2: S06–S09 sources/status.
- G3: S01–S05 reading/search/bookmarks.
- Trước demo, từng owner đối chiếu acceptance boxes + DESIGN2 visual checklist trong Guide.
