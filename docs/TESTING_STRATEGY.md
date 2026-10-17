# Testing strategy

Áp dụng các quy tắc phù hợp từ Web_USTH cho RSS Reader:

- Unit test: parser RSS/Atom, URL validation/SSRF, normalization, dedupe, mapper và filter specification.
- Integration test: controller + database test; mock HTTP RSS server, không gọi Dân trí thật trong CI.
- UI test: từng S01–S09 có success/loading/empty/error, bookmark toggle, remove confirmation và original-link intent.
- Demo data không được dùng làm assertion duy nhất; fixture chỉ tạo khác biệt của case test.
- Test source fail không được ngắt scheduler của source còn lại.
- Mỗi feature cập nhật test case/chưa test trong `Guide.md` của chính nó.
