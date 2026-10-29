# Cheat sheet thuyết trình — Mobile USTH RSS News Reader

Mục tiêu: tìm nhanh đúng file và dòng code để giải thích giao diện, sự kiện bấm, RSS và lưu dữ liệu.

Lý thuyết : View, Activity, Fragment, Multiview
Dựa trên 6 case :

4 Case ( FE )
 + Color : Text, Background, Button
 + Change Icon :
 + Text Family :
 + Change Position :

2 case ( BE )
 + Function Find :
 + Database :



## 1. Nhìn nhanh cấu trúc project

- Đây là app **Android Kotlin + Jetpack Compose**. UI được viết bằng hàm `@Composable`, không phải layout XML.
- Điểm bắt đầu: [MainActivity.kt:11-21](app/src/main/java/vn/edu/usth/rssreader/MainActivity.kt#L11-L21) — `onCreate()` gọi `setContent`, áp dụng `MobileUSTHTheme`, sau đó mở `AppNavHost()`.
- Điều hướng màn hình: [AppNavHost.kt:62-146](app/src/main/java/vn/edu/usth/rssreader/ui/navigation/AppNavHost.kt#L62-L146).
- Các màn Home, Explore, Saved, RSS nằm trong `app/src/main/java/vn/edu/usth/rssreader/ui/screen/`.
- **Lưu ý khi tìm màn bài viết chi tiết:** màn này ở [ui/screen/detail](app/src/main/java/vn/edu/usth/rssreader/ui/screen/detail), cùng module với các màn khác: [ArticleDetailScreen.kt:73-120](app/src/main/java/vn/edu/usth/rssreader/ui/screen/detail/ArticleDetailScreen.kt#L73-L120).
- Repository đang dùng là app chạy thật. Thư mục `docs/guides/` và `docs/mockups/` chỉ chứa Guide/wireframe, không phải màn hình runtime.

### Bản đồ đường đi của dữ liệu

```text
MainActivity
  → AppNavHost / Destination (chọn màn hình)
  → Screen Compose (vẽ UI, nối sự kiện)
  → ViewModel (xử lý thao tác, giữ UiState)
  → Repository (RSS + JSON cục bộ)
  → StateFlow cập nhật lại UI
```

Repository dùng chung được tạo trong [RssReaderApplication.kt:7-13](app/src/main/java/vn/edu/usth/rssreader/RssReaderApplication.kt#L7-L13).

### Phân biệt file Screen và file chứa function xử lý

- **Screen** là file `...Screen.kt`: tìm hàm đầu tiên dạng `@Composable fun ...Screen(...)`. Đây là phần vẽ màn hình và nối các nút/sự kiện. Android app này viết UI bằng Compose nên không có một file XML riêng cho từng màn.
- **ViewModel** là file cùng thư mục, tên `...ViewModel.kt`: thường chứa hàm xử lý như `onSyncClick`, `onQueryChange`, `saveNewSource`; cũng giữ `uiState` mà Screen đang hiển thị.
- **Component** là các file con trong thư mục `components/`: từng mảnh UI, ví dụ card bài báo, ô tìm kiếm, nút danh mục. Nếu muốn đổi riêng card thì sửa component; nếu muốn đổi bố cục cả màn thì sửa Screen.
- **AppNavHost.kt** đăng ký route để mở Screen. Nếu cần biết bấm tab nào mở màn nào thì xem [AppNavHost.kt:113-143](app/src/main/java/vn/edu/usth/rssreader/ui/navigation/AppNavHost.kt#L113-L143); nếu cần tên/icon tab thì xem [Destination.kt:10-39](app/src/main/java/vn/edu/usth/rssreader/ui/navigation/Destination.kt#L10-L39).

| Tab/màn | File UI (điểm bắt đầu) | File ViewModel (xử lý) | Ví dụ function |
|---|---|---|---|
| Home | [HomeScreen.kt:48-60](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/HomeScreen.kt#L48-L60) — `HomeScreen()` | [HomeViewModel.kt:22-26](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/HomeViewModel.kt#L22-L26) | Đồng bộ [onSyncClick():170](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/HomeViewModel.kt#L170-L172); chọn category [dòng 156](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/HomeViewModel.kt#L156-L159); bookmark [dòng 161](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/HomeViewModel.kt#L161-L168). |
| Explore | [ExploreScreen.kt:58-77](app/src/main/java/vn/edu/usth/rssreader/ui/screen/explore/ExploreScreen.kt#L58-L77) — `ExploreScreen()` | [ExploreViewModel.kt:17-23](app/src/main/java/vn/edu/usth/rssreader/ui/screen/explore/ExploreViewModel.kt#L17-L23) | Tìm [onQueryChange():65](app/src/main/java/vn/edu/usth/rssreader/ui/screen/explore/ExploreViewModel.kt#L65-L68); chọn category [dòng 70](app/src/main/java/vn/edu/usth/rssreader/ui/screen/explore/ExploreViewModel.kt#L70-L73); tải thêm [dòng 75](app/src/main/java/vn/edu/usth/rssreader/ui/screen/explore/ExploreViewModel.kt#L75-L80). |
| Saved | [SavedScreen.kt:62-76](app/src/main/java/vn/edu/usth/rssreader/ui/screen/saved/SavedScreen.kt#L62-L76) — `SavedScreen()` | [SavedViewModel.kt:18-26](app/src/main/java/vn/edu/usth/rssreader/ui/screen/saved/SavedViewModel.kt#L18-L26) | Lọc category, đổi sort, bỏ lưu: [SavedViewModel.kt:47-58](app/src/main/java/vn/edu/usth/rssreader/ui/screen/saved/SavedViewModel.kt#L47-L58). |
| Nguồn RSS | [RssScreen.kt:64-76](app/src/main/java/vn/edu/usth/rssreader/ui/screen/rss/RssScreen.kt#L64-L76) — `RssScreen()` | [RssSourcesViewModel.kt:21-27](app/src/main/java/vn/edu/usth/rssreader/ui/screen/rss/RssSourcesViewModel.kt#L21-L27) | Thêm nguồn [dòng 103](app/src/main/java/vn/edu/usth/rssreader/ui/screen/rss/RssSourcesViewModel.kt#L103-L157)/[159](app/src/main/java/vn/edu/usth/rssreader/ui/screen/rss/RssSourcesViewModel.kt#L159-L193); refresh [dòng 195](app/src/main/java/vn/edu/usth/rssreader/ui/screen/rss/RssSourcesViewModel.kt#L195-L206); xóa [dòng 208](app/src/main/java/vn/edu/usth/rssreader/ui/screen/rss/RssSourcesViewModel.kt#L208-L223)/[225](app/src/main/java/vn/edu/usth/rssreader/ui/screen/rss/RssSourcesViewModel.kt#L225-L265). |
| Chi tiết bài | [ArticleDetailScreen.kt:73-120](app/src/main/java/vn/edu/usth/rssreader/ui/screen/detail/ArticleDetailScreen.kt#L73-L120) — `ArticleDetailScreen()` | [ArticleDetailViewModel.kt:20-40](app/src/main/java/vn/edu/usth/rssreader/ui/screen/detail/ArticleDetailViewModel.kt#L20-L40) | Nạp bài [loadArticle():34](app/src/main/java/vn/edu/usth/rssreader/ui/screen/detail/ArticleDetailViewModel.kt#L34-L52); bookmark [toggleBookmark():54](app/src/main/java/vn/edu/usth/rssreader/ui/screen/detail/ArticleDetailViewModel.kt#L54-L65). |

Màn **Category Feed** hiện chưa có route/screen riêng trong app runtime. Bấm danh mục hiện lọc nội dung ngay trên Home hoặc Explore. Để xác nhận route đang chạy, xem [AppNavHost.kt:113-146](app/src/main/java/vn/edu/usth/rssreader/ui/navigation/AppNavHost.kt#L113-L146).

### Ví dụ lần theo một nút từ màn hình tới function

Lấy nút đồng bộ trên Home:

1. Mở [HomeScreen.kt:73-81](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/HomeScreen.kt#L73-L81). Ở [dòng 78](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/HomeScreen.kt#L78-L78) thấy `onSyncClick = viewModel::onSyncClick`. Dấu `::` nghĩa là đưa function `onSyncClick` làm callback cho component `GreetingRow`.
2. Mở [HomeViewModel.kt:170-189](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/HomeViewModel.kt#L170-L189). `onSyncClick()` gọi `refreshWithFeedback()`, hàm này gọi `articleRepository.refreshAll()` rồi phát toast kết quả.
3. Mở [JsonArticleRepository.kt:63-88](app/src/main/java/vn/edu/usth/rssreader/data/repository/JsonArticleRepository.kt#L63-L88) để xem repository tải lại feed; request mạng ở [dòng 164-187](app/src/main/java/vn/edu/usth/rssreader/data/repository/JsonArticleRepository.kt#L164-L187), ghi/cache bài ở [dòng 188-217](app/src/main/java/vn/edu/usth/rssreader/data/repository/JsonArticleRepository.kt#L188-L217).
4. Kết quả được phát qua `StateFlow`; Home đang nghe danh sách bài tại [HomeViewModel.kt:54-60](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/HomeViewModel.kt#L54-L60), Compose nhận state ở [HomeScreen.kt:54](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/HomeScreen.kt#L54-L54) và vẽ lại.

Khi mò một nút bất kỳ, làm giống vậy: **tìm nút trong `...Screen.kt` → xem `onClick`/`onSomething` nó nối tới đâu → mở tên đó trong `...ViewModel.kt` → nếu thấy `repository.` thì mở file Repository để xem lưu/tải dữ liệu**. Có thể nhấn Ctrl/Cmd + click vào tên hàm để nhảy tới nơi khai báo.

## 2. Thầy hỏi: muốn đổi màu, hình, icon, chữ thì sửa ở đâu?

### Đổi màu toàn app

Mở [Color.kt](app/src/main/java/vn/edu/usth/rssreader/ui/theme/Color.kt#L14-L44):

- Màu thương hiệu: [dòng 14-22](app/src/main/java/vn/edu/usth/rssreader/ui/theme/Color.kt#L14-L22), ví dụ `Primary` ở [dòng 15](app/src/main/java/vn/edu/usth/rssreader/ui/theme/Color.kt#L15-L15).
- Màu nền/thẻ: [dòng 24-28](app/src/main/java/vn/edu/usth/rssreader/ui/theme/Color.kt#L24-L28), ví dụ `SurfaceCanvas` ở [dòng 25](app/src/main/java/vn/edu/usth/rssreader/ui/theme/Color.kt#L25-L25).
- Màu chữ: [dòng 30-35](app/src/main/java/vn/edu/usth/rssreader/ui/theme/Color.kt#L30-L35).
- Màu trạng thái thành công/lỗi: [dòng 37-41](app/src/main/java/vn/edu/usth/rssreader/ui/theme/Color.kt#L37-L41).

Các tên màu này được dùng lại trong nhiều màn. Đổi một token như `Primary` sẽ đổi mọi chỗ đang dùng token đó. Theme ánh xạ các màu sang Material ở [Theme.kt:14-44](app/src/main/java/vn/edu/usth/rssreader/ui/theme/Theme.kt#L14-L44).

Nếu chỉ muốn đổi đúng **một nút/thành phần**, mở Composable của thành phần đó và sửa `containerColor`, `color`, `tint`, hoặc `Modifier.background(...)`. Ví dụ nút thêm RSS ở [RssScreen.kt:186-212](app/src/main/java/vn/edu/usth/rssreader/ui/screen/rss/RssScreen.kt#L186-L212); màu icon tab được tính tại [AppBottomNav.kt:73](app/src/main/java/vn/edu/usth/rssreader/ui/components/AppBottomNav.kt#L73-L73).

### Đổi chữ đậm, cỡ chữ, nền, khoảng cách

Trong Compose, tìm thành phần `Text(...)` hoặc `Button(...)` bên trong file UI:

- `fontWeight = FontWeight.Bold` / `SemiBold`: độ đậm.
- `fontSize = ...sp`: cỡ chữ.
- `color = ...`: màu chữ.
- `Modifier.background(...)`: màu nền.
- `padding(...)`, `height(...)`, `size(...)`: khoảng cách/kích thước.

Ví dụ tiêu đề trong danh sách tin Home: [NewsListItem.kt:91-99](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/components/NewsListItem.kt#L91-L99); nền card ở [dòng 51-57](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/components/NewsListItem.kt#L51-L57). Typography dùng chung khai báo ở [Type.kt:10-18](app/src/main/java/vn/edu/usth/rssreader/ui/theme/Type.kt#L10-L18), nhưng một số text đặt kiểu trực tiếp trong từng Composable.

### Đổi icon menu dưới cùng

- Tên/nhãn và icon ứng với từng tab: [Destination.kt:10-39](app/src/main/java/vn/edu/usth/rssreader/ui/navigation/Destination.kt#L10-L39). Ví dụ Home chọn `Icons.Filled.Home` ở [dòng 20](app/src/main/java/vn/edu/usth/rssreader/ui/navigation/Destination.kt#L20-L20).
- Cách vẽ, màu tab được chọn và màu tab chưa chọn: [AppBottomNav.kt:34-66](app/src/main/java/vn/edu/usth/rssreader/ui/components/AppBottomNav.kt#L34-L66) và [dòng 68-102](app/src/main/java/vn/edu/usth/rssreader/ui/components/AppBottomNav.kt#L68-L102).
- Bấm tab gọi `onNavigate(destination)` tại [AppBottomNav.kt:78-85](app/src/main/java/vn/edu/usth/rssreader/ui/components/AppBottomNav.kt#L78-L85); nơi định tuyến các tab là [AppNavHost.kt:73-88](app/src/main/java/vn/edu/usth/rssreader/ui/navigation/AppNavHost.kt#L73-L88) và [dòng 113-143](app/src/main/java/vn/edu/usth/rssreader/ui/navigation/AppNavHost.kt#L113-L143).

Muốn thay icon: đổi `icon = Icons.Filled...` trong `Destination.kt`. Muốn đổi màu khi chọn/bỏ chọn: sửa `tint` ở [AppBottomNav.kt:73](app/src/main/java/vn/edu/usth/rssreader/ui/components/AppBottomNav.kt#L73-L73), hoặc đổi token `Primary`/`TextMuted` trong `Color.kt`.

### Đổi ảnh bài báo

Ảnh bài báo không phải icon trong menu. Card nhận URL từ `article.imageUrl`:

- Ảnh bài nổi bật: [FeaturedNewsCard.kt:80-90](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/components/FeaturedNewsCard.kt#L80-L90) (`AsyncImage`).
- Ảnh bài trong danh sách: [NewsListItem.kt:126-140](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/components/NewsListItem.kt#L126-L140).
- Dữ liệu ảnh thuộc model [Article.kt:5-19](app/src/main/java/vn/edu/usth/rssreader/data/model/Article.kt#L5-L19).

Với RSS thật, ảnh thường được lấy từ RSS khi parse bài; parser ở [RssArticleParser.kt](app/src/main/java/vn/edu/usth/rssreader/data/parser/RssArticleParser.kt). Nếu thay `AsyncImage` thì đổi cách hiển thị ảnh; nếu muốn đổi **ảnh của một bài cụ thể**, phải đổi `imageUrl` ở dữ liệu nguồn/parser tương ứng. `FakeNewsData.kt` và `FakeExploreData.kt` chứa ảnh mẫu chủ yếu cho Preview/demo.

### Ví dụ cụ thể: muốn sửa màn Home

Mở [HomeScreen.kt:48-180](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/HomeScreen.kt#L48-L180). File này sắp xếp các khối Home:

- Header: `GreetingRow` — [dòng 73-81](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/HomeScreen.kt#L73-L81).
- Thanh tìm kiếm: `HomeSearchBar` — [dòng 87-93](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/HomeScreen.kt#L87-L93).
- Danh mục: `CategoryStrip` — [dòng 97-104](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/HomeScreen.kt#L97-L104).
- Tin nổi bật: `FeaturedSection` — [dòng 128-142](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/HomeScreen.kt#L128-L142).
- Tin mới: `LatestSectionHeader` + danh sách lazy — [dòng 146-163](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/HomeScreen.kt#L146-L163).
- Thanh trạng thái RSS: `RssStatusBar` — [dòng 172-175](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/HomeScreen.kt#L172-L175).

Muốn đổi chi tiết của một khối thì mở component tương ứng ở `ui/screen/home/components/` (ví dụ `FeaturedNewsCard.kt`, `NewsListItem.kt`, `GreetingRow.kt`). Muốn đổi **màu nền toàn màn Home**, xem [HomeScreen.kt:63-67](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/HomeScreen.kt#L63-L67).

## 3. Thầy hỏi: function xử lý tính năng nằm đâu, trả về gì?

UI và xử lý được tách đôi: Screen nhận `uiState` và nối callback; ViewModel xử lý callback rồi gọi Repository. Các hàm `on...` trong ViewModel thường trả về `Unit` (không trả dữ liệu cho UI ngay); chúng khởi chạy coroutine, cập nhật `StateFlow` hoặc phát thông báo. Kết quả hiển thị quay lại qua `uiState`/event.

| Tính năng | UI nối sự kiện | Hàm xử lý / kết quả |
|---|---|---|
| Home: chọn danh mục | [HomeScreen.kt:97-104](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/HomeScreen.kt#L97-L104) | `HomeViewModel.onCategorySelected(categoryId)` — cập nhật state và lọc bài; [HomeViewModel.kt:156-159](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/HomeViewModel.kt#L156-L159). |
| Home: lưu/bỏ lưu bài | [HomeScreen.kt:153-163](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/HomeScreen.kt#L153-L163) | `onToggleBookmark(article, bookmarked)` gọi `articleRepository.toggleBookmark`; [HomeViewModel.kt:161-168](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/HomeViewModel.kt#L161-L168). Repository trả `Boolean` là trạng thái sau khi bật/tắt; interface ở [ArticleRepository.kt:24](app/src/main/java/vn/edu/usth/rssreader/data/repository/ArticleRepository.kt#L24-L24). |
| Home: đồng bộ/làm mới bài | [HomeScreen.kt:73-81](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/HomeScreen.kt#L73-L81), [146-163](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/HomeScreen.kt#L146-L163) | `onSyncClick()` / `onRefreshClick()` gọi `refreshWithFeedback()`; [HomeViewModel.kt:170-189](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/HomeViewModel.kt#L170-L189). Hàm gọi `articleRepository.refreshAll()`; phát thông báo như “Đã cập nhật N bài mới”, lỗi gần nhất, hoặc “Không có bài mới”. |
| Tìm kiếm | [ExploreScreen.kt:58-77](app/src/main/java/vn/edu/usth/rssreader/ui/screen/explore/ExploreScreen.kt#L58-L77) | `ExploreViewModel.onQueryChange(q)` cập nhật query rồi lọc danh sách; [ExploreViewModel.kt:65-68](app/src/main/java/vn/edu/usth/rssreader/ui/screen/explore/ExploreViewModel.kt#L65-L68), logic lọc/tìm tại [dòng 93-145](app/src/main/java/vn/edu/usth/rssreader/ui/screen/explore/ExploreViewModel.kt#L93-L145). |
| Màn Đã lưu: lọc/sắp xếp/bỏ lưu | [SavedScreen.kt:62-76](app/src/main/java/vn/edu/usth/rssreader/ui/screen/saved/SavedScreen.kt#L62-L76) | `SavedViewModel.onCategorySelected`, `onSortModeChange`, `onUnsave`; [SavedViewModel.kt:47-58](app/src/main/java/vn/edu/usth/rssreader/ui/screen/saved/SavedViewModel.kt#L47-L58). |
| Thêm nguồn RSS | [RssScreen.kt:122-131](app/src/main/java/vn/edu/usth/rssreader/ui/screen/rss/RssScreen.kt#L122-L131) | `RssSourcesViewModel.validateNewSource()` kiểm tra URL; `saveNewSource()` gọi repository; [RssSourcesViewModel.kt:103-193](app/src/main/java/vn/edu/usth/rssreader/ui/screen/rss/RssSourcesViewModel.kt#L103-L193). |
| Làm mới một nguồn RSS | [RssScreen.kt:135-160](app/src/main/java/vn/edu/usth/rssreader/ui/screen/rss/RssScreen.kt#L135-L160) | `RssSourcesViewModel.refresh(sourceId)` gọi `repository.refresh(sourceId)`; [RssSourcesViewModel.kt:195-206](app/src/main/java/vn/edu/usth/rssreader/ui/screen/rss/RssSourcesViewModel.kt#L195-L206). |
| Xóa nguồn RSS | [RssScreen.kt:112-121](app/src/main/java/vn/edu/usth/rssreader/ui/screen/rss/RssScreen.kt#L112-L121) | `requestRemoval()` mở xác nhận, `confirmRemoval()` gọi repository rồi phát event; [RssSourcesViewModel.kt:208-265](app/src/main/java/vn/edu/usth/rssreader/ui/screen/rss/RssSourcesViewModel.kt#L208-L265). |

### Luồng fetch RSS (đây là chỗ nên mở khi thầy hỏi “fetch ở đâu?”)

1. Tab RSS nối nút làm mới vào ViewModel tại [RssScreen.kt:135-160](app/src/main/java/vn/edu/usth/rssreader/ui/screen/rss/RssScreen.kt#L135-L160).
2. `RssSourcesViewModel.refresh()` gọi `repository.refresh(sourceId)` tại [RssSourcesViewModel.kt:195-206](app/src/main/java/vn/edu/usth/rssreader/ui/screen/rss/RssSourcesViewModel.kt#L195-L206).
3. `JsonRssSourceRepository.refresh()` ở [RssSourceRepository.kt:164-172](app/src/main/java/vn/edu/usth/rssreader/data/repository/RssSourceRepository.kt#L164-L172) chuyển nguồn sang `FETCHING`, rồi `completeFetch()` kiểm tra URL/feed và lưu trạng thái `ACTIVE` hoặc `ERROR` tại [dòng 212-245](app/src/main/java/vn/edu/usth/rssreader/data/repository/RssSourceRepository.kt#L212-L245). Kết quả hàm là `SourceActionResult` (Success / NotFound / Failure), định nghĩa ở [dòng 43-47](app/src/main/java/vn/edu/usth/rssreader/data/repository/RssSourceRepository.kt#L43-L47).
4. Khi nguồn active, `JsonArticleRepository.observeSources()` phát hiện tại [JsonArticleRepository.kt:132-157](app/src/main/java/vn/edu/usth/rssreader/data/repository/JsonArticleRepository.kt#L132-L157), gọi `fetchAndMerge()` ở [dòng 158-163](app/src/main/java/vn/edu/usth/rssreader/data/repository/JsonArticleRepository.kt#L158-L163).
5. Request HTTP tải XML ở [JsonArticleRepository.kt:164-187](app/src/main/java/vn/edu/usth/rssreader/data/repository/JsonArticleRepository.kt#L164-L187); `RssArticleParser` parse thành `List<Article>`. Sau đó merge bài theo id và ghi cache ở [dòng 188-217](app/src/main/java/vn/edu/usth/rssreader/data/repository/JsonArticleRepository.kt#L188-L217).
6. `HomeViewModel` đang collect danh sách bài từ repository tại [HomeViewModel.kt:54-60](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/HomeViewModel.kt#L54-L60), cập nhật `HomeUiState`; Compose quan sát state nên màn tự vẽ lại.

**Cách nói gọn:** “ViewModel nhận thao tác người dùng; Repository làm I/O. Nguồn RSS được kiểm tra và đổi trạng thái trước, sau đó ArticleRepository mới tải và parse XML thành Article.”

**Kiểu kết quả khi thêm nguồn:** `RssFeedInspector.validate()` trả `RssFeedValidationResult.Valid(detectedTitle, detectedSiteName)` hoặc `Invalid(message)` ([RssFeedInspector.kt:10-17](app/src/main/java/vn/edu/usth/rssreader/data/repository/RssFeedInspector.kt#L10-L17)). `RssSourceRepository.add()` trả `AddSourceResult.Created(source)`, `Duplicate`, hoặc `Failure(message)` ([RssSourceRepository.kt:55-59](app/src/main/java/vn/edu/usth/rssreader/data/repository/RssSourceRepository.kt#L55-L59), [dòng 125-160](app/src/main/java/vn/edu/usth/rssreader/data/repository/RssSourceRepository.kt#L125-L160)). `saveNewSource()` đọc các nhánh đó và cập nhật UI/event ở [RssSourcesViewModel.kt:159-193](app/src/main/java/vn/edu/usth/rssreader/ui/screen/rss/RssSourcesViewModel.kt#L159-L193). Nhớ phân biệt: callback ViewModel thường là `Unit`; kết quả có ích nằm ở lời gọi Repository/state/event.

## 4. Thầy hỏi: data lưu đâu, quản lý và lấy ra thế nào?

App này **không có backend riêng**. Nó tải RSS trực tiếp trên điện thoại và lưu cache vào bộ nhớ riêng của app.

| Dữ liệu | Nơi lưu / nguồn | Đọc và cập nhật |
|---|---|---|
| Danh mục RSS mặc định | File đóng gói cùng app: [rss_sources.json](app/src/main/assets/rss_sources.json) | Dùng làm seed khi cài app lần đầu. Repository copy vào file riêng của app. |
| Danh sách RSS người dùng thêm/xóa, trạng thái fetch | `filesDir/rss_sources.json` trong bộ nhớ riêng ứng dụng | `JsonRssSourceRepository` đọc/ghi JSON; khởi tạo file từ asset nếu chưa có. Xem [RssSourceRepository.kt:66-80](app/src/main/java/vn/edu/usth/rssreader/data/repository/RssSourceRepository.kt#L66-L80), đọc/ghi tại [dòng 255-299](app/src/main/java/vn/edu/usth/rssreader/data/repository/RssSourceRepository.kt#L255-L299), add/remove tại [dòng 125-186](app/src/main/java/vn/edu/usth/rssreader/data/repository/RssSourceRepository.kt#L125-L186). |
| Các bài đã tải từ feed | `filesDir/articles.json` | [JsonArticleRepository.kt:30-38](app/src/main/java/vn/edu/usth/rssreader/data/repository/JsonArticleRepository.kt#L30-L38), nạp cache ở [dòng 123-131](app/src/main/java/vn/edu/usth/rssreader/data/repository/JsonArticleRepository.kt#L123-L131), lưu bài ở [dòng 199-217](app/src/main/java/vn/edu/usth/rssreader/data/repository/JsonArticleRepository.kt#L199-L217). |
| Các bài đã bookmark | `filesDir/bookmarks.json` | Tương tự, đọc/ghi ở [JsonArticleRepository.kt:218-237](app/src/main/java/vn/edu/usth/rssreader/data/repository/JsonArticleRepository.kt#L218-L237); thao tác toggle ở [dòng 89-105](app/src/main/java/vn/edu/usth/rssreader/data/repository/JsonArticleRepository.kt#L89-L105). |
| State đang hiển thị | RAM qua `StateFlow` | Repository phát `StateFlow`; ViewModel collect và tạo `UiState`; Screen collect state để vẽ. Ví dụ [HomeViewModel.kt:22-52](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/HomeViewModel.kt#L22-L52), [HomeScreen.kt:48-60](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/HomeScreen.kt#L48-L60). |

Trong [RssSourceRepository.kt:68](app/src/main/java/vn/edu/usth/rssreader/data/repository/RssSourceRepository.kt#L68-L68), `catalogFile` trỏ tới `File(appContext.filesDir, "rss_sources.json")`. Trong [JsonArticleRepository.kt:37-38](app/src/main/java/vn/edu/usth/rssreader/data/repository/JsonArticleRepository.kt#L37-L38), hai file cache là `articles.json` và `bookmarks.json`. `filesDir` là vùng riêng theo app, không phải file nằm trong thư mục source code của project.

Nếu cần chỉ vị trí dữ liệu trên thiết bị debug, có thể mở **Android Studio → View → Tool Windows → Device Explorer**, rồi tìm vùng riêng của package `vn.edu.usth.rssreader`, thư mục `files/`. Đây là dữ liệu trên điện thoại/emulator; nó không sửa các file JSON trong project.

Đường đi dữ liệu RSS: `rss_sources.json` → fetch URL → parser → `Article` → `articles.json` → `StateFlow` → ViewModel → màn hình. Model bài báo nằm ở [Article.kt:5-19](app/src/main/java/vn/edu/usth/rssreader/data/model/Article.kt#L5-L19); model nguồn RSS ở [RssSource.kt:10-26](app/src/main/java/vn/edu/usth/rssreader/data/model/RssSource.kt#L10-L26).

### Phân biệt dữ liệu thật và dữ liệu mẫu

- Home runtime lấy danh sách bài từ `articleRepository`; xem [HomeViewModel.kt:24-26](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/HomeViewModel.kt#L24-L26), [54-60](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/HomeViewModel.kt#L54-L60), [121-150](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/HomeViewModel.kt#L121-L150).
- `FakeArticles`, `FakeExploreTrending`, `FakeExploreArticles` được dùng trong `@Preview` để xem mẫu UI; ví dụ [FeaturedNewsCard.kt:288](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/components/FeaturedNewsCard.kt#L288-L299), [ExploreScreen.kt:329](app/src/main/java/vn/edu/usth/rssreader/ui/screen/explore/ExploreScreen.kt#L329-L350), [SavedScreen.kt:359](app/src/main/java/vn/edu/usth/rssreader/ui/screen/saved/SavedScreen.kt#L359-L387).
- `FakeCategories` còn được dùng làm danh mục “Tất cả”/danh mục gợi ý, không có nghĩa toàn bộ bài đang hiển thị là fake.
- `ArticleDetailViewModel` có ghép dữ liệu repository với các bài fake để tìm bài theo id: [ArticleDetailViewModel.kt:84](app/src/main/java/vn/edu/usth/rssreader/ui/screen/detail/ArticleDetailViewModel.kt#L84-L85). Nếu thầy hỏi riêng màn chi tiết thì nói rõ nó có fallback dữ liệu mẫu.

## 5. Câu hỏi: sửa code thì điện thoại có đổi ngay không?

**Chỉ lưu file code thì chưa đổi trên điện thoại.** Điện thoại đang chạy APK đã build/cài trước đó. Muốn xem bản sửa:

1. Kết nối điện thoại Android bằng USB, bật **Developer options → USB debugging**, mở khóa và chấp nhận hộp thoại cho phép debug nếu hiện ra.
2. Mở project bằng Android Studio, chọn đúng điện thoại ở thanh thiết bị.
3. Nhấn **Run ▶ app** để Android Studio build và cài/chạy bản debug mới lên điện thoại. Nếu chỉ sửa một vài thay đổi UI, có thể thử **Apply Changes**; nếu không cập nhật hoặc build lỗi thì dùng Run lại.

Khi cài đè bản debug cùng package, dữ liệu local thường được giữ. Nếu gỡ app rồi cài lại thì Android xóa vùng dữ liệu riêng của app, gồm RSS đã thêm, cache bài và bookmark.

Máy ảo Android cũng dùng được. Chỉ khi IDE cài bản build mới (hoặc Apply Changes hỗ trợ loại thay đổi đó) thì giao diện trên máy mới đổi; nó không tự đồng bộ theo từng lần bạn gõ code.

**Riêng file RSS mặc định:** lần chạy đầu repository copy [rss_sources.json](app/src/main/assets/rss_sources.json) sang `filesDir/rss_sources.json`. Sau đó bản copy cục bộ được ưu tiên để giữ nguồn người dùng đã chỉnh. Vì vậy sửa asset rồi bấm Run có thể **không ghi đè** danh sách RSS đã có trên điện thoại. Có thể dùng chức năng “Khôi phục mặc định” trong app; thao tác này thay danh sách RSS cục bộ bằng danh sách mặc định.

Muốn build/cài qua terminal (đã có Android SDK và thiết bị được `adb` nhận):

```bash
./gradlew installDebug
```

## 6. Danh sách file nên mở sẵn khi thuyết trình

1. [MainActivity.kt](app/src/main/java/vn/edu/usth/rssreader/MainActivity.kt#L11-L21) — app bắt đầu ở đâu: [dòng 11-21](app/src/main/java/vn/edu/usth/rssreader/MainActivity.kt#L11-L21).
2. [AppNavHost.kt](app/src/main/java/vn/edu/usth/rssreader/ui/navigation/AppNavHost.kt#L62-L146) — màn hình/điều hướng: [dòng 62-146](app/src/main/java/vn/edu/usth/rssreader/ui/navigation/AppNavHost.kt#L62-L146).
3. [Destination.kt](app/src/main/java/vn/edu/usth/rssreader/ui/navigation/Destination.kt#L10-L39) — tên tab và icon: [dòng 10-39](app/src/main/java/vn/edu/usth/rssreader/ui/navigation/Destination.kt#L10-L39).
4. [HomeScreen.kt](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/HomeScreen.kt#L48-L180) — các khối giao diện Home: [dòng 48-180](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/HomeScreen.kt#L48-L180).
5. [HomeViewModel.kt](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/HomeViewModel.kt#L22-L189) — state và xử lý bấm Home: [dòng 22-52](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/HomeViewModel.kt#L22-L52), [121-189](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/HomeViewModel.kt#L121-L189).
6. [JsonArticleRepository.kt](app/src/main/java/vn/edu/usth/rssreader/data/repository/JsonArticleRepository.kt#L30-L105) — fetch RSS, cache bài và bookmark: [dòng 30-105](app/src/main/java/vn/edu/usth/rssreader/data/repository/JsonArticleRepository.kt#L30-L105), [132-198](app/src/main/java/vn/edu/usth/rssreader/data/repository/JsonArticleRepository.kt#L132-L198), [199-247](app/src/main/java/vn/edu/usth/rssreader/data/repository/JsonArticleRepository.kt#L199-L247).
7. [RssSourcesViewModel.kt](app/src/main/java/vn/edu/usth/rssreader/ui/screen/rss/RssSourcesViewModel.kt#L21-L265) — validate/add/refresh/remove: [dòng 103-265](app/src/main/java/vn/edu/usth/rssreader/ui/screen/rss/RssSourcesViewModel.kt#L103-L265).
8. [RssSourceRepository.kt](app/src/main/java/vn/edu/usth/rssreader/data/repository/RssSourceRepository.kt#L66-L80) — nguồn RSS lưu JSON: [dòng 66-80](app/src/main/java/vn/edu/usth/rssreader/data/repository/RssSourceRepository.kt#L66-L80), [125-186](app/src/main/java/vn/edu/usth/rssreader/data/repository/RssSourceRepository.kt#L125-L186), [255-280](app/src/main/java/vn/edu/usth/rssreader/data/repository/RssSourceRepository.kt#L255-L280).
9. [Color.kt](app/src/main/java/vn/edu/usth/rssreader/ui/theme/Color.kt#L14-L44) + [AppBottomNav.kt](app/src/main/java/vn/edu/usth/rssreader/ui/components/AppBottomNav.kt#L34-L102) — màu và menu dưới: [Color.kt:14-44](app/src/main/java/vn/edu/usth/rssreader/ui/theme/Color.kt#L14-L44), [AppBottomNav.kt:34-102](app/src/main/java/vn/edu/usth/rssreader/ui/components/AppBottomNav.kt#L34-L102).

Trong Android Studio: dùng **Search Everywhere** (nhấn `Shift` hai lần) hoặc **Navigate → File** để gõ tên file; bật **View → Appearance → Show Line Numbers** để chỉ đúng dòng. Tên hàm có thể tìm nhanh bằng `Ctrl+Shift+F` (Windows/Linux) hoặc `⌘+Shift+F` (macOS).

## 7. Bài nói mẫu ngắn

> “App viết bằng Kotlin và Jetpack Compose. `MainActivity` mở `AppNavHost`, nơi điều hướng giữa các màn. Screen chỉ dựng giao diện và nối callback; ViewModel xử lý thao tác, gọi Repository và phát state. Với RSS, app tải feed trực tiếp trên thiết bị, parse thành model `Article`, lưu cache JSON vào vùng riêng của app rồi cập nhật UI qua `StateFlow`. Màu dùng chung nằm trong `Color.kt`, còn từng screen và component quyết định bố cục cụ thể.”

## 8. Các chỗ còn TODO

- Nút “Xem tất cả” của khối tin nổi bật trên Home còn `TODO`, chưa mở màn danh sách riêng: [HomeScreen.kt:137](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/HomeScreen.kt#L137-L137).
- Nút filter ở màn Explore hiện nối callback rỗng (`onFilterClick = { }`): [ExploreScreen.kt:114](app/src/main/java/vn/edu/usth/rssreader/ui/screen/explore/ExploreScreen.kt#L114-L114).
