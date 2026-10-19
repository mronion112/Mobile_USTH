# S01–S09 — Final assignment by screen

| ID | Screen | Owner | Requirement | Folder |
| --- | --- | --- | --- | --- |
| S01 | Home News Feed (Hiển thị bài viết mới) | Nguyễn Tiến Đạt | Hiển thị tin mới nhất, Category Tabs, Source Filter, Refresh, Article List và trạng thái Loading/Empty/Error. | `src/S01-home-news-feed` |
| S02 | Article Detail (Xem chi tiết một bài RSS) | Nguyễn Văn Đức | Hiển thị tiêu đề, ảnh, mô tả RSS, nguồn tin, chuyên mục, thời gian đăng; mở bài gốc và thao tác Bookmark. | `src/S02-article-detail` |
| S03 | Search & Filter (Tìm kiếm và lọc bài) | Nguyễn Thị Mai Linh | Tìm kiếm theo tiêu đề/mô tả, lọc theo Category/Source, sắp xếp tin mới nhất và xóa bộ lọc. | `src/S03-search-filter` |
| S04 | Category Feed (Xem bài theo chuyên mục) | Nguyễn Tiến Đạt | Hiển thị tin theo chuyên mục, Refresh, phân trang và tái sử dụng Article List/Card từ S01. | `src/S04-category-feed` |
| S05 | Bookmarks (Xem các bài đã lưu) | Nguyễn Thị Mai Linh | Hiển thị bài đã lưu, mở chi tiết, bỏ lưu và xử lý danh sách trống. | `src/S05-bookmarks` |
| S06 | RSS Sources (Quản lý các nguồn RSS) | Nguyễn Hoàng Anh | Hiển thị danh sách nguồn RSS, URL, chuyên mục, trạng thái, thời gian cập nhật; hỗ trợ Refresh và xóa nguồn. | `src/S06-rss-sources` |
| S07 | Add RSS Source (Thêm một RSS feed) | Vũ Minh Hoàng | Nhập RSS URL, xác thực, xem trước metadata, chọn chuyên mục và thêm nguồn mới. | `src/S07-add-rss-source` |
| S08 | Remove Confirmation (Xác nhận xóa Source) | Nguyễn Hoàng Anh | Xây dựng hộp thoại xác nhận, xử lý Cancel/Confirm và cập nhật danh sách sau khi xóa. | `src/S08-remove-source-confirmation` |
| S09 | Fetch / Empty / Error State (Trạng thái cập nhật và lỗi) | Nguyễn Hoàng Minh | Xây dựng UI trạng thái đang cập nhật, không có dữ liệu, lỗi API/RSS và Retry; hiển thị thông báo lỗi rõ ràng. | `src/S09-fetch-status-states` |

**Nguyễn Quang Vũ Hoàng** phụ trách cố định CI/CD GitHub, không nhận task screen.

Visual reference: [Vietnam News demo](https://stitch.withgoogle.com/preview/5420956129683526122?node-id=5e5e9a12953f49129d1ef8c1987b4ed8), [DESIGN2.md](../DESIGN2.md) và các mockup PNG ở root. Open the matching `Guide.md` before implementation; `docs/api-contract.md` is the direct API data reference.
