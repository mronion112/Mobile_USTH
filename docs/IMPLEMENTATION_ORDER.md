# Thứ tự triển khai Frontend

## G0 — Design + local store

FE Lead chốt Jetpack Compose hoặc XML Views (khuyến nghị Compose), tạo local store (Room), on-device RSS fetcher, nullable model và navigation shell. Áp dụng token trong `DESIGN2.md` trước khi tạo screen riêng.

**Bàn giao:** bottom navigation 4 tab, model local dùng chung, loading/error mapping, preview/mock dữ liệu cho UI.

## G1 — Shared editorial UI

Hoàn thành `src/00-shared-components`: featured/compact article card, chips, search input, source status card, button, loading/empty/error states.

**Bàn giao:** S01–S05 dùng một ArticleCard; S06–S09 dùng một SourceCard/status style. Không copy component sang feature khác.

## G2 — Source operations

S06–S09 đọc/ghi local store kết hợp fetch feed trên thiết bị: source list, validate/create, remove, refresh/status. Render đúng `ACTIVE`, `FETCHING`, `ERROR` và giữ input khi fetch lỗi.

## G3 — Reading operations

S01–S05 đọc local store và render: Home, Detail, Search, Category, Bookmarks. Link gốc mở browser intent; `imageUrl=null` hiển thị placeholder.

## G4 — Visual QA and demo

Đối chiếu cả S01–S09 với `DESIGN2.md`: color roles, typography, 16–20dp cards, 64dp bottom nav, 44–48dp primary actions, null/loading/empty/error state và screenshots demo.
