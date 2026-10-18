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
- App tự gọi REST API được nhóm chọn. Endpoint/payload hiện có ở [API reference](docs/api-contract.md); khi API ngoài thay đổi, cập nhật tài liệu FE cùng PR.
- Mỗi screen render trực tiếp từ API response qua client/ViewModel của app. Không parse RSS XML trong mobile và không tạo server layer giả trong repository.

## Bắt đầu triển khai

1. Đọc [Frontend README](Frontend/README.md), [design implementation](Frontend/DESIGN2_IMPLEMENTATION.md) và [implementation order](docs/IMPLEMENTATION_ORDER.md).
2. Chọn Jetpack Compose hoặc XML Views một lần trong `Frontend/00-app-shell`; ưu tiên Compose cho design system này.
3. Mỗi người code một S-folder/PR, dùng component và model chung, không copy UI hay DTO.
4. Đối chiếu state loading, empty, error, null image/link với `Guide.md` của screen trước khi handover.

## Screen ownership

| Nhóm | Folder chính | Người phụ trách theo kế hoạch |
| --- | --- | --- |
| Home & Category | `Frontend/S01-*`, `Frontend/S04-*` | Nguyễn Tiến Đạt |
| Detail & RSS Sources | `Frontend/S02-*`, `Frontend/S06-*`–`S09-*` | Nguyễn Văn Đức |
| Search & Bookmarks | `Frontend/S03-*`, `Frontend/S05-*` | Nguyễn Thị Mai Linh |

Request/response, Kotlin nullable mapping và từng endpoint: [Frontend API notes](Frontend/API_NOTES.md).
