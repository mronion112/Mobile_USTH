# Frontend — Kotlin Android feature scaffold

Frontend được chia theo S01–S09 để mỗi owner làm độc lập. App Kotlin tự fetch RSS trực tiếp trên thiết bị, lưu local và render trên màn hình; repository này không chứa server module và không phụ thuộc dịch vụ mạng ngoài. Khi nhóm khởi tạo project Android, mỗi folder map sang một package/module tương ứng; không cần đổi ID screen.

```text
00-app-shell/             navigation, theme, local store, on-device RSS fetcher
00-shared-components/     ArticleCard, ArticleList, state UI, model mapping
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

Mỗi `Guide.md` định nghĩa UI, data dependency, event và definition of done. Các folder S01–S09 không được tạo model riêng; dùng model local chung do `00-app-shell` công bố.

Thiết kế bắt buộc: [DESIGN2_IMPLEMENTATION.md](DESIGN2_IMPLEMENTATION.md), chuyển đổi trực tiếp từ [DESIGN2.md](../../DESIGN2.md).

Demo visual/reference: [Vietnam News](https://stitch.withgoogle.com/preview/5420956129683526122?node-id=5e5e9a12953f49129d1ef8c1987b4ed8). Nguyễn Quang Vũ Hoàng phụ trách CI/CD GitHub cố định, không nhận screen.
