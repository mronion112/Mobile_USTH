# Call graph — Mobile USTH RSS News Reader

`file:line` là vị trí khai báo, mũi tên là lời gọi trong thân hàm.

## 1. UI runtime

- [MainActivity.kt:11](app/src/main/java/vn/edu/usth/rssreader/MainActivity.kt#L11-L23) — `MainActivity`
  - `onCreate:13` → `setContent:15`
    - [Theme.kt:36](app/src/main/java/vn/edu/usth/rssreader/ui/theme/Theme.kt#L36-L43) — `MobileUSTHTheme`
      - [AppNavHost.kt:62](app/src/main/java/vn/edu/usth/rssreader/ui/navigation/AppNavHost.kt#L62-L146) — `AppNavHost`
        - [Destination.kt:42](app/src/main/java/vn/edu/usth/rssreader/ui/navigation/Destination.kt#L42-L43) — `Destination.fromRoute` (route → tab)
        - `navigateToTab:73` — chặn tap khi transition chưa `RESUMED:78`, rồi `navigate:80`
        - `navigateToArticle:89` → `navigate("article/$id"):90`
        - [AppScaffold.kt:11](app/src/main/java/vn/edu/usth/rssreader/ui/navigation/AppScaffold.kt#L11-L30) — `AppScaffold`
          - [AppBottomNav.kt:34](app/src/main/java/vn/edu/usth/rssreader/ui/components/AppBottomNav.kt#L34-L66) — `AppBottomNav`
            - [AppBottomNav.kt:68](app/src/main/java/vn/edu/usth/rssreader/ui/components/AppBottomNav.kt#L68-L102) — `BottomNavItem` → `onNavigate(destination)`
        - `NavHost:98` — enter [screenEnter:43](app/src/main/java/vn/edu/usth/rssreader/ui/navigation/AppNavHost.kt#L43-L50), exit [screenExit:52](app/src/main/java/vn/edu/usth/rssreader/ui/navigation/AppNavHost.kt#L52-L59)

### Home

- [HomeScreen.kt:48](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/HomeScreen.kt#L48-L180) — `HomeScreen`
  - `uiState.collectAsState:54`
  - `LaunchedEffect` thu `toastEvents:57`
  - [GreetingRow.kt:33](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/components/GreetingRow.kt#L33-L108) — `GreetingRow`
    - [StatusDot.kt:34](app/src/main/java/vn/edu/usth/rssreader/ui/components/StatusDot.kt#L34-L106) — `StatusDot`
    - [CircleIconButton.kt:35](app/src/main/java/vn/edu/usth/rssreader/ui/components/CircleIconButton.kt#L35-L80) — `CircleIconButton` → `StatusDot:94`
  - [HomeSearchBar.kt:34](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/components/HomeSearchBar.kt#L34-L76) — `HomeSearchBar` → `onSearchClick`
  - [CategoryStrip.kt:28](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/components/CategoryStrip.kt#L28-L72) — `CategoryStrip`
    - [CategoryPill.kt:30](app/src/main/java/vn/edu/usth/rssreader/ui/components/article/CategoryPill.kt#L30-L65) — `CategoryPill`
  - [FeedbackStates.kt:79](app/src/main/java/vn/edu/usth/rssreader/ui/components/FeedbackStates.kt#L79-L133) — `LoadingState`
  - `NoSourceState:111` → khai báo [HomeScreen.kt:185](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/HomeScreen.kt#L185-L214)
  - [FeedbackStates.kt:160](app/src/main/java/vn/edu/usth/rssreader/ui/components/FeedbackStates.kt#L160-L188) — `ErrorState`
  - `EmptyFilterState:123` → khai báo [HomeScreen.kt:217](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/HomeScreen.kt#L217-L239)
  - [FeaturedSection.kt:36](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/components/FeaturedSection.kt#L36-L91) — `FeaturedSection`
    - `HotBadge:68` → [FeaturedSection.kt:93](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/components/FeaturedSection.kt#L93-L120)
    - [FeaturedNewsCard.kt:57](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/components/FeaturedNewsCard.kt#L57-L271) — `FeaturedNewsCard`
      - `AsyncImage:80`
      - [CategoryBadge.kt:30](app/src/main/java/vn/edu/usth/rssreader/ui/components/article/CategoryBadge.kt#L30-L64) — `CategoryBadge`
      - [BookmarkButton.kt:39](app/src/main/java/vn/edu/usth/rssreader/ui/components/article/BookmarkButton.kt#L39-L95) — `BookmarkButton`
      - `OverlayTag:113` → [FeaturedNewsCard.kt:273](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/components/FeaturedNewsCard.kt#L273-L288)
  - [LatestSectionHeader.kt:38](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/components/LatestSectionHeader.kt#L38-L72) — `LatestSectionHeader`
    - `RefreshChip:69` → [LatestSectionHeader.kt:74](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/components/LatestSectionHeader.kt#L74-L115)
    - `StatusDot:62`
  - [NewsListItem.kt:41](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/components/NewsListItem.kt#L41-L138) — `NewsListItem`
    - `CategoryBadge:69`, `BookmarkButton:113`, `AsyncImage:128`
  - [RssStatusBar.kt:36](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/components/RssStatusBar.kt#L36-L112) — `RssStatusBar` → `StatusDot:95`

### Explore

- [ExploreScreen.kt:58](app/src/main/java/vn/edu/usth/rssreader/ui/screen/explore/ExploreScreen.kt#L58-L77) — `ExploreScreen`
  - [ExploreScreen.kt:80](app/src/main/java/vn/edu/usth/rssreader/ui/screen/explore/ExploreScreen.kt#L80-L209) — `ExploreContent`
    - [ExploreSearchBar.kt:48](app/src/main/java/vn/edu/usth/rssreader/ui/screen/explore/components/ExploreSearchBar.kt#L48-L144) — `ExploreSearchBar` → `onValueChange:113`; focus trễ `TAB_ANIM_MS` tại `ExploreScreen.kt:93`
    - `LazyRow` danh mục:125 → [CategoryPill.kt:30](app/src/main/java/vn/edu/usth/rssreader/ui/components/article/CategoryPill.kt#L30-L65)
    - `ExploreLoading:143` → [ExploreScreen.kt:214](app/src/main/java/vn/edu/usth/rssreader/ui/screen/explore/ExploreScreen.kt#L214-L237)
    - `ExploreNoSource:145` → [ExploreScreen.kt:240](app/src/main/java/vn/edu/usth/rssreader/ui/screen/explore/ExploreScreen.kt#L240-L269)
    - [TrendingTopicsSection.kt:33](app/src/main/java/vn/edu/usth/rssreader/ui/screen/explore/components/TrendingTopicsSection.kt#L33-L93) — `TrendingTopicsSection`
      - [TrendingTopicCard.kt:49](app/src/main/java/vn/edu/usth/rssreader/ui/screen/explore/components/TrendingTopicCard.kt#L49-L155) — `TrendingTopicCard`
    - [FilterBadgesRow.kt:25](app/src/main/java/vn/edu/usth/rssreader/ui/screen/explore/components/FilterBadgesRow.kt#L25-L66) — `FilterBadgesRow`
    - `ExploreEmpty:170` → [ExploreScreen.kt:272](app/src/main/java/vn/edu/usth/rssreader/ui/screen/explore/ExploreScreen.kt#L272-L325)
    - [SearchResultItem.kt:50](app/src/main/java/vn/edu/usth/rssreader/ui/screen/explore/components/SearchResultItem.kt#L50-L179) — `SearchResultItem`
      - [HighlightedText.kt:25](app/src/main/java/vn/edu/usth/rssreader/ui/components/HighlightedText.kt#L25-L82) — `HighlightedText`
      - `SnippetText:119` → [SearchResultItem.kt:195](app/src/main/java/vn/edu/usth/rssreader/ui/screen/explore/components/SearchResultItem.kt#L195-L224) → `buildSnippet` [SearchResultItem.kt:181](app/src/main/java/vn/edu/usth/rssreader/ui/screen/explore/components/SearchResultItem.kt#L181-L193)
      - `Dot:88` → [SearchResultItem.kt:226](app/src/main/java/vn/edu/usth/rssreader/ui/screen/explore/components/SearchResultItem.kt#L226-L235)
      - `BookmarkButton:154`
    - [LoadMoreButton.kt:29](app/src/main/java/vn/edu/usth/rssreader/ui/screen/explore/components/LoadMoreButton.kt#L29-L63) — `LoadMoreButton`

### Saved

- [SavedScreen.kt:62](app/src/main/java/vn/edu/usth/rssreader/ui/screen/saved/SavedScreen.kt#L62-L76) — `SavedScreen`
  - [SavedScreen.kt:79](app/src/main/java/vn/edu/usth/rssreader/ui/screen/saved/SavedScreen.kt#L79-L229) — `SavedContent`
    - lọc + sort tại UI: `90-100`
    - [SavedSummaryCard.kt:41](app/src/main/java/vn/edu/usth/rssreader/ui/screen/saved/components/SavedSummaryCard.kt#L41-L158) — `SavedSummaryCard`
    - [FolderPill.kt:32](app/src/main/java/vn/edu/usth/rssreader/ui/screen/saved/components/FolderPill.kt#L32-L77) — `FolderPill`
    - `SortModeChip:181` → [SavedScreen.kt:232](app/src/main/java/vn/edu/usth/rssreader/ui/screen/saved/SavedScreen.kt#L232-L300)
      - `SortOptionRow:282` → [SavedScreen.kt:303](app/src/main/java/vn/edu/usth/rssreader/ui/screen/saved/SavedScreen.kt#L303-L329)
    - [SavedArticleItem.kt:42](app/src/main/java/vn/edu/usth/rssreader/ui/screen/saved/components/SavedArticleItem.kt#L42-L149) — `SavedArticleItem`
      - `CategoryBadge:68`, `BookmarkButton:123`, `AsyncImage:139`
    - [EmptySavedState.kt:33](app/src/main/java/vn/edu/usth/rssreader/ui/screen/saved/components/EmptySavedState.kt#L33-L80) — `EmptySavedState`
    - `EmptyFilterState:212` → [SavedScreen.kt:332](app/src/main/java/vn/edu/usth/rssreader/ui/screen/saved/SavedScreen.kt#L332-L357)
    - [FeedSyncBar.kt:33](app/src/main/java/vn/edu/usth/rssreader/ui/screen/saved/components/FeedSyncBar.kt#L33-L105) — `FeedSyncBar`

### RSS sources

- [RssScreen.kt:64](app/src/main/java/vn/edu/usth/rssreader/ui/screen/rss/RssScreen.kt#L64-L132) — `RssScreen`
  - `events` → `SnackbarHost:72,103`
  - [FeedbackStates.kt:79](app/src/main/java/vn/edu/usth/rssreader/ui/components/FeedbackStates.kt#L79-L133) — `LoadingState`
  - [FeedbackStates.kt:160](app/src/main/java/vn/edu/usth/rssreader/ui/components/FeedbackStates.kt#L160-L188) — `ErrorState`
  - [FeedbackStates.kt:135](app/src/main/java/vn/edu/usth/rssreader/ui/components/FeedbackStates.kt#L135-L158) — `EmptyState`
  - [RssScreen.kt:135](app/src/main/java/vn/edu/usth/rssreader/ui/screen/rss/RssScreen.kt#L135-L179) — `SourcesContent`
    - `SourceHeader:151` → [RssScreen.kt:182](app/src/main/java/vn/edu/usth/rssreader/ui/screen/rss/RssScreen.kt#L182-L222) → `SourceCountBadge:196` [RssScreen.kt:224](app/src/main/java/vn/edu/usth/rssreader/ui/screen/rss/RssScreen.kt#L224-L239)
    - `SourceSummary:165` → [RssScreen.kt:241](app/src/main/java/vn/edu/usth/rssreader/ui/screen/rss/RssScreen.kt#L241-L277) → `SummaryCell:253` [RssScreen.kt:279](app/src/main/java/vn/edu/usth/rssreader/ui/screen/rss/RssScreen.kt#L279-L299)
    - [RssSourceCard.kt:54](app/src/main/java/vn/edu/usth/rssreader/ui/screen/rss/components/RssSourceCard.kt#L54-L180) — `RssSourceCard`
      - [FetchStatusPresentation.kt:20](app/src/main/java/vn/edu/usth/rssreader/feature/status/FetchStatusPresentation.kt#L20-L63) — `toFetchStatusUi` → `formatRelativeFetchTime:65`
      - `CategoryBadge:109` → bản private trùng tại [RssSourceCard.kt:182](app/src/main/java/vn/edu/usth/rssreader/ui/screen/rss/components/RssSourceCard.kt#L182-L199)
      - `SourceStatus:125` → [RssSourceCard.kt:201](app/src/main/java/vn/edu/usth/rssreader/ui/screen/rss/components/RssSourceCard.kt#L201-L252) → `StatusPill:207` [RssSourceCard.kt:254](app/src/main/java/vn/edu/usth/rssreader/ui/screen/rss/components/RssSourceCard.kt#L254-L281) → `StatusDot:211`
      - `LastFetchedTime:127` → [RssSourceCard.kt:283](app/src/main/java/vn/edu/usth/rssreader/ui/screen/rss/components/RssSourceCard.kt#L283-L299)
  - [RemoveSourceConfirmationDialog.kt:33](app/src/main/java/vn/edu/usth/rssreader/ui/screen/rss/components/RemoveSourceConfirmationDialog.kt#L33-L106) — `RemoveSourceConfirmationDialog`
  - [AddRssSourceSheet.kt:54](app/src/main/java/vn/edu/usth/rssreader/ui/screen/rss/components/AddRssSourceSheet.kt#L54-L215) — `AddRssSourceSheet`
    - `SourceInput:94` → [AddRssSourceSheet.kt:217](app/src/main/java/vn/edu/usth/rssreader/ui/screen/rss/components/AddRssSourceSheet.kt#L217-L241)
    - `SourceFeedback:136` → [AddRssSourceSheet.kt:243](app/src/main/java/vn/edu/usth/rssreader/ui/screen/rss/components/AddRssSourceSheet.kt#L243-L270)

### Article detail

- [ArticleDetailScreen.kt:73](app/src/main/java/vn/edu/usth/rssreader/ui/screen/detail/ArticleDetailScreen.kt#L73-L177) — `ArticleDetailScreen`
  - `provideFactory:82` → [ArticleDetailViewModel.kt:89](app/src/main/java/vn/edu/usth/rssreader/ui/screen/detail/ArticleDetailViewModel.kt#L89-L99)
  - [ArticleDetailTopBar.kt:31](app/src/main/java/vn/edu/usth/rssreader/ui/screen/detail/components/ArticleDetailTopBar.kt#L31-L95) — `ArticleDetailTopBar`
    - `onShareClick:96` → `shareArticle` [ArticleDetailScreen.kt:393](app/src/main/java/vn/edu/usth/rssreader/ui/screen/detail/ArticleDetailScreen.kt#L393-L401)
    - `onBookmarkToggle:99`
  - `Loading:109`, `Error:129` → `loadArticle:153`
  - `ArticleDetailContent:165` → [ArticleDetailScreen.kt:180](app/src/main/java/vn/edu/usth/rssreader/ui/screen/detail/ArticleDetailScreen.kt#L180-L391)
    - [ArticleSummaryCard.kt:30](app/src/main/java/vn/edu/usth/rssreader/ui/screen/detail/components/ArticleSummaryCard.kt#L30-L74) — `ArticleSummaryCard`
    - [ArticleContentRenderer.kt:31](app/src/main/java/vn/edu/usth/rssreader/ui/screen/detail/components/ArticleContentRenderer.kt#L31-L110) — `ArticleContentRenderer`
      - `Paragraph:41` → [DropCapParagraph.kt:17](app/src/main/java/vn/edu/usth/rssreader/ui/screen/detail/components/DropCapParagraph.kt#L17-L51)
      - `Heading:54`, `InlineImage:65`, `Quote:97`
    - `openOriginal:169` → [ArticleDetailScreen.kt:403](app/src/main/java/vn/edu/usth/rssreader/ui/screen/detail/ArticleDetailScreen.kt#L403-L416)

## 2. Event → ViewModel → Repository

### Home

- [HomeViewModel.kt:22](app/src/main/java/vn/edu/usth/rssreader/ui/screen/home/HomeViewModel.kt#L22-L52) — `HomeViewModel`, init:44
  - `observeArticles:54` ← `articleRepository.articles:56` → `applyArticles:121`
  - `observeBookmarks:62` ← `articleRepository.bookmarkedIds:64`
  - `observeRefreshState:70` ← `isRefreshing:72`, `refreshError:79` → `applyArticles:81`
  - `observeSources:86` ← `sourceRepository.sources:88`
  - `onNotificationClick:152`
  - `onCategorySelected:156` → `applyArticles:158`
  - `onToggleBookmark:161` → `bookmarkedIds:163` → `articleRepository.toggleBookmark:165`
  - `onSyncClick:170` / `onRefreshClick:172` → `refreshWithFeedback:174`
    - `refreshAll:178`, so sánh id `177,179`, `refreshError:181` → `toastEvents.emit`

### Explore

- [ExploreViewModel.kt:17](app/src/main/java/vn/edu/usth/rssreader/ui/screen/explore/ExploreViewModel.kt#L17-L160) — `ExploreViewModel`
  - `observeArticles:35`, `observeBookmarks:44`, `observeSources:52`
  - `onQueryChange:65` → `recompute:93`
  - `onCategorySelected:70` → `recompute:93`
  - `onLoadMore:75` → `recomputeVisible:147`
  - `onToggleBookmark:82` → `articleRepository.toggleBookmark:86`
  - `recompute:93` → `recomputeVisible:147` → `matchesQuery:153`

### Saved

- [SavedViewModel.kt:18](app/src/main/java/vn/edu/usth/rssreader/ui/screen/saved/SavedViewModel.kt#L18-L90) — `SavedViewModel`
  - init:26 ← `sourceRepository.sources:32`, `articleRepository.bookmarkedArticles:40` → `recompute:42`
  - `onCategorySelected:47` — chỉ đổi state, UI tự lọc
  - `onSortModeChange:51`
  - `onUnsave:55` → `articleRepository.toggleBookmark:57`
  - `recompute:60`

### RSS sources

- [RssSourcesViewModel.kt:21](app/src/main/java/vn/edu/usth/rssreader/ui/screen/rss/RssSourcesViewModel.kt#L21-L303) — `RssSourcesViewModel`
  - init:29 ← `repository.sources:31`, `repository.loadError:41`
  - `reloadSources:47` → `repository.reload:48`
  - `restoreBundledCatalog:51` → `repository.restoreBundledCatalog:53`
  - `openAddSource:61`, `dismissAddSource:75`
  - `updateNewSourceName:80`, `updateNewSourceUrl:84`, `selectNewSourceCategory:96` → `updateAddSource:268`
  - `validateNewSource:103` → `repository.validate:114` → `suggestCategoryForFeed:296`
  - `saveNewSource:159` → `repository.add:171`
  - `refresh:195` → `repository.refresh:200`
  - `requestRemoval:208`, `dismissRemoval:220`
  - `confirmRemoval:225` → `repository.remove:233`

### Article detail

- [ArticleDetailViewModel.kt:20](app/src/main/java/vn/edu/usth/rssreader/ui/screen/detail/ArticleDetailViewModel.kt#L20-L88) — `ArticleDetailViewModel`
  - init:31 → `observeBookmarks:68`, `loadArticle:34`
  - `loadArticle:34` → `findArticle:82` → [ArticleHtmlParser.kt:18](app/src/main/java/vn/edu/usth/rssreader/ui/screen/detail/ArticleHtmlParser.kt#L18-L36)
    - `parseDocument:37` → `extractImageUrl:143`, `isIgnoredElement:157`, `isMetadataParagraph:163`
    - `fallbackFromArticle:171`
  - `toggleBookmark:54` → `articleRepository.toggleBookmark:57`

## 3. Data layer

- [JsonArticleRepository.kt:30](app/src/main/java/vn/edu/usth/rssreader/data/repository/JsonArticleRepository.kt#L30-L122) — `JsonArticleRepository`
  - `loadFromDisk:123` → `readArticles:199`, `readBookmarks:218`
  - `observeSources:132`
    - `fetchAndMerge:158`
      - `fetchFromNetwork:164`
        - [RssArticleParser.kt:19](app/src/main/java/vn/edu/usth/rssreader/data/parser/RssArticleParser.kt#L19-L52) — `parse`
          - `consume:76` → `toArticle:119`
          - `stripHtml:149`, `extractFirstImage:156`, `parseDate:164`, `md5Hex:179`
      - `mergeAndPersist:188` → `persistArticles:210` → `atomicWrite:238`
  - `refreshAll:63` → `fetchAndMerge` song song (`async:76`)
  - `toggleBookmark:89` → `persistBookmarks:230`
  - `pruneOrphanedArticles:107` ← gọi trong `observeSources:143`
  - JSON: `toJson:248`, `toArticle:270`, `optNullableString:294`, `optNullableInstant:297`
- [RssSourceRepository.kt:66](app/src/main/java/vn/edu/usth/rssreader/data/repository/RssSourceRepository.kt#L66-L82) — `JsonRssSourceRepository`
  - `reload:83`
  - `restoreBundledCatalog:100` → `replaceCatalogWith:289`
  - `validate:118` → [RssFeedInspector.kt:20](app/src/main/java/vn/edu/usth/rssreader/data/repository/RssFeedInspector.kt#L20-L47) → `readFeedTitle:49`
  - `add:125` → `normalizedUrl:342`
  - `refresh:164` → `markFetching:187` → `completeFetch:212` → `feedInspector.validate:213`
  - `remove:173` → `persist:266`
  - `resumeInterruptedFetches:247`
  - `readSources:255` → `ensureCatalogExists:277` → `copyBundledCatalog:281`
- Util: [Greeting.kt:5](app/src/main/java/vn/edu/usth/rssreader/util/Greeting.kt#L5-L14) — `greetingForNow`; [TimeFormatter.kt:14](app/src/main/java/vn/edu/usth/rssreader/util/TimeFormatter.kt#L14-L39) — `formatRelativeTime`; [NumberFormatter.kt:10](app/src/main/java/vn/edu/usth/rssreader/util/NumberFormatter.kt#L10-L20) — `formatCompact`

## 4. Ghi chú kiến trúc

- Luồng chuẩn: `Screen → callback → ViewModel → Repository → StateFlow → Screen`.
- `SavedContent` lọc và sort tại UI (`SavedScreen.kt:90-100`), không qua ViewModel.
- Trạng thái rỗng/lỗi lặp ở 4 nơi: `HomeScreen.kt:185`, `HomeScreen.kt:217`, `ExploreScreen.kt:240`, `SavedScreen.kt:332`; `ScreenStateContent` ([FeedbackStates.kt:52](app/src/main/java/vn/edu/usth/rssreader/ui/components/FeedbackStates.kt#L52-L77)) không được gọi.
- Component không dùng trong runtime: `ShareIconButton`, `TrailingLink` (chỉ còn trong preview).
- `RssSourceCard` có `private CategoryBadge:182` trùng với [CategoryBadge.kt:30](app/src/main/java/vn/edu/usth/rssreader/ui/components/article/CategoryBadge.kt#L30-L64).
- Callback chưa nối: `onFilterClick` (`ExploreScreen.kt:114`), `onSeeAllClick` (`ExploreScreen.kt:153`), `onShareClick` (`ExploreScreen.kt:184`, `SavedScreen.kt:194`).
