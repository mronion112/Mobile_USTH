# Thứ tự triển khai Frontend

## G0 — Design + API client

FE Lead chốt Jetpack Compose hoặc XML Views (khuyến nghị Compose), tạo base API URL, direct HTTP client, nullable DTOs và navigation shell. Áp dụng token trong `DESIGN2.md` trước khi tạo screen riêng.

**Bàn giao:** bottom navigation 4 tab, top utility bar, `ApiEnvelope<T>`, loading/error mapping, preview/mock response cho UI.

## G1 — Shared editorial UI

Hoàn thành `Frontend/00-shared-components`: featured/compact article card, chips, search input, source status card, button, loading/empty/error states.

**Bàn giao:** S01–S05 dùng một ArticleCard; S06–S09 dùng một SourceCard/status style. Không copy component sang feature khác.

## G2 — Source operations

S06–S09 gọi external API trực tiếp: source list, validate/create, remove, refresh/status. Render đúng `ACTIVE`, `FETCHING`, `ERROR` response và giữ input khi API lỗi.

## G3 — Reading operations

S01–S05 gọi API và render response: Home, Detail, Search, Category, Bookmarks. Link gốc mở browser intent; `imageUrl=null` hiển thị placeholder.

## G4 — Visual QA and demo

Đối chiếu cả S01–S09 với `DESIGN2.md`: color roles, typography, 16–20dp cards, 64dp bottom nav, 44–48dp primary actions, null/loading/empty/error state và screenshots demo.
