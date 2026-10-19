# RSS News Reader — Kotlin Frontend

Đây là khung **frontend-only** cho Android Kotlin. Ứng dụng gọi REST API bên ngoài trực tiếp, nhận JSON response và render các màn S01–S09; repository không chứa server module.

```text
.
├── Frontend/        # Kotlin Android: App shell, shared UI và S01–S09
├── docs/            # API reference, workflow và test strategy cho FE
├── DESIGN2.md       # Design system nguồn: Editorial Pulse
└── *.png            # Wireframe/màn demo do nhóm cung cấp
```

## Nguồn thiết kế và dữ liệu

- [DESIGN2.md](DESIGN2.md) là nguồn đúng cho màu, typography, spacing, elevation và component style.
- [Vietnam News — product demo](https://stitch.withgoogle.com/preview/5420956129683526122?node-id=5e5e9a12953f49129d1ef8c1987b4ed8) là reference chính cho layout, nhịp tương tác và cảm giác sản phẩm khi build UI.
- App tự gọi REST API được nhóm chọn. Endpoint/payload hiện có ở [API reference](docs/api-contract.md); khi API ngoài thay đổi, cập nhật tài liệu FE cùng PR.
- Mỗi screen render trực tiếp từ API response qua client/ViewModel của app. Không parse RSS XML trong mobile và không tạo server layer giả trong repository.

## Bắt đầu triển khai

1. Đọc [Frontend README](Frontend/README.md), [design implementation](Frontend/DESIGN2_IMPLEMENTATION.md) và [implementation order](docs/IMPLEMENTATION_ORDER.md).
2. Chọn Jetpack Compose hoặc XML Views một lần trong `Frontend/00-app-shell`; ưu tiên Compose cho design system này.
3. Mỗi người code một S-folder/PR, dùng component và model chung, không copy UI hay DTO.
4. Đối chiếu state loading, empty, error, null image/link với `Guide.md` của screen trước khi handover.

## Phân công cuối cùng theo screen

| ID | Screen | Owner | Folder |
| --- | --- | --- | --- |
| S01 | Home News Feed | Nguyễn Tiến Đạt | `Frontend/S01-home-news-feed` |
| S02 | Article Detail | Nguyễn Văn Đức | `Frontend/S02-article-detail` |
| S03 | Search & Filter | Nguyễn Thị Mai Linh | `Frontend/S03-search-filter` |
| S04 | Category Feed | Nguyễn Tiến Đạt | `Frontend/S04-category-feed` |
| S05 | Bookmarks | Nguyễn Thị Mai Linh | `Frontend/S05-bookmarks` |
| S06 | RSS Sources | Nguyễn Hoàng Anh | `Frontend/S06-rss-sources` |
| S07 | Add RSS Source | Vũ Minh Hoàng | `Frontend/S07-add-rss-source` |
| S08 | Remove Confirmation | Nguyễn Hoàng Anh | `Frontend/S08-remove-source-confirmation` |
| S09 | Fetch / Empty / Error State | Nguyễn Hoàng Minh | `Frontend/S09-fetch-status-states` |

**Nguyễn Quang Vũ Hoàng** phụ trách cố định CI/CD GitHub, không nhận task screen.

Request/response, Kotlin nullable mapping và từng endpoint: [Frontend API notes](Frontend/API_NOTES.md).
