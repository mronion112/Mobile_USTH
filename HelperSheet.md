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
- Điểm bắt đầu: `app/src/main/java/vn/edu/usth/rssreader/MainActivity.kt:11-19` — `onCreate()` gọi `setContent`, áp dụng `MobileUSTHTheme`, sau đó mở `AppNavHost()`.
- Điều hướng màn hình: `app/src/main/java/vn/edu/usth/rssreader/ui/navigation/AppNavHost.kt:52-128`.
- Các màn Home, Explore, Saved, RSS nằm trong `app/src/main/java/vn/edu/usth/rssreader/ui/screen/`.
- **Lưu ý khi tìm màn bài viết chi tiết:** màn này nằm ở `src/S02-article-detail/`, không nằm cạnh các màn khác. `app/build.gradle.kts:35-40` thêm thư mục đó vào source set để Android build được.
- Repository đang dùng là app chạy thật. Thư mục `src/` còn có Guide và code S02; đừng nhầm các file Guide/wireframe với màn hình runtime.

### Bản đồ đường đi của dữ liệu

```text
MainActivity
  → AppNavHost / Destination (chọn màn hình)
  → Screen Compose (vẽ UI, nối sự kiện)
  → ViewModel (xử lý thao tác, giữ UiState)
  → Repository (RSS + JSON cục bộ)
  → StateFlow cập nhật lại UI
```

Repository dùng chung được tạo trong `app/src/main/java/vn/edu/usth/rssreader/RssReaderApplication.kt:7-14`.

### Phân biệt file Screen và file chứa function xử lý

- **Screen** là file `...Screen.kt`: tìm hàm đầu tiên dạng `@Composable fun ...Screen(...)`. Đây là phần vẽ màn hình và nối các nút/sự kiện. Android app này viết UI bằng Compose nên không có một file XML riêng cho từng màn.
- **ViewModel** là file cùng thư mục, tên `...ViewModel.kt`: thường chứa hàm xử lý như `onSyncClick`, `onQueryChange`, `saveNewSource`; cũng giữ `uiState` mà Screen đang hiển thị.
- **Component** là các file con trong thư mục `components/`: từng mảnh UI, ví dụ card bài báo, ô tìm kiếm, nút danh mục. Nếu muốn đổi riêng card thì sửa component; nếu muốn đổi bố cục cả màn thì sửa Screen.
- **AppNavHost.kt** đăng ký route để mở Screen. Nếu cần biết bấm tab nào mở màn nào thì xem `AppNavHost.kt:91-112`; nếu cần tên/icon tab thì xem `Destination.kt:10-39`.

| Tab/màn | File UI (điểm bắt đầu) | File ViewModel (xử lý) | Ví dụ function |
|---|---|---|---|
| Home | `ui/screen/home/HomeScreen.kt:47-53` — `HomeScreen()` | `ui/screen/home/HomeViewModel.kt:22-26` | Đồng bộ `onSyncClick()` dòng 170; chọn category dòng 156; bookmark dòng 161. |
| Explore | `ui/screen/explore/ExploreScreen.kt:57-77` — `ExploreScreen()` | `ui/screen/explore/ExploreViewModel.kt:17-23` | Tìm `onQueryChange()` dòng 65; chọn category dòng 70; tải thêm dòng 75. |
| Saved | `ui/screen/saved/SavedScreen.kt:65-80` — `SavedScreen()` | `ui/screen/saved/SavedViewModel.kt:18-26` | Lọc category, đổi sort, bỏ lưu: `SavedViewModel.kt:47-58`. |
| Nguồn RSS | `ui/screen/rss/RssScreen.kt:61-65` — `RssScreen()` | `ui/screen/rss/RssSourcesViewModel.kt:21-27` | Thêm nguồn dòng 103/159; refresh dòng 195; xóa dòng 208/225. |
| Chi tiết bài | `src/S02-article-detail/ArticleDetailScreen.kt:72-177` — `ArticleDetailScreen()` | `src/S02-article-detail/ArticleDetailViewModel.kt:20-86` | Nạp bài `loadArticle()` dòng 34; bookmark `toggleBookmark()` dòng 54. |

