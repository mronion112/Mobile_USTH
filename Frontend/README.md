# Frontend — Kotlin Android feature scaffold

Frontend được chia theo S01–S09 để mỗi owner làm độc lập. App Kotlin gọi REST API bên ngoài trực tiếp và render response trên màn hình; repository này không chứa server module. Khi nhóm khởi tạo project Android, mỗi folder map sang một package/module tương ứng; không cần đổi ID screen.

```text
00-app-shell/             navigation, theme, DI/API bootstrap
00-shared-components/     ArticleCard, ArticleList, state UI, DTO mapping
S01-home-news-feed/       Nguyễn Tiến Đạt
S02-article-detail/       Nguyễn Văn Đức
S03-search-filter/        Nguyễn Thị Mai Linh
S04-category-feed/        Nguyễn Tiến Đạt
S05-bookmarks/            Nguyễn Thị Mai Linh
S06-rss-sources/          Nguyễn Hoàng Anh
S07-add-rss-source/       Vũ Minh Hoàng
S08-remove-source-confirmation/ Nguyễn Hoàng Anh
S09-fetch-status-states/  Nguyễn Hoàng Minh
```

Mỗi `Guide.md` định nghĩa UI, API dependency, event và definition of done. Các folder S01–S09 không được tạo DTO riêng; dùng direct API client/model chung do `00-app-shell` công bố.

Kotlin-side request/response mapping và screen → endpoint: [API_NOTES.md](API_NOTES.md).

Thiết kế bắt buộc: [DESIGN2_IMPLEMENTATION.md](DESIGN2_IMPLEMENTATION.md), chuyển đổi trực tiếp từ [DESIGN2.md](../DESIGN2.md).

Demo visual/reference: [Vietnam News](https://stitch.withgoogle.com/preview/5420956129683526122?node-id=5e5e9a12953f49129d1ef8c1987b4ed8). Nguyễn Quang Vũ Hoàng phụ trách CI/CD GitHub cố định, không nhận screen.
