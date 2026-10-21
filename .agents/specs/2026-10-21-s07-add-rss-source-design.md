# S07 — Add RSS Source Design Specification

**Date:** 2026-10-21  
**Owner:** Vũ Minh Hoàng  
**Branch:** `hoangvm`  
**Visual Reference:** `Add RSS Source.png`, `DESIGN2.md`, `src/S07-add-rss-source/Guide.md`

---

## 1. Goal & Scope

Implement the **S07 — Add RSS Source** feature in Jetpack Compose following the **Editorial Pulse** design system (`DESIGN2.md`) and the project's on-device local store architecture (`src/00-app-shell/Guide.md`).

The feature provides a modal bottom sheet (`AddRssSourceSheet`) and standalone screen content (`AddRssSourceContent`) that allows a user to:
1. Enter a display name (`Tên hiển thị nguồn`) and RSS feed URL (`Đường dẫn RSS (URL)`).
2. Validate the RSS/Atom URL on-device (`Kiểm tra`), fetching and parsing the XML stream to extract `detectedTitle`, `detectedSiteName`, and `suggestedCategory`.
3. Preview the detected feed metadata or inspect clear Vietnamese validation error messages (`RSS_FEED_INVALID`, `DUPLICATE_SOURCE`, `RSS_FETCH_FAILED`) without losing form input.
4. Select a category (`Chuyên mục mặc định`) from horizontally scrollable pill chips, pre-selecting `suggestedCategory.id` when available.
5. Save the new RSS source (`Thêm nguồn`) with initial status `FETCHING` and notify S06 to update its list.

---

## 2. Modular Package Architecture

To ensure clean code, modularity, and synchronization with `00-app-shell`, `00-shared-components`, and `S06`–`S09`, the codebase under `app/src/main/java/vn/edu/usth/rssreader/` is organized into distinct layers:

```text
app/src/main/java/vn/edu/usth/rssreader/
├── core/
│   ├── designsystem/
│   │   └── EditorialPulseTheme.kt      # Shared DESIGN2 color, shape, and typography tokens
│   └── model/
│       └── RssModels.kt                # Shared models: Category, SourceRef, Article, RssSource, ValidationPreview, FetchError
├── data/
│   ├── parser/
│   │   └── RssFeedValidator.kt         # Interface + On-device XmlPullParser + HTTP feed validator
│   └── repository/
│       └── RssSourceRepository.kt      # Local state store for categories & RSS sources (duplicate check, create, list)
├── feature/
│   └── s07_add_source/
│       ├── AddRssSourceUiState.kt      # Immutable UI state & user events
│       ├── AddRssSourceViewModel.kt    # State holder / ViewModel with coroutine validation & creation logic
│       └── AddRssSourceSheet.kt        # Compose ModalBottomSheet + stateless AddRssSourceContent + @Preview
└── MainActivity.kt                     # App shell integrating S07 trigger in Destination.Rss tab
```

---

## 3. Shared Domain Models (`core/model/RssModels.kt`)

Matches `src/00-app-shell/Guide.md` and `src/S07-add-rss-source/Guide.md` exact field signatures:

```kotlin
package vn.edu.usth.rssreader.core.model

data class Category(
    val id: Long,
    val name: String,
    val slug: String,
)

data class SourceRef(
    val id: Long,
    val name: String,
)

data class Article(
    val id: Long,
    val title: String,
    val description: String?,
    val link: String,
    val imageUrl: String?,
    val publishedAt: String?,
    val fetchedAt: String,
    val source: SourceRef,
    val category: Category,
    val isBookmarked: Boolean,
)

data class RssSource(
    val id: Long,
    val name: String,
    val rssUrl: String,
    val category: Category,
    val status: String,
    val lastFetchedAt: String?,
    val lastError: String?,
    val createdAt: String,
)

data class ValidationPreview(
    val valid: Boolean,
    val detectedTitle: String,
    val detectedSiteName: String,
    val suggestedCategory: Category?,
)

data class FetchError(
    val code: String,
    val message: String,
) {
    companion object {
        const val RSS_FEED_INVALID = "RSS_FEED_INVALID"
        const val RSS_FETCH_FAILED = "RSS_FETCH_FAILED"
        const val DUPLICATE_SOURCE = "DUPLICATE_SOURCE"
        const val RESOURCE_NOT_FOUND = "RESOURCE_NOT_FOUND"
        const val INTERNAL_ERROR = "INTERNAL_ERROR"
    }
}
```

---

## 4. Data Layer: On-Device Validation & Repository

### 4.1 `RssFeedValidator` (`data/parser/RssFeedValidator.kt`)
- **Responsibility**: Validates URL syntax (`http://` or `https://`), fetches the XML stream (or parses raw XML in unit tests), extracts `<channel><title>` (RSS 2.0) or `<feed><title>` (Atom), infers site name and suggested category from URL/title keywords.
- **Interface**:
  ```kotlin
  sealed interface ValidationResult {
      data class Success(val preview: ValidationPreview) : ValidationResult
      data class Failure(val error: FetchError) : ValidationResult
  }

  fun interface RssFeedValidator {
      suspend fun validate(rssUrl: String, categories: List<Category>): ValidationResult
  }
  ```
- **Built-in XML Parser**: Uses `javax.xml.parsers.DocumentBuilderFactory` / `org.xmlpull.v1.XmlPullParser` safely with timeouts and no external library overhead. Also supports offline/preset Dan Tri RSS feeds so emulator and unit tests work reliably even with restricted network.

