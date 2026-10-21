# Code Style Rules

- **Language & Paradigms**: Modern Kotlin; functional & declarative Jetpack Compose.
- **Naming Conventions**:
  - Classes, Interfaces, Sealed Interfaces, Composables: `PascalCase` (`AddRssSourceViewModel`, `SourcePreviewCard`).
  - Functions & Methods: `camelCase` (`validateRssUrl()`, `createSource()`).
  - Variables, Properties: `camelCase` (`rssUrlInput`, `isLoading`).
  - Constants: `UPPER_SNAKE_CASE` (`DEFAULT_PAGE_SIZE`).
  - Compose Events/Callbacks: `on[Action]` (`onUrlChanged`, `onSaveClicked`, `onDismiss`).
- **Formatting**:
  - 4 spaces indentation (no tabs).
  - Trailing commas in multi-line parameter lists.
  - Max line length 100-120 chars.
- **Compose State Management**:
  - Hoist state; Composable functions should be stateless where possible.
  - Use `StateFlow` and immutable UI state data classes:
```kotlin
data class AddRssSourceUiState(
    val rssUrl: String = "",
    val detectedTitle: String? = null,
    val selectedCategoryId: Long? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
```
- **Null Safety**:
  - Explicitly handle nullable fields (`imageUrl`, `description`, `lastFetchedAt`). Never force unwrap with `!!`.
```kotlin
val relativeTime = article.publishedAt?.let { formatRelativeTime(it) } ?: "Gần đây"
```
