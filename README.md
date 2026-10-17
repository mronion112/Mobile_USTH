# RSS News Reader — Team Repository Scaffold

Đây là **khung phân chia công việc**, không phải app đã code sẵn. Mỗi feature có folder độc lập và file `Guide.md` mô tả phần cần làm, dependency và tiêu chí nghiệm thu. Cấu trúc này học theo cách quản lý backend của `Web_USTH`, nhưng Android FE vẫn dành riêng cho Kotlin.

```text
.
├── Frontend/        # Kotlin Android: S01–S09, shell và component chung
├── Backend/         # Java Spring Boot: B00–B08, mỗi capability một folder
├── docs/            # Contract, integration order, test strategy
└── *.png            # Mockup UI tham chiếu do nhóm cung cấp
```

## Quy tắc bắt đầu

1. Đọc [docs/IMPLEMENTATION_ORDER.md](docs/IMPLEMENTATION_ORDER.md).
2. Mỗi người chỉ code trong folder feature được giao; tạo PR nhỏ theo feature.
3. Backend owner chốt [docs/API_CONTRACT_DRAFT.md](docs/API_CONTRACT_DRAFT.md) trước khi FE tích hợp.
4. Mỗi screen FE bám mockup PNG cùng tên ở root; không tự tạo field ngoài contract.
5. Mỗi feature hoàn thành phải cập nhật `Guide.md`: endpoint/payload thật, test đã chạy và limitation còn lại.

Request/response đầy đủ: [API contract](docs/api-contract.md), [ghi chú Backend](Backend/API_NOTES.md) và [ghi chú Kotlin FE](Frontend/API_NOTES.md).

## Phân công từ tài liệu gốc

| Nhóm | Folder chính | Người phụ trách theo kế hoạch |
| --- | --- | --- |
| FE Home & Category | `Frontend/S01-*`, `Frontend/S04-*` | Nguyễn Tiến Đạt |
| FE Detail & Sources | `Frontend/S02-*`, `Frontend/S06-*`–`S09-*` | Nguyễn Văn Đức |
| FE Search & Bookmarks | `Frontend/S03-*`, `Frontend/S05-*` | Nguyễn Thị Mai Linh |
| BE RSS ingestion | `Backend/B03-rss-ingestion` | Vũ Minh Hoàng |
| BE Article APIs | `Backend/B05-*`, `B06-*` | Nguyễn Hoàng Anh |
| BE DB/source/bookmark/scheduler | `Backend/B01-*`, `B02-*`, `B04-*`, `B07-*` | Nguyễn Hoàng Minh |
| Integration/CI | `Backend/B08-*`, `.github/` | Nguyễn Quang Vũ Hoàng |

## Reference workflow adopted from Web_USTH

- Every backend capability owns a `Guide.md`, modeled after the Web project’s feature guides.
- Database schema/migration is source-of-truth; no automatic `ddl-auto=update` on integration branches.
- Development/demo data must be explicitly labeled mock; test cases create only their deltas using fixtures.
- CI separates build/test and contract/infrastructure checks. The exact pipeline can be added in `Backend/B08-quality-integration` after the stack is initialized.