Màn **Category Feed** hiện chưa có route/screen riêng trong app runtime. Bấm danh mục hiện lọc nội dung ngay trên Home hoặc Explore. Để xác nhận route đang chạy, xem `AppNavHost.kt:91-128`.

### Ví dụ lần theo một nút từ màn hình tới function

Lấy nút đồng bộ trên Home:

1. Mở `HomeScreen.kt:72-80`. Ở dòng 78 thấy `onSyncClick = viewModel::onSyncClick`. Dấu `::` nghĩa là đưa function `onSyncClick` làm callback cho component `GreetingRow`.
2. Mở `HomeViewModel.kt:170-189`. `onSyncClick()` gọi `refreshWithFeedback()`, hàm này gọi `articleRepository.refreshAll()` rồi phát toast kết quả.
3. Mở `data/repository/JsonArticleRepository.kt:63-87` để xem repository tải lại feed; request mạng ở dòng **164-186**, ghi/cache bài ở **188-216**.
4. Kết quả được phát qua `StateFlow`; Home đang nghe danh sách bài tại `HomeViewModel.kt:54-60`, Compose nhận state ở `HomeScreen.kt:54` và vẽ lại.

Khi mò một nút bất kỳ, làm giống vậy: **tìm nút trong `...Screen.kt` → xem `onClick`/`onSomething` nó nối tới đâu → mở tên đó trong `...ViewModel.kt` → nếu thấy `repository.` thì mở file Repository để xem lưu/tải dữ liệu**. Có thể nhấn Ctrl/Cmd + click vào tên hàm để nhảy tới nơi khai báo.

## 2. Thầy hỏi: muốn đổi màu, hình, icon, chữ thì sửa ở đâu?

### Đổi màu toàn app

Mở `app/src/main/java/vn/edu/usth/rssreader/ui/theme/Color.kt`:

- Màu thương hiệu: dòng **14-22**, ví dụ `Primary` ở dòng 15.
- Màu nền/thẻ: dòng **24-28**, ví dụ `SurfaceCanvas` ở dòng 25.
- Màu chữ: dòng **30-35**.
- Màu trạng thái thành công/lỗi: dòng **37-40**.

Các tên màu này được dùng lại trong nhiều màn. Đổi một token như `Primary` sẽ đổi mọi chỗ đang dùng token đó. Theme ánh xạ các màu sang Material ở `ui/theme/Theme.kt:14-43`.

Nếu chỉ muốn đổi đúng **một nút/thành phần**, mở Composable của thành phần đó và sửa `containerColor`, `color`, `tint`, hoặc `Modifier.background(...)`. Ví dụ nút thêm RSS ở `ui/screen/rss/RssScreen.kt:190-200`; màu icon tab được tính tại `ui/components/AppBottomNav.kt:73-99`.

### Đổi chữ đậm, cỡ chữ, nền, khoảng cách

Trong Compose, tìm thành phần `Text(...)` hoặc `Button(...)` bên trong file UI:

- `fontWeight = FontWeight.Bold` / `SemiBold`: độ đậm.
- `fontSize = ...sp`: cỡ chữ.
- `color = ...`: màu chữ.
- `Modifier.background(...)`: màu nền.
- `padding(...)`, `height(...)`, `size(...)`: khoảng cách/kích thước.

Ví dụ tiêu đề trong danh sách tin Home: `ui/screen/home/components/NewsListItem.kt:89-99`; nền card ở dòng **48-55**. Typography dùng chung khai báo ở `ui/theme/Type.kt:9-17`, nhưng một số text đặt kiểu trực tiếp trong từng Composable.

### Đổi icon menu dưới cùng

