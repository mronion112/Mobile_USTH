# RSS News Reader — Kotlin Frontend

Đây là khung **frontend-only** cho Android Kotlin. Ứng dụng tự fetch các RSS feed đã đăng ký trực tiếp trên thiết bị, lưu local và render các màn S01–S09; repository không chứa server module và không phụ thuộc dịch vụ mạng ngoài.

```text
.
├── app/             # Kotlin Android module: Compose UI, data layer, tests
├── docs/            # Workflow, guides, thứ tự triển khai, test strategy
│   ├── guides/      # Guide.md cho từng screen và shared UI
│   └── mockups/     # Wireframe/mockup trước khi code, không phải ảnh sản phẩm
└── DESIGN2.md       # Design system nguồn: Editorial Pulse
```

## Nguồn thiết kế và dữ liệu

- [DESIGN2.md](DESIGN2.md) là nguồn đúng cho màu, typography, spacing, elevation và component style.
- [Vietnam News — product demo](https://stitch.withgoogle.com/preview/5420956129683526122?node-id=5e5e9a12953f49129d1ef8c1987b4ed8) là reference chính cho layout, nhịp tương tác và cảm giác sản phẩm khi build UI.
- Mỗi screen render trực tiếp từ local store qua repository/ViewModel của app. App tự fetch và parse RSS/XML trên thiết bị, không gọi dịch vụ mạng ngoài và không tạo server layer giả trong repository.

## Bắt đầu triển khai

1. Đọc [guides README](docs/guides/README.md), [design implementation](docs/guides/DESIGN2_IMPLEMENTATION.md) và [implementation order](docs/IMPLEMENTATION_ORDER.md).
2. Chọn Jetpack Compose hoặc XML Views một lần trong `docs/guides/00-app-shell`; ưu tiên Compose cho design system này.
3. Mỗi người code một S-folder/PR, dùng component và model chung, không copy UI hay model.
4. Đối chiếu state loading, empty, error, null image/link với `Guide.md` của screen trước khi handover.

## Phân công cuối cùng theo screen

| ID | Screen | Owner | Folder |
| --- | --- | --- | --- |
| S01 | Home News Feed | Nguyễn Tiến Đạt | `docs/guides/S01-home-news-feed` |
| S02 | Article Detail | Nguyễn Văn Đức | `docs/guides/S02-article-detail` |
| S03 | Search & Filter | Nguyễn Thị Mai Linh | `docs/guides/S03-search-filter` |
| S04 | Category Feed | Nguyễn Tiến Đạt | `docs/guides/S04-category-feed` |
| S05 | Bookmarks | Nguyễn Thị Mai Linh | `docs/guides/S05-bookmarks` |
| S06 | RSS Sources | Nguyễn Hoàng Anh | `docs/guides/S06-rss-sources` |
| S07 | Add RSS Source | Vũ Minh Hoàng | `docs/guides/S07-add-rss-source` |
| S08 | Remove Confirmation | Nguyễn Hoàng Anh | `docs/guides/S08-remove-source-confirmation` |
| S09 | Fetch / Empty / Error State | Nguyễn Hoàng Minh | `docs/guides/S09-fetch-status-states` |

**Nguyễn Quang Vũ Hoàng** phụ trách cố định CI/CD GitHub, không nhận task screen.

Model local dùng chung và data flow: [app-shell Guide](docs/guides/00-app-shell/Guide.md).
