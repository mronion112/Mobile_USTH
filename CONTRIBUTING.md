# Contributing

## Yêu cầu

- Android Studio bản mới nhất, JDK 21.
- Không commit `local.properties`, keystore, `.env`, token.

## Bắt đầu nhanh

```fish
git clone https://github.com/mronion112/Mobile_USTH.git
cd Mobile_USTH
./gradlew assembleDebug
./gradlew testDebugUnitTest lintDebug
python3 scripts/ci/check-docs.py
```

## Nhánh và commit

- Một nhánh cho một screen/UI: `feature/s01-home-feed`, `feature/s06-rss-sources`, `fix/article-image-fallback`.
- Một commit cho một thay đổi logic; message theo Conventional Commits, chữ thường, động từ mệnh lệnh, tối đa 72 ký tự, ví dụ `feat(s01): add category filter chips`.

## Quy trình PR chuẩn

1. Đồng bộ `main` mới nhất, tạo nhánh từ `main`.
2. Một PR hoàn thành đúng một mục tiêu trong `Guide.md` của screen; không gộp refactor lớn với thay đổi visual/rendering.
3. Dùng component và model dùng chung của `00-app-shell` và `00-shared-components`; không copy UI hay model riêng cho từng screen.
4. Tự kiểm tra trước khi mở PR:
   - Build, unit test và lint pass tại máy.
   - Đủ các state loading, empty, error, retry; `imageUrl`/`description`/`publishedAt` null có fallback.
   - Màn 360–480dp đúng token `DESIGN2.md`: màu, radius, spacing, touch target tối thiểu 44dp.
   - Test chỉ dùng RSS fixture giả và store giả; không gọi feed production.
5. Mở PR vào `main`, điền đầy đủ template: Feature, mục tiêu và phạm vi, data/design impact, kiểm thử, handover. Đính screenshot khi thay đổi visual khó kiểm tra bằng code, hoặc khi reviewer yêu cầu.
6. CI phải xanh: `docs-validate`, `android-build` (cả hai matrix `assemble` và `verify`), `security-scan`.
7. Mức review do team quyết theo phạm vi PR: PR nhỏ, rõ ràng có thể merge nhanh; PR chạm navigation, model dùng chung hoặc visual hệ thống cần FE Lead review. Sửa theo review bằng commit mới trên cùng nhánh.
8. Chỉ merge khi CI xanh và đạt mức review team đã chốt. Nên xóa nhánh sau khi merge để repo gọn (GitHub có nút Delete branch hoặc bật auto-delete); giữ lại nếu team cần đối chiếu.

## Báo lỗi

Mở issue ghi rõ: thiết bị/emulator, bản build, bước tái hiện, kết quả mong đợi và thực tế, screenshot nếu liên quan UI.