- Tên/nhãn và icon ứng với từng tab: `ui/navigation/Destination.kt:10-39`. Ví dụ Home chọn `Icons.Filled.Home` ở dòng 16-21.
- Cách vẽ, màu tab được chọn và màu tab chưa chọn: `ui/components/AppBottomNav.kt:33-65` và **67-102**.
- Bấm tab gọi `onNavigate(destination)` tại `AppBottomNav.kt:55-60`; nơi định tuyến các tab là `AppNavHost.kt:64-71` và **91-112**.

Muốn thay icon: đổi `icon = Icons.Filled...` trong `Destination.kt`. Muốn đổi màu khi chọn/bỏ chọn: sửa `tint` ở `AppBottomNav.kt:73`, hoặc đổi token `Primary`/`TextMuted` trong `Color.kt`.

### Đổi ảnh bài báo

Ảnh bài báo không phải icon trong menu. Card nhận URL từ `article.imageUrl`:

- Ảnh bài nổi bật: `ui/screen/home/components/FeaturedNewsCard.kt:73-85` (`AsyncImage`).
- Ảnh bài trong danh sách: `ui/screen/home/components/NewsListItem.kt:121-133`.
- Dữ liệu ảnh thuộc model `data/model/Article.kt:5-19`.

Với RSS thật, ảnh thường được lấy từ RSS khi parse bài; parser ở `data/parser/RssArticleParser.kt`. Nếu thay `AsyncImage` thì đổi cách hiển thị ảnh; nếu muốn đổi **ảnh của một bài cụ thể**, phải đổi `imageUrl` ở dữ liệu nguồn/parser tương ứng. `FakeNewsData.kt` và `FakeExploreData.kt` chứa ảnh mẫu chủ yếu cho Preview/demo.

### Ví dụ cụ thể: muốn sửa màn Home

Mở `ui/screen/home/HomeScreen.kt:47-159`. File này sắp xếp các khối Home:

- Header: `GreetingRow` — dòng **71-81**.
- Thanh tìm kiếm: `HomeSearchBar` — dòng **83-88**.
- Danh mục: `CategoryStrip` — dòng **92-99**.
- Tin nổi bật: `FeaturedSection` — dòng **115-130**.
- Tin mới: `LatestSection` — dòng **133-147**.
- Thanh trạng thái RSS: `RssStatusBar` — dòng **151-155**.

Muốn đổi chi tiết của một khối thì mở component tương ứng ở `ui/screen/home/components/` (ví dụ `FeaturedNewsCard.kt`, `NewsListItem.kt`, `GreetingRow.kt`). Muốn đổi **màu nền toàn màn Home**, xem `HomeScreen.kt:63-67`.

## 3. Thầy hỏi: function xử lý tính năng nằm đâu, trả về gì?

UI và xử lý được tách đôi: Screen nhận `uiState` và nối callback; ViewModel xử lý callback rồi gọi Repository. Các hàm `on...` trong ViewModel thường trả về `Unit` (không trả dữ liệu cho UI ngay); chúng khởi chạy coroutine, cập nhật `StateFlow` hoặc phát thông báo. Kết quả hiển thị quay lại qua `uiState`/event.

