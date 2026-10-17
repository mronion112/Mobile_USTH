# Frontend — Kotlin Android feature scaffold

Frontend được chia theo S01–S09 để mỗi owner làm độc lập. Khi FE Lead tạo project Android (Compose hoặc XML sau G0), mỗi folder này map sang một package/module tương ứng; không cần đổi ID screen.

```text
00-app-shell/             navigation, theme, DI/API bootstrap
00-shared-components/     ArticleCard, ArticleList, state UI, DTO mapping
S01-home-news-feed/       Nguyễn Tiến Đạt
S02-article-detail/       Nguyễn Văn Đức
S03-search-filter/        Nguyễn Thị Mai Linh
S04-category-feed/        Nguyễn Tiến Đạt
S05-bookmarks/            Nguyễn Thị Mai Linh
S06-rss-sources/          Nguyễn Văn Đức
S07-add-rss-source/       Nguyễn Văn Đức
S08-remove-source-confirmation/ Nguyễn Văn Đức
S09-fetch-status-states/  FE Lead + từng owner
```

Mỗi `Guide.md` định nghĩa UI, API dependency, event và definition of done. Các folder S01–S09 không được tạo Retrofit DTO riêng; dùng contract/model chung do `00-app-shell` công bố.

Kotlin-side request/response mapping và screen → endpoint: [API_NOTES.md](API_NOTES.md).