### 4.2 `RssSourceRepository` (`data/repository/RssSourceRepository.kt`)
- **Responsibility**: Holds `StateFlow<List<Category>>` and `StateFlow<List<RssSource>>`.
- **Operations**:
  - `getCategories(): List<Category>`: Returns default categories (`Thời sự`, `Thế giới`, `Kinh tế`, `Công nghệ`, `Thể thao`, `Giải trí`).
  - `validateUrl(rssUrl: String): ValidationResult`: Checks for duplicate `rssUrl` in existing sources (`DUPLICATE_SOURCE`) first, then delegates to `RssFeedValidator`.
  - `createSource(name: String, rssUrl: String, categoryId: Long): Result<RssSource>`: Verifies non-duplicate URL, resolves `Category` by `categoryId`, creates `RssSource` with `status = "FETCHING"`, prepends/inserts into `sources` StateFlow, and returns the created `RssSource`.

---

## 5. Presentation Layer (`feature/s07_add_source/`)

### 5.1 `AddRssSourceUiState`
```kotlin
data class AddRssSourceUiState(
    val nameInput: String = "",
    val rssUrlInput: String = "",
    val categories: List<Category> = emptyList(),
    val selectedCategoryId: Long? = null,
    val isValidating: Boolean = false,
    val validationPreview: ValidationPreview? = null,
    val error: FetchError? = null,
    val isSaving: Boolean = false,
    val createdSource: RssSource? = null,
) {
    val canValidate: Boolean
        get() = rssUrlInput.isNotBlank() && !isValidating && !isSaving

    val canSave: Boolean
        get() = validationPreview?.valid == true &&
            selectedCategoryId != null &&
            !isValidating &&
            !isSaving &&
            error == null
}
```

### 5.2 Key State Rules (from `Guide.md`)
- Changing `rssUrlInput` clears the stale `validationPreview` and `error` (since the new URL has not been validated yet), while keeping `nameInput` and `selectedCategoryId` intact.
- When `validateUrl()` succeeds:
  - `validationPreview` is populated with `detectedTitle` and `detectedSiteName`.
  - If `validationPreview.suggestedCategory != null`, `selectedCategoryId` is updated to `suggestedCategory.id` (user can still tap any category chip to override).
  - If `nameInput` is blank, it auto-fills with `detectedTitle`.
- When `validateUrl()` or `saveSource()` fails:
  - `error` is populated with safe Vietnamese copy.
  - `nameInput`, `rssUrlInput`, and `selectedCategoryId` are preserved.
- Double-submission prevention:
  - While `isValidating` or `isSaving` is `true`, both Validate and Save buttons are disabled.

### 5.3 Visual Design (`AddRssSourceSheet.kt`)
- **Surface**: White `#FFFFFF`, top corner radius `20.dp`, padding `20.dp`.
- **Header**: Mint icon badge (`#CCFBF1`, `36.dp`) with Teal link icon (`#0F766E`), title `"Thêm luồng RSS mới"` (`18.sp`, bold, `#0F172A`), and circular close icon button (`#F1F5F9`).
- **Inputs**:
  - `"Tên hiển thị nguồn"` and `"Đường dẫn RSS (URL)"` with `13.sp` semi-bold labels (`#475569`).
  - Rounded `12.dp` fields on `#F1F5F9` subtle background, `2.dp` `#0F766E` focus border, `48.dp+` height.
  - Inline `"Kiểm tra"` button inside the URL field container with verified icon and loading indicator during validation.
- **Validation Feedback**:
  - **Valid Preview Card**: Soft `#F8FAFC` / `#F0FDFA` card with `1.dp` `#CCFBF1` border, `"✓ RSS feed hợp lệ"` badge in `#16A34A`, displaying `detectedTitle` and `detectedSiteName`.
  - **Invalid Error Card**: Soft `#FEF2F2` card with `#FECACA` border and `#DC2626` message + checklist hints.
- **Category Selector**:
  - `"Chuyên mục mặc định"` label + `LazyRow` of pill-shaped chips (`RoundedCornerShape(50)`).
  - Active chip: `#0F766E` background, `#FFFFFF` text. Inactive chip: `#F1F5F9` background, `#475569` text.
- **Action Bar**:
  - `"Hủy"` ghost button (`#F1F5F9` bg, `#475569` text) and `"Thêm nguồn"` primary Teal button (`#0F766E` bg, `#FFFFFF` text, `48.dp` height, `12.dp` radius).

---

## 6. Verification & Testing Strategy

1. **Unit Tests (`app/src/test/...`)**:
   - `RssFeedValidatorTest`: Valid RSS 2.0 XML, valid Atom XML, malformed XML (`RSS_FEED_INVALID`), invalid URL format.
   - `AddRssSourceViewModelTest`:
     - Preselects `suggestedCategory.id` on valid preview and allows manual category override.
     - Preserves `nameInput`, `rssUrlInput`, and `selectedCategoryId` on `RSS_FEED_INVALID` and `DUPLICATE_SOURCE`.
     - Prevents save before validation and prevents double-submission while loading.
     - Emits created `RssSource` with `status = "FETCHING"` and updates repository list.
2. **CI & Build Verification**:
   - `python3 scripts/ci/check-docs.py` passes.
   - `./gradlew testDebugUnitTest assembleDebug` passes with zero errors.