| Tính năng | UI nối sự kiện | Hàm xử lý / kết quả |
|---|---|---|
| Home: chọn danh mục | `HomeScreen.kt:92-98` | `HomeViewModel.onCategorySelected(categoryId)` — cập nhật state và lọc bài; `HomeViewModel.kt:156-159`. |
| Home: lưu/bỏ lưu bài | `HomeScreen.kt:116-123` | `onToggleBookmark(article, bookmarked)` gọi `articleRepository.toggleBookmark`; `HomeViewModel.kt:161-168`. Repository trả `Boolean` là trạng thái sau khi bật/tắt; interface ở `data/repository/ArticleRepository.kt:23-24`. |
| Home: đồng bộ/làm mới bài | `HomeScreen.kt:72-80`, **133-140** | `onSyncClick()` / `onRefreshClick()` gọi `refreshWithFeedback()`; `HomeViewModel.kt:170-189`. Hàm gọi `articleRepository.refreshAll()`; phát thông báo như “Đã cập nhật N bài mới”, lỗi gần nhất, hoặc “Không có bài mới”. |
| Tìm kiếm | `ui/screen/explore/ExploreScreen.kt:57-77` | `ExploreViewModel.onQueryChange(q)` cập nhật query rồi lọc danh sách; `ExploreViewModel.kt:65-68`, logic lọc/tìm tại **93-160**. |
| Màn Đã lưu: lọc/sắp xếp/bỏ lưu | `ui/screen/saved/SavedScreen.kt` | `SavedViewModel.onCategorySelected`, `onSortModeChange`, `onUnsave`; `ui/screen/saved/SavedViewModel.kt:47-58`. |
| Thêm nguồn RSS | `ui/screen/rss/RssScreen.kt:112-121` | `RssSourcesViewModel.validateNewSource()` kiểm tra URL; `saveNewSource()` gọi repository; `RssSourcesViewModel.kt:103-193`. |
| Làm mới một nguồn RSS | `RssScreen.kt:85-91` | `RssSourcesViewModel.refresh(sourceId)` gọi `repository.refresh(sourceId)`; `RssSourcesViewModel.kt:195-205`. |
| Xóa nguồn RSS | `RssScreen.kt:102-109` | `requestRemoval()` mở xác nhận, `confirmRemoval()` gọi repository rồi phát event; `RssSourcesViewModel.kt:208-265`. |

### Luồng fetch RSS (đây là chỗ nên mở khi thầy hỏi “fetch ở đâu?”)

1. Tab RSS nối nút làm mới vào ViewModel tại `RssScreen.kt:85-91`.
2. `RssSourcesViewModel.refresh()` gọi `repository.refresh(sourceId)` tại `RssSourcesViewModel.kt:195-205`.
3. `JsonRssSourceRepository.refresh()` ở `data/repository/RssSourceRepository.kt:163-170` chuyển nguồn sang `FETCHING`, rồi `completeFetch()` kiểm tra URL/feed và lưu trạng thái `ACTIVE` hoặc `ERROR` tại **212-245**. Kết quả hàm là `SourceActionResult` (Success / NotFound / Failure), định nghĩa ở dòng **43-47**.
4. Khi nguồn active, `JsonArticleRepository.observeSources()` phát hiện tại `data/repository/JsonArticleRepository.kt:132-155`, gọi `fetchAndMerge()` ở **158-162**.
5. Request HTTP tải XML ở `JsonArticleRepository.kt:164-186`; `RssArticleParser` parse thành `List<Article>`. Sau đó merge bài theo id và ghi cache ở **188-195**.
6. `HomeViewModel` đang collect danh sách bài từ repository tại `ui/screen/home/HomeViewModel.kt:54-60`, cập nhật `HomeUiState`; Compose quan sát state nên màn tự vẽ lại.

**Cách nói gọn:** “ViewModel nhận thao tác người dùng; Repository làm I/O. Nguồn RSS được kiểm tra và đổi trạng thái trước, sau đó ArticleRepository mới tải và parse XML thành Article.”

**Kiểu kết quả khi thêm nguồn:** `RssFeedInspector.validate()` trả `RssFeedValidationResult.Valid(detectedTitle, detectedSiteName)` hoặc `Invalid(message)` (`data/repository/RssFeedInspector.kt:10-20`). `RssSourceRepository.add()` trả `AddSourceResult.Created(source)`, `Duplicate`, hoặc `Failure(message)` (`data/repository/RssSourceRepository.kt:49-59`, **125-160**). `saveNewSource()` đọc các nhánh đó và cập nhật UI/event ở `RssSourcesViewModel.kt:169-190`. Nhớ phân biệt: callback ViewModel thường là `Unit`; kết quả có ích nằm ở lời gọi Repository/state/event.

