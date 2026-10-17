# API contract — entry point

Contract chi tiết ở [api-contract.md](api-contract.md). Hai owner BE và FE Lead phải xác nhận các mục sau bằng PR/comment trước G3:

- [ ] Chọn `Jetpack Compose` hoặc XML Views cho Android MVP.
- [ ] Chọn PostgreSQL hoặc MySQL; migration versioned là bắt buộc.
- [ ] Bookmark là backend theo demo account hay local-only.
- [ ] Page tính từ `0`, default `size=20`, sort newest-first.
- [ ] URL RSS từng chuyên mục Dân trí đã được xác minh; không seed URL phỏng đoán.
- [ ] Các field RSS thiếu ảnh/ngày/mô tả trả `null`; FE có fallback tương ứng.
- [ ] Delete source chỉ ngừng fetch mới, không xóa bài cũ.

Sau khi chốt, Backend owner copy JSON request/response đã test vào `api-contract.md` và tag tất cả FE owners.
