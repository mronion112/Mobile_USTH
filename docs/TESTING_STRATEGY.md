# Frontend testing strategy

- UI/unit test cho từng S01–S09: success, loading, empty, error, retry, null `imageUrl`, null description/date và direct API failure.
- Fake/mock HTTP client tạo JSON response đúng `src/API_NOTES.md`; không gọi Dân trí/API production trong test tự động.
- Test interaction: category/source filters, bookmark toggle, remove confirmation, add/validate source, refresh status và original-link intent.
- Visual QA: screenshot 360–480dp đối chiếu `DESIGN2.md`; kiểm tra color role, readable contrast, card radius, typography hierarchy, touch target >=44dp và bottom navigation safe area.
- Mỗi feature cập nhật case đã test/chưa test trong Guide của chính nó.