## 4. Thầy hỏi: data lưu đâu, quản lý và lấy ra thế nào?

App này **không có backend riêng**. Nó tải RSS trực tiếp trên điện thoại và lưu cache vào bộ nhớ riêng của app.

| Dữ liệu | Nơi lưu / nguồn | Đọc và cập nhật |
|---|---|---|
| Danh mục RSS mặc định | File đóng gói cùng app: `app/src/main/assets/rss_sources.json` | Dùng làm seed khi cài app lần đầu. Repository copy vào file riêng của app. |
| Danh sách RSS người dùng thêm/xóa, trạng thái fetch | `filesDir/rss_sources.json` trong bộ nhớ riêng ứng dụng | `JsonRssSourceRepository` đọc/ghi JSON; khởi tạo file từ asset nếu chưa có. Xem `RssSourceRepository.kt:61-80`, đọc/ghi tại **255-297**, add/remove tại **125-185**. |
| Các bài đã tải từ feed | `filesDir/articles.json` | `JsonArticleRepository.kt:30-38`, nạp cache ở **123-130**, lưu bài ở **199-216**. |
| Các bài đã bookmark | `filesDir/bookmarks.json` | Tương tự, đọc/ghi ở `JsonArticleRepository.kt:218-246`; thao tác toggle ở **89-105**. |
| State đang hiển thị | RAM qua `StateFlow` | Repository phát `StateFlow`; ViewModel collect và tạo `UiState`; Screen collect state để vẽ. Ví dụ `HomeViewModel.kt:28-49`, `HomeScreen.kt:47-55`. |

Trong `RssSourceRepository.kt:66-76`, `catalogFile` trỏ tới `File(appContext.filesDir, "rss_sources.json")`. Trong `JsonArticleRepository.kt:36-38`, hai file cache là `articles.json` và `bookmarks.json`. `filesDir` là vùng riêng theo app, không phải file nằm trong thư mục source code của project.

Nếu cần chỉ vị trí dữ liệu trên thiết bị debug, có thể mở **Android Studio → View → Tool Windows → Device Explorer**, rồi tìm vùng riêng của package `vn.edu.usth.rssreader`, thư mục `files/`. Đây là dữ liệu trên điện thoại/emulator; nó không sửa các file JSON trong project.

Đường đi dữ liệu RSS: `rss_sources.json` → fetch URL → parser → `Article` → `articles.json` → `StateFlow` → ViewModel → màn hình. Model bài báo nằm ở `data/model/Article.kt:5-19`; model nguồn RSS ở `data/model/RssSource.kt`.

### Phân biệt dữ liệu thật và dữ liệu mẫu

- Home runtime lấy danh sách bài từ `articleRepository`; xem `HomeViewModel.kt:24-26`, **54-60**, **121-148**.
- `FakeArticles`, `FakeExploreTrending`, `FakeExploreArticles` được dùng trong `@Preview` để xem mẫu UI; ví dụ `FeaturedNewsCard.kt:289+`, `ExploreScreen.kt:318+`, `SavedScreen.kt:377+`.
- `FakeCategories` còn được dùng làm danh mục “Tất cả”/danh mục gợi ý, không có nghĩa toàn bộ bài đang hiển thị là fake.
- `ArticleDetailViewModel` có ghép dữ liệu repository với các bài fake để tìm bài theo id: `src/S02-article-detail/ArticleDetailViewModel.kt:84`. Nếu thầy hỏi riêng màn chi tiết thì nói rõ nó có fallback dữ liệu mẫu.

## 5. Câu hỏi: sửa code thì điện thoại có đổi ngay không?

**Chỉ lưu file code thì chưa đổi trên điện thoại.** Điện thoại đang chạy APK đã build/cài trước đó. Muốn xem bản sửa:

1. Kết nối điện thoại Android bằng USB, bật **Developer options → USB debugging**, mở khóa và chấp nhận hộp thoại cho phép debug nếu hiện ra.
2. Mở project bằng Android Studio, chọn đúng điện thoại ở thanh thiết bị.
3. Nhấn **Run ▶ app** để Android Studio build và cài/chạy bản debug mới lên điện thoại. Nếu chỉ sửa một vài thay đổi UI, có thể thử **Apply Changes**; nếu không cập nhật hoặc build lỗi thì dùng Run lại.

Khi cài đè bản debug cùng package, dữ liệu local thường được giữ. Nếu gỡ app rồi cài lại thì Android xóa vùng dữ liệu riêng của app, gồm RSS đã thêm, cache bài và bookmark.

Máy ảo Android cũng dùng được. Chỉ khi IDE cài bản build mới (hoặc Apply Changes hỗ trợ loại thay đổi đó) thì giao diện trên máy mới đổi; nó không tự đồng bộ theo từng lần bạn gõ code.

**Riêng file RSS mặc định:** lần chạy đầu repository copy `app/src/main/assets/rss_sources.json` sang `filesDir/rss_sources.json`. Sau đó bản copy cục bộ được ưu tiên để giữ nguồn người dùng đã chỉnh. Vì vậy sửa asset rồi bấm Run có thể **không ghi đè** danh sách RSS đã có trên điện thoại. Có thể dùng chức năng “Khôi phục mặc định” trong app; thao tác này thay danh sách RSS cục bộ bằng danh sách mặc định.

Muốn build/cài qua terminal (đã có Android SDK và thiết bị được `adb` nhận):

```bash
./gradlew installDebug
```

## 6. Danh sách file nên mở sẵn khi thuyết trình

1. `MainActivity.kt` — app bắt đầu ở đâu: dòng **11-19**.
2. `AppNavHost.kt` — màn hình/điều hướng: dòng **52-128**.
3. `Destination.kt` — tên tab và icon: dòng **10-39**.
4. `HomeScreen.kt` — các khối giao diện Home: dòng **47-159**.
5. `HomeViewModel.kt` — state và xử lý bấm Home: dòng **22-49**, **121-189**.
6. `JsonArticleRepository.kt` — fetch RSS, cache bài và bookmark: dòng **30-105**, **132-195**, **199-246**.
7. `RssSourcesViewModel.kt` — validate/add/refresh/remove: dòng **103-265**.
8. `RssSourceRepository.kt` — nguồn RSS lưu JSON: dòng **61-80**, **125-185**, **255-297**.
9. `Color.kt` + `AppBottomNav.kt` — màu và menu dưới: `Color.kt:14-43`, `AppBottomNav.kt:33-102`.

Trong Android Studio: dùng **Search Everywhere** (nhấn `Shift` hai lần) hoặc **Navigate → File** để gõ tên file; bật **View → Appearance → Show Line Numbers** để chỉ đúng dòng. Tên hàm có thể tìm nhanh bằng `Ctrl+Shift+F` (Windows/Linux) hoặc `⌘+Shift+F` (macOS).

## 7. Bài nói mẫu ngắn

> “App viết bằng Kotlin và Jetpack Compose. `MainActivity` mở `AppNavHost`, nơi điều hướng giữa các màn. Screen chỉ dựng giao diện và nối callback; ViewModel xử lý thao tác, gọi Repository và phát state. Với RSS, app tải feed trực tiếp trên thiết bị, parse thành model `Article`, lưu cache JSON vào vùng riêng của app rồi cập nhật UI qua `StateFlow`. Màu dùng chung nằm trong `Color.kt`, còn từng screen và component quyết định bố cục cụ thể.”

## 8. Các chỗ còn TODO

- Hai nút “Xem tất cả”/“Xem thêm” trên Home đang có `TODO`, chưa mở màn danh sách riêng: `HomeScreen.kt:125-128` và **141-144**.
- Nút filter ở màn Explore hiện nối callback rỗng (`onFilterClick = { }`): `ui/screen/explore/ExploreScreen.kt:109-114`.

