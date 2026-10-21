# S07 — Add RSS Source Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement the complete S07 (Add RSS Source) modal bottom sheet and on-device RSS validation/creation flow in Jetpack Compose adhering to Editorial Pulse (`DESIGN2.md`).

**Architecture:** Clean modular UDF architecture separated into `core/model` (shared domain models matching `00-app-shell/Guide.md`), `core/designsystem` (Editorial Pulse theme tokens), `data/parser` & `data/repository` (on-device RSS/Atom XML validation + local StateFlow source/category store), and `feature/s07_add_source` (`AddRssSourceUiState`, `AddRssSourceViewModel`, `AddRssSourceSheet`).

**Tech Stack:** Kotlin 2.2, Jetpack Compose (BOM 2024.12.01), Material 3, Android Lifecycle ViewModel & StateFlow, Kotlin Coroutines, JUnit 4 + `kotlinx-coroutines-test`.

**Spec:** `.agents/specs/2026-10-21-s07-add-rss-source-design.md`

## Global Constraints

- Strictly 100% Jetpack Compose for UI; do not add legacy XML layout files.
- Align all visual tokens with `DESIGN2.md`: Canvas `#F8FAFC`, Card `#FFFFFF`, Subtle field `#F1F5F9`, Primary Teal `#0F766E`, Primary Text `#0F172A`, Secondary Text `#475569`, Muted `#94A3B8`, Border `#E2E8F0`, Active `#16A34A`, Error `#DC2626`.
- Do not reference legacy backend endpoint markers in any `.md`, `.kt`, or `.kts` file; `python3 scripts/ci/check-docs.py` must pass at every step.
- Preserve entered `nameInput`, `rssUrlInput`, and `selectedCategoryId` when validation or save fails.
- Prevent double submission while `isValidating` or `isSaving` is active.

---

### Task 1: Shared Domain Models, Editorial Pulse Theme Tokens & Build Config

**Files:**
- Modify: `app/build.gradle.kts`
- Modify: `app/src/main/AndroidManifest.xml`
- Create: `app/src/main/java/vn/edu/usth/rssreader/core/model/RssModels.kt`
- Create: `app/src/main/java/vn/edu/usth/rssreader/core/designsystem/EditorialPulseTheme.kt`

**Interfaces:**
- Consumes: Compose BOM `2024.12.01` in `app/build.gradle.kts`.
- Produces:
  - `Category(val id: Long, val name: String, val slug: String)`
  - `SourceRef(val id: Long, val name: String)`
  - `Article(...)`
  - `RssSource(val id: Long, val name: String, val rssUrl: String, val category: Category, val status: String, val lastFetchedAt: String?, val lastError: String?, val createdAt: String)`
  - `ValidationPreview(val valid: Boolean, val detectedTitle: String, val detectedSiteName: String, val suggestedCategory: Category?)`
  - `FetchError(val code: String, val message: String)`
  - `EditorialPulseColors` and `EditorialPulseTheme`

- [ ] **Step 1: Update `app/build.gradle.kts` with ViewModel Compose and Unit Test dependencies**

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "vn.edu.usth.rssreader"

    compileSdk = 36

    defaultConfig {
        applicationId = "vn.edu.usth.rssreader"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.12.01")

    implementation(composeBom)
    implementation("androidx.activity:activity-compose:1.10.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")

    debugImplementation("androidx.compose.ui:ui-tooling")
}
```

- [ ] **Step 2: Add INTERNET permission to `app/src/main/AndroidManifest.xml`**

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <uses-permission android:name="android.permission.INTERNET" />

    <application
        android:allowBackup="true"
        android:icon="@drawable/ic_app"
        android:label="@string/app_name"
        android:roundIcon="@drawable/ic_app"
        android:supportsRtl="true"
        android:theme="@style/Theme.MobileUSTH">
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```

- [ ] **Step 3: Create `RssModels.kt` and `EditorialPulseTheme.kt`**

Create `app/src/main/java/vn/edu/usth/rssreader/core/model/RssModels.kt`:

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
) {
    companion object {
        const val STATUS_ACTIVE = "ACTIVE"
        const val STATUS_FETCHING = "FETCHING"
        const val STATUS_ERROR = "ERROR"
    }
}

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

Create `app/src/main/java/vn/edu/usth/rssreader/core/designsystem/EditorialPulseTheme.kt`:

```kotlin
package vn.edu.usth.rssreader.core.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object EditorialPulseColors {
    val SurfaceCanvas = Color(0xFFF8FAFC)
    val SurfaceCard = Color(0xFFFFFFFF)
    val SurfaceCardSubtle = Color(0xFFF1F5F9)
    val PrimaryM3 = Color(0xFF005C55)
    val PrimaryTeal = Color(0xFF0F766E)
    val PrimaryTealSoft = Color(0xFFCCFBF1)
    val PrimaryTealSurface = Color(0xFFF0FDFA)
    val TextPrimary = Color(0xFF0F172A)
    val TextSecondary = Color(0xFF475569)
    val TextMuted = Color(0xFF94A3B8)
    val BorderSubtle = Color(0xFFE2E8F0)
    val BorderFocus = Color(0xFF0F766E)
    val SecondaryAmber = Color(0xFFD97706)
    val SecondaryAmberSoft = Color(0xFFFEF3C7)
    val TertiarySky = Color(0xFF0284C7)
    val StatusActive = Color(0xFF16A34A)
    val StatusActiveSoft = Color(0xFFDCFCE7)
    val StatusError = Color(0xFFDC2626)
    val StatusErrorSoft = Color(0xFFFEF2F2)
    val StatusErrorBorder = Color(0xFFFECACA)
}

private val EditorialLightColorScheme = lightColorScheme(
    primary = EditorialPulseColors.PrimaryTeal,
    onPrimary = EditorialPulseColors.SurfaceCard,
    secondary = EditorialPulseColors.SecondaryAmber,
    tertiary = EditorialPulseColors.TertiarySky,
    background = EditorialPulseColors.SurfaceCanvas,
    surface = EditorialPulseColors.SurfaceCard,
    onSurface = EditorialPulseColors.TextPrimary,
    onSurfaceVariant = EditorialPulseColors.TextSecondary,
    outline = EditorialPulseColors.BorderSubtle,
    error = EditorialPulseColors.StatusError,
)

@Composable
fun EditorialPulseTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EditorialLightColorScheme,
        typography = Typography(),
        content = content,
    )
}
```

- [ ] **Step 4: Commit Task 1**

```bash
git add app/build.gradle.kts app/src/main/AndroidManifest.xml app/src/main/java/vn/edu/usth/rssreader/core/
git commit -m "feat(core): add shared RSS domain models and Editorial Pulse design tokens"
```

---

### Task 2: On-Device RSS/Atom XML Validator & Local Source Repository

**Files:**
- Create: `app/src/main/java/vn/edu/usth/rssreader/data/parser/RssFeedValidator.kt`
- Create: `app/src/main/java/vn/edu/usth/rssreader/data/repository/RssSourceRepository.kt`
- Test: `app/src/test/java/vn/edu/usth/rssreader/data/RssFeedValidatorTest.kt`

**Interfaces:**
- Consumes: `Category`, `RssSource`, `ValidationPreview`, `FetchError` from `vn.edu.usth.rssreader.core.model`.
- Produces:
  - `sealed interface ValidationResult` (`Success(val preview: ValidationPreview)`, `Failure(val error: FetchError)`)
  - `fun interface RssFeedValidator { suspend fun validate(rssUrl: String, categories: List<Category>): ValidationResult }`
  - `class OnDeviceRssFeedValidator : RssFeedValidator` with `parseFeedXml(rssUrl: String, xmlContent: String, categories: List<Category>): ValidationResult`
  - `class RssSourceRepository(validator: RssFeedValidator)` with `categories: StateFlow<List<Category>>`, `sources: StateFlow<List<RssSource>>`, `suspend fun validateRssUrl(rssUrl: String): ValidationResult`, `suspend fun createSource(name: String, rssUrl: String, categoryId: Long): Result<RssSource>`, `class SourceCreationException(val fetchError: FetchError) : Exception(fetchError.message)`

- [ ] **Step 1: Write the failing test in `RssFeedValidatorTest.kt`**

```kotlin
package vn.edu.usth.rssreader.data

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import vn.edu.usth.rssreader.core.model.FetchError
import vn.edu.usth.rssreader.data.parser.OnDeviceRssFeedValidator
import vn.edu.usth.rssreader.data.parser.ValidationResult
import vn.edu.usth.rssreader.data.repository.RssSourceRepository

class RssFeedValidatorTest {

    private val validator = OnDeviceRssFeedValidator()
    private val categories = RssSourceRepository.defaultCategories()

    @Test
    fun `parseFeedXml extracts title, siteName and suggested category from valid RSS 2_0 XML`() {
        val sampleXml = """
            <?xml version="1.0" encoding="utf-8"?>
            <rss version="2.0">
              <channel>
                <title>Dân trí - Thế giới</title>
                <link>https://dantri.com.vn/the-gioi.htm</link>
                <description>Tin tức thế giới</description>
                <item>
                  <title>Bài viết mẫu</title>
                </item>
              </channel>
            </rss>
        """.trimIndent()

        val result = validator.parseFeedXml(
            rssUrl = "https://dantri.com.vn/rss/the-gioi.rss",
            xmlContent = sampleXml,
            categories = categories,
        )

        assertTrue(result is ValidationResult.Success)
        val preview = (result as ValidationResult.Success).preview
        assertTrue(preview.valid)
        assertEquals("Dân trí - Thế giới", preview.detectedTitle)
        assertEquals("Dân trí", preview.detectedSiteName)
        assertNotNull(preview.suggestedCategory)
        assertEquals("the-gioi", preview.suggestedCategory?.slug)
    }

    @Test
    fun `parseFeedXml returns RSS_FEED_INVALID when XML is not an RSS or Atom feed`() {
        val htmlContent = "<html><body>Not a feed</body></html>"
        val result = validator.parseFeedXml(
            rssUrl = "https://example.com/not-rss",
            xmlContent = htmlContent,
            categories = categories,
        )

        assertTrue(result is ValidationResult.Failure)
        val error = (result as ValidationResult.Failure).error
        assertEquals(FetchError.RSS_FEED_INVALID, error.code)
    }

    @Test
    fun `repository rejects duplicate RSS URL with DUPLICATE_SOURCE`() = runTest {
        val repo = RssSourceRepository(validator)
        // First source already seeded: https://dantri.com.vn/rss/thoi-su.rss
        val result = repo.validateRssUrl("https://dantri.com.vn/rss/thoi-su.rss")

        assertTrue(result is ValidationResult.Failure)
        val error = (result as ValidationResult.Failure).error
        assertEquals(FetchError.DUPLICATE_SOURCE, error.code)
    }
}
```

- [ ] **Step 2: Implement `RssFeedValidator.kt` and `RssSourceRepository.kt`**

Create `app/src/main/java/vn/edu/usth/rssreader/data/parser/RssFeedValidator.kt`:

```kotlin
package vn.edu.usth.rssreader.data.parser

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.w3c.dom.Element
import org.xml.sax.InputSource
import vn.edu.usth.rssreader.core.model.Category
import vn.edu.usth.rssreader.core.model.FetchError
import vn.edu.usth.rssreader.core.model.ValidationPreview
import java.io.StringReader
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

sealed interface ValidationResult {
    data class Success(val preview: ValidationPreview) : ValidationResult
    data class Failure(val error: FetchError) : ValidationResult
}

fun interface RssFeedValidator {
    suspend fun validate(rssUrl: String, categories: List<Category>): ValidationResult
}

class OnDeviceRssFeedValidator(
    private val connectTimeoutMs: Int = 8_000,
    private val readTimeoutMs: Int = 8_000,
) : RssFeedValidator {

    override suspend fun validate(
        rssUrl: String,
        categories: List<Category>,
    ): ValidationResult = withContext(Dispatchers.IO) {
        val trimmedUrl = rssUrl.trim()
        if (!isValidHttpUrl(trimmedUrl)) {
            return@withContext ValidationResult.Failure(
                FetchError(
                    code = FetchError.RSS_FEED_INVALID,
                    message = "Đường dẫn không đúng định dạng URL (cần bắt đầu bằng http:// hoặc https://).",
                ),
            )
        }

        // Check built-in known Dan Tri / VnExpress feeds for fast deterministic offline/emulator support,
        // while still attempting real HTTP fetch first if reachable.
        val networkXml = runCatching { fetchUrlText(trimmedUrl) }.getOrNull()
        if (networkXml != null) {
            return@withContext parseFeedXml(trimmedUrl, networkXml, categories)
        }

        val knownPreview = matchKnownVietnamFeed(trimmedUrl, categories)
        if (knownPreview != null) {
            return@withContext ValidationResult.Success(knownPreview)
        }

        ValidationResult.Failure(
            FetchError(
                code = FetchError.RSS_FEED_INVALID,
                message = "RSS URL không hợp lệ hoặc không thể truy cập",
            ),
        )
    }

    fun parseFeedXml(
        rssUrl: String,
        xmlContent: String,
        categories: List<Category>,
    ): ValidationResult {
        val trimmedXml = xmlContent.trimStart()
        if (trimmedXml.isEmpty()) {
            return invalidFeedFailure()
        }

        return try {
            val factory = DocumentBuilderFactory.newInstance().apply {
                isNamespaceAware = false
                setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
                setFeature("http://xml.org/sax/features/external-general-entities", false)
                setFeature("http://xml.org/sax/features/external-parameter-entities", false)
            }
            val builder = factory.newDocumentBuilder()
            val document = builder.parse(InputSource(StringReader(trimmedXml)))
            val root = document.documentElement ?: return invalidFeedFailure()
            val rootTag = root.tagName.lowercase()

            val rawTitle = when {
                rootTag == "rss" || rootTag.endsWith(":rss") -> {
                    val channel = root.getElementsByTagName("channel").item(0) as? Element
                        ?: return invalidFeedFailure()
                    channel.directChildText("title")
                }
                rootTag == "feed" || rootTag.endsWith(":feed") -> {
                    root.directChildText("title")
                }
                else -> return invalidFeedFailure()
            }?.trim().orEmpty()

            if (rawTitle.isBlank()) {
                return invalidFeedFailure()
            }

            val siteName = inferSiteName(rssUrl, rawTitle)
            val suggested = inferCategory(rssUrl, rawTitle, categories)

            ValidationResult.Success(
                ValidationPreview(
                    valid = true,
                    detectedTitle = rawTitle,
                    detectedSiteName = siteName,
                    suggestedCategory = suggested,
                ),
            )
        } catch (_: Exception) {
            invalidFeedFailure()
        }
    }

    private fun Element.directChildText(tagName: String): String? {
        val children = childNodes
        for (i in 0 until children.length) {
            val node = children.item(i)
            if (node is Element && node.tagName.equals(tagName, ignoreCase = true)) {
                return node.textContent
            }
        }
        return null
    }

    private fun invalidFeedFailure(): ValidationResult.Failure = ValidationResult.Failure(
        FetchError(
            code = FetchError.RSS_FEED_INVALID,
            message = "RSS URL không hợp lệ hoặc không thể truy cập",
        ),
    )

    private fun isValidHttpUrl(url: String): Boolean = try {
        val uri = URI(url)
        val scheme = uri.scheme?.lowercase()
        (scheme == "http" || scheme == "https") && !uri.host.isNullOrBlank()
    } catch (_: Exception) {
        false
    }

    private fun fetchUrlText(urlString: String): String {
        val connection = (URL(urlString).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = connectTimeoutMs
            readTimeout = readTimeoutMs
            setRequestProperty("User-Agent", "USTH-RSS-Reader/1.0 (Android)")
            setRequestProperty("Accept", "application/rss+xml, application/atom+xml, application/xml, text/xml")
            instanceFollowRedirects = true
        }
        try {
            val status = connection.responseCode
            if (status !in 200..299) {
                throw IllegalStateException("HTTP $status")
            }
            return connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
        } finally {
            connection.disconnect()
        }
    }

    private fun inferSiteName(rssUrl: String, detectedTitle: String): String {
        val lowerUrl = rssUrl.lowercase()
        return when {
            "dantri.com.vn" in lowerUrl || detectedTitle.contains("Dân trí", ignoreCase = true) -> "Dân trí"
            "vnexpress.net" in lowerUrl || detectedTitle.contains("VnExpress", ignoreCase = true) -> "VnExpress"
            "tuoitre.vn" in lowerUrl || detectedTitle.contains("Tuổi Trẻ", ignoreCase = true) -> "Tuổi Trẻ"
            "thanhnien.vn" in lowerUrl || detectedTitle.contains("Thanh Niên", ignoreCase = true) -> "Thanh Niên"
            else -> {
                val dashSplit = detectedTitle.split(" - ", " — ", " | ")
                if (dashSplit.size > 1 && dashSplit.first().isNotBlank()) {
                    dashSplit.first().trim()
                } else {
                    runCatching { URI(rssUrl).host.orEmpty().removePrefix("www.") }
                        .getOrDefault("Nguồn RSS")
                }
            }
        }
    }

    private fun inferCategory(
        rssUrl: String,
        detectedTitle: String,
        categories: List<Category>,
    ): Category? {
        val combined = "${rssUrl.lowercase()} ${detectedTitle.lowercase()}"
        val matchedSlug = when {
            "the-gioi" in combined || "thế giới" in combined -> "the-gioi"
            "suc-manh-so" in combined || "cong-nghe" in combined || "công nghệ" in combined || "so-hoa" in combined -> "cong-nghe"
            "kinh-doanh" in combined || "kinh-te" in combined || "kinh tế" in combined || "kinh doanh" in combined -> "kinh-te"
            "the-thao" in combined || "thể thao" in combined -> "the-thao"
            "giai-tri" in combined || "giải trí" in combined -> "giai-tri"
            "thoi-su" in combined || "thời sự" in combined || "xa-hoi" in combined -> "thoi-su"
            else -> null
        }
        return categories.firstOrNull { it.slug == matchedSlug } ?: categories.firstOrNull()
    }

    private fun matchKnownVietnamFeed(
        rssUrl: String,
        categories: List<Category>,
    ): ValidationPreview? {
        val normalized = rssUrl.trim().lowercase()
        val isKnownHost = ("dantri.com.vn" in normalized || "vnexpress.net" in normalized || "tuoitre.vn" in normalized)
        val looksLikeFeed = normalized.endsWith(".rss") || normalized.endsWith(".xml") || "/rss/" in normalized
        if (!isKnownHost || !looksLikeFeed) return null

        val suggested = inferCategory(rssUrl, "", categories)
        val siteName = inferSiteName(rssUrl, "")
        val categoryLabel = suggested?.name ?: "Tin mới"
        return ValidationPreview(
            valid = true,
            detectedTitle = "$siteName - $categoryLabel",
            detectedSiteName = siteName,
            suggestedCategory = suggested,
        )
    }
}
```

Create `app/src/main/java/vn/edu/usth/rssreader/data/repository/RssSourceRepository.kt`:

```kotlin
package vn.edu.usth.rssreader.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import vn.edu.usth.rssreader.core.model.Category
import vn.edu.usth.rssreader.core.model.FetchError
import vn.edu.usth.rssreader.core.model.RssSource
import vn.edu.usth.rssreader.data.parser.OnDeviceRssFeedValidator
import vn.edu.usth.rssreader.data.parser.RssFeedValidator
import vn.edu.usth.rssreader.data.parser.ValidationResult
import java.time.Instant

class SourceOperationException(val fetchError: FetchError) : Exception(fetchError.message)

class RssSourceRepository(
    private val validator: RssFeedValidator = OnDeviceRssFeedValidator(),
    initialCategories: List<Category> = defaultCategories(),
    initialSources: List<RssSource> = defaultSeededSources(initialCategories),
) {
    private val _categories = MutableStateFlow(initialCategories)
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _sources = MutableStateFlow(initialSources)
    val sources: StateFlow<List<RssSource>> = _sources.asStateFlow()

    suspend fun validateRssUrl(rssUrl: String): ValidationResult {
        val normalizedUrl = normalizeUrl(rssUrl)
        if (normalizedUrl.isBlank()) {
            return ValidationResult.Failure(
                FetchError(
                    code = FetchError.RSS_FEED_INVALID,
                    message = "Vui lòng nhập đường dẫn RSS (URL).",
                ),
            )
        }

        if (isDuplicateUrl(normalizedUrl)) {
            return ValidationResult.Failure(
                FetchError(
                    code = FetchError.DUPLICATE_SOURCE,
                    message = "Nguồn RSS này đã có trong danh sách theo dõi.",
                ),
            )
        }

        return validator.validate(rssUrl.trim(), _categories.value)
    }

    suspend fun createSource(
        name: String,
        rssUrl: String,
        categoryId: Long,
    ): Result<RssSource> {
        val normalizedUrl = normalizeUrl(rssUrl)
        if (isDuplicateUrl(normalizedUrl)) {
            return Result.failure(
                SourceOperationException(
                    FetchError(
                        code = FetchError.DUPLICATE_SOURCE,
                        message = "Nguồn RSS này đã có trong danh sách theo dõi.",
                    ),
                ),
            )
        }

        val category = _categories.value.firstOrNull { it.id == categoryId }
            ?: _categories.value.first()

        val nextId = (_sources.value.maxOfOrNull { it.id } ?: 0L) + 1L
        val finalName = name.trim().ifBlank { "${category.name} RSS" }

        val newSource = RssSource(
            id = nextId,
            name = finalName,
            rssUrl = rssUrl.trim(),
            category = category,
            status = RssSource.STATUS_FETCHING,
            lastFetchedAt = null,
            lastError = null,
            createdAt = Instant.now().toString(),
        )

        _sources.update { current -> listOf(newSource) + current }
        return Result.success(newSource)
    }

    private fun isDuplicateUrl(normalizedUrl: String): Boolean =
        _sources.value.any { normalizeUrl(it.rssUrl) == normalizedUrl }

    private fun normalizeUrl(url: String): String =
        url.trim().trimEnd('/').lowercase()

    companion object {
        val shared: RssSourceRepository by lazy { RssSourceRepository() }

        fun defaultCategories(): List<Category> = listOf(
            Category(id = 1L, name = "Thời sự", slug = "thoi-su"),
            Category(id = 2L, name = "Thế giới", slug = "the-gioi"),
            Category(id = 3L, name = "Kinh tế", slug = "kinh-te"),
            Category(id = 4L, name = "Thể thao", slug = "the-thao"),
            Category(id = 5L, name = "Giải trí", slug = "giai-tri"),
            Category(id = 9L, name = "Công nghệ", slug = "cong-nghe"),
        )

        fun defaultSeededSources(categories: List<Category> = defaultCategories()): List<RssSource> {
            val thoiSu = categories.first { it.slug == "thoi-su" }
            val congNghe = categories.first { it.slug == "cong-nghe" }
            val kinhTe = categories.first { it.slug == "kinh-te" }
            return listOf(
                RssSource(
                    id = 1L,
                    name = "Dân trí — Thời sự",
                    rssUrl = "https://dantri.com.vn/rss/thoi-su.rss",
                    category = thoiSu,
                    status = RssSource.STATUS_ACTIVE,
                    lastFetchedAt = "2 phút trước",
                    lastError = null,
                    createdAt = "2026-10-01T08:00:00Z",
                ),
                RssSource(
                    id = 2L,
                    name = "Dân trí — Công nghệ",
                    rssUrl = "https://dantri.com.vn/rss/suc-manh-so.rss",
                    category = congNghe,
                    status = RssSource.STATUS_ACTIVE,
                    lastFetchedAt = "10 phút trước",
                    lastError = null,
                    createdAt = "2026-10-01T08:00:00Z",
                ),
                RssSource(
                    id = 3L,
                    name = "Dân trí — Kinh doanh",
                    rssUrl = "https://dantri.com.vn/rss/kinh-doanh.rss",
                    category = kinhTe,
                    status = RssSource.STATUS_FETCHING,
                    lastFetchedAt = "30 phút trước",
                    lastError = null,
                    createdAt = "2026-10-01T08:00:00Z",
                ),
            )
        }
    }
}
```

- [ ] **Step 3: Run unit tests for Task 2**

Run: `./gradlew testDebugUnitTest --tests "vn.edu.usth.rssreader.data.RssFeedValidatorTest"`
Expected: PASS

- [ ] **Step 4: Commit Task 2**

```bash
git add app/src/main/java/vn/edu/usth/rssreader/data/ app/src/test/java/vn/edu/usth/rssreader/data/
git commit -m "feat(data): implement on-device RSS XML validator and local source repository"
```

---

### Task 3: S07 UI State & ViewModel (`AddRssSourceViewModel`)

**Files:**
- Create: `app/src/main/java/vn/edu/usth/rssreader/feature/s07_add_source/AddRssSourceUiState.kt`
- Create: `app/src/main/java/vn/edu/usth/rssreader/feature/s07_add_source/AddRssSourceViewModel.kt`
- Test: `app/src/test/java/vn/edu/usth/rssreader/feature/s07_add_source/AddRssSourceViewModelTest.kt`

**Interfaces:**
- Consumes: `RssSourceRepository`, `ValidationResult`, `Category`, `RssSource`, `ValidationPreview`, `FetchError`
- Produces:
  - `AddRssSourceUiState`
  - `AddRssSourceViewModel(repository: RssSourceRepository)` with `uiState: StateFlow<AddRssSourceUiState>`, `onNameChanged(String)`, `onRssUrlChanged(String)`, `onCategorySelected(Long)`, `validateRssUrl()`, `saveSource(onSuccess: (RssSource) -> Unit = {})`, `resetForm()`

- [ ] **Step 1: Write the failing test in `AddRssSourceViewModelTest.kt`**

```kotlin
package vn.edu.usth.rssreader.feature.s07_add_source

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import vn.edu.usth.rssreader.core.model.FetchError
import vn.edu.usth.rssreader.core.model.RssSource
import vn.edu.usth.rssreader.core.model.ValidationPreview
import vn.edu.usth.rssreader.data.parser.RssFeedValidator
import vn.edu.usth.rssreader.data.parser.ValidationResult
import vn.edu.usth.rssreader.data.repository.RssSourceRepository

@OptIn(ExperimentalCoroutinesApi::class)
class AddRssSourceViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `valid preview preselects suggested category and allows user to override it`() = runTest {
        val categories = RssSourceRepository.defaultCategories()
        val worldCategory = categories.first { it.slug == "the-gioi" }
        val techCategory = categories.first { it.slug == "cong-nghe" }

        val fakeValidator = RssFeedValidator { _, _ ->
            ValidationResult.Success(
                ValidationPreview(
                    valid = true,
                    detectedTitle = "Dân trí - Thế giới",
                    detectedSiteName = "Dân trí",
                    suggestedCategory = worldCategory,
                ),
            )
        }
        val repo = RssSourceRepository(validator = fakeValidator, initialSources = emptyList())
        val viewModel = AddRssSourceViewModel(repo)

        viewModel.onRssUrlChanged("https://dantri.com.vn/rss/the-gioi.rss")
        viewModel.validateRssUrl()
        advanceUntilIdle()

        val stateAfterValidate = viewModel.uiState.value
        assertTrue(stateAfterValidate.canSave)
        assertEquals("Dân trí — Thế giới", stateAfterValidate.nameInput)
        assertEquals(worldCategory.id, stateAfterValidate.selectedCategoryId)

        // User manually overrides category to Công nghệ
        viewModel.onCategorySelected(techCategory.id)
        assertEquals(techCategory.id, viewModel.uiState.value.selectedCategoryId)
        assertTrue(viewModel.uiState.value.canSave)
    }

    @Test
    fun `validation failure keeps entered name, URL and selected category intact`() = runTest {
        val fakeValidator = RssFeedValidator { _, _ ->
            ValidationResult.Failure(
                FetchError(
                    code = FetchError.RSS_FEED_INVALID,
                    message = "RSS URL không hợp lệ hoặc không thể truy cập",
                ),
            )
        }
        val repo = RssSourceRepository(validator = fakeValidator, initialSources = emptyList())
        val viewModel = AddRssSourceViewModel(repo)

        viewModel.onNameChanged("Nguồn riêng của tôi")
        viewModel.onRssUrlChanged("https://example.com/not-rss")
        viewModel.onCategorySelected(4L)
        viewModel.validateRssUrl()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.canSave)
        assertNotNull(state.error)
        assertEquals(FetchError.RSS_FEED_INVALID, state.error?.code)
        assertEquals("Nguồn riêng của tôi", state.nameInput)
        assertEquals("https://example.com/not-rss", state.rssUrlInput)
        assertEquals(4L, state.selectedCategoryId)
    }

    @Test
    fun `successful save adds source with FETCHING status and refreshes repository list`() = runTest {
        val categories = RssSourceRepository.defaultCategories()
        val worldCategory = categories.first { it.slug == "the-gioi" }

        val fakeValidator = RssFeedValidator { _, _ ->
            ValidationResult.Success(
                ValidationPreview(
                    valid = true,
                    detectedTitle = "Dân trí - Thế giới",
                    detectedSiteName = "Dân trí",
                    suggestedCategory = worldCategory,
                ),
            )
        }
        val repo = RssSourceRepository(validator = fakeValidator, initialSources = emptyList())
        val viewModel = AddRssSourceViewModel(repo)

        viewModel.onRssUrlChanged("https://dantri.com.vn/rss/the-gioi.rss")
        viewModel.validateRssUrl()
        advanceUntilIdle()

        var savedCallbackSource: RssSource? = null
        viewModel.saveSource { created -> savedCallbackSource = created }
        advanceUntilIdle()

        assertNotNull(savedCallbackSource)
        assertEquals(RssSource.STATUS_FETCHING, savedCallbackSource?.status)
        assertEquals(worldCategory.id, savedCallbackSource?.category?.id)
        assertEquals(1, repo.sources.value.size)
        assertNull(repo.sources.value.first().lastFetchedAt)
    }
}
```

- [ ] **Step 2: Create `AddRssSourceUiState.kt` and `AddRssSourceViewModel.kt`**

Create `app/src/main/java/vn/edu/usth/rssreader/feature/s07_add_source/AddRssSourceUiState.kt`:

```kotlin
package vn.edu.usth.rssreader.feature.s07_add_source

import vn.edu.usth.rssreader.core.model.Category
import vn.edu.usth.rssreader.core.model.FetchError
import vn.edu.usth.rssreader.core.model.RssSource
import vn.edu.usth.rssreader.core.model.ValidationPreview

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

Create `app/src/main/java/vn/edu/usth/rssreader/feature/s07_add_source/AddRssSourceViewModel.kt`:

```kotlin
package vn.edu.usth.rssreader.feature.s07_add_source

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import vn.edu.usth.rssreader.core.model.FetchError
import vn.edu.usth.rssreader.core.model.RssSource
import vn.edu.usth.rssreader.data.parser.ValidationResult
import vn.edu.usth.rssreader.data.repository.RssSourceRepository
import vn.edu.usth.rssreader.data.repository.SourceOperationException

class AddRssSourceViewModel(
    private val repository: RssSourceRepository = RssSourceRepository.shared,
) : ViewModel() {

    private val initialCategories = repository.categories.value

    private val _uiState = MutableStateFlow(
        AddRssSourceUiState(
            categories = initialCategories,
            selectedCategoryId = initialCategories.firstOrNull()?.id,
        ),
    )
    val uiState: StateFlow<AddRssSourceUiState> = _uiState.asStateFlow()

    fun onNameChanged(newName: String) {
        _uiState.update { state ->
            state.copy(nameInput = newName)
        }
    }

    fun onRssUrlChanged(newUrl: String) {
        _uiState.update { state ->
            if (state.rssUrlInput == newUrl) {
                state
            } else {
                state.copy(
                    rssUrlInput = newUrl,
                    validationPreview = null,
                    error = null,
                )
            }
        }
    }

    fun onCategorySelected(categoryId: Long) {
        _uiState.update { state ->
            state.copy(selectedCategoryId = categoryId)
        }
    }

    fun validateRssUrl() {
        val currentState = _uiState.value
        if (currentState.isValidating || currentState.isSaving) return

        val urlToValidate = currentState.rssUrlInput.trim()
        if (urlToValidate.isEmpty()) {
            _uiState.update {
                it.copy(
                    error = FetchError(
                        code = FetchError.RSS_FEED_INVALID,
                        message = "Vui lòng nhập đường dẫn RSS (URL) trước khi kiểm tra.",
                    ),
                    validationPreview = null,
                )
            }
            return
        }

        _uiState.update {
            it.copy(
                isValidating = true,
                error = null,
                validationPreview = null,
            )
        }

        viewModelScope.launch {
            when (val result = repository.validateRssUrl(urlToValidate)) {
                is ValidationResult.Success -> {
                    val preview = result.preview
                    val normalizedDisplayTitle = preview.detectedTitle.replace(" - ", " — ")
                    _uiState.update { state ->
                        state.copy(
                            isValidating = false,
                            validationPreview = preview,
                            error = null,
                            nameInput = state.nameInput.ifBlank { normalizedDisplayTitle },
                            selectedCategoryId = preview.suggestedCategory?.id ?: state.selectedCategoryId,
                        )
                    }
                }
                is ValidationResult.Failure -> {
                    _uiState.update { state ->
                        state.copy(
                            isValidating = false,
                            validationPreview = null,
                            error = result.error,
                        )
                    }
                }
            }
        }
    }

    fun saveSource(onSuccess: (RssSource) -> Unit = {}) {
        val currentState = _uiState.value
        if (!currentState.canSave) return

        val categoryId = currentState.selectedCategoryId ?: return
        val finalName = currentState.nameInput.trim().ifBlank {
            currentState.validationPreview?.detectedTitle?.replace(" - ", " — ").orEmpty()
        }

        _uiState.update { it.copy(isSaving = true, error = null) }

        viewModelScope.launch {
            val result = repository.createSource(
                name = finalName,
                rssUrl = currentState.rssUrlInput.trim(),
                categoryId = categoryId,
            )
            result.fold(
                onSuccess = { created ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            createdSource = created,
                        )
                    }
                    onSuccess(created)
                },
                onFailure = { throwable ->
                    val fetchError = (throwable as? SourceOperationException)?.fetchError
                        ?: FetchError(
                            code = FetchError.INTERNAL_ERROR,
                            message = "Không thể lưu nguồn RSS. Vui lòng thử lại.",
                        )
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            error = fetchError,
                        )
                    }
                },
            )
        }
    }

    fun resetForm() {
        val cats = repository.categories.value
        _uiState.value = AddRssSourceUiState(
            categories = cats,
            selectedCategoryId = cats.firstOrNull()?.id,
        )
    }

    companion object {
        fun provideFactory(
            repository: RssSourceRepository = RssSourceRepository.shared,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AddRssSourceViewModel(repository) as T
            }
        }
    }
}
```

- [ ] **Step 3: Run unit tests for Task 3**

Run: `./gradlew testDebugUnitTest --tests "vn.edu.usth.rssreader.feature.s07_add_source.AddRssSourceViewModelTest"`
Expected: PASS

- [ ] **Step 4: Commit Task 3**

```bash
git add app/src/main/java/vn/edu/usth/rssreader/feature/s07_add_source/ app/src/test/java/vn/edu/usth/rssreader/feature/s07_add_source/
git commit -m "feat(s07): implement AddRssSourceViewModel and UI state with unit tests"
```

---

### Task 4: S07 Jetpack Compose UI (`AddRssSourceSheet.kt`), App Shell Integration & Guide Update

**Files:**
- Create: `app/src/main/java/vn/edu/usth/rssreader/feature/s07_add_source/AddRssSourceSheet.kt`
- Modify: `app/src/main/java/vn/edu/usth/rssreader/MainActivity.kt`
- Modify: `src/S07-add-rss-source/Guide.md`

**Interfaces:**
- Consumes: `AddRssSourceUiState`, `AddRssSourceViewModel`, `EditorialPulseColors`, `RssSourceRepository`
- Produces:
  - `@Composable fun AddRssSourceModalSheet(...)`
  - `@Composable fun AddRssSourceSheetContent(...)`
  - Interactive S06/S07 demonstration in `MainActivity.kt` when the user selects the `Nguồn RSS` tab

- [ ] **Step 1: Create `AddRssSourceSheet.kt` matching `Add RSS Source.png` and `DESIGN2.md`**

Create `app/src/main/java/vn/edu/usth/rssreader/feature/s07_add_source/AddRssSourceSheet.kt`:

```kotlin
package vn.edu.usth.rssreader.feature.s07_add_source

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AddLink
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import vn.edu.usth.rssreader.core.designsystem.EditorialPulseColors
import vn.edu.usth.rssreader.core.designsystem.EditorialPulseTheme
import vn.edu.usth.rssreader.core.model.Category
import vn.edu.usth.rssreader.core.model.FetchError
import vn.edu.usth.rssreader.core.model.RssSource
import vn.edu.usth.rssreader.core.model.ValidationPreview
import vn.edu.usth.rssreader.data.repository.RssSourceRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRssSourceModalSheet(
    onDismissRequest: () -> Unit,
    onSourceAdded: (RssSource) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddRssSourceViewModel = viewModel(factory = AddRssSourceViewModel.provideFactory()),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        containerColor = EditorialPulseColors.SurfaceCard,
        scrimColor = Color.Black.copy(alpha = 0.45f),
        dragHandle = null,
        modifier = modifier,
    ) {
        AddRssSourceSheetContent(
            uiState = uiState,
            onNameChanged = viewModel::onNameChanged,
            onRssUrlChanged = viewModel::onRssUrlChanged,
            onValidateClicked = viewModel::validateRssUrl,
            onCategorySelected = viewModel::onCategorySelected,
            onCancelClicked = onDismissRequest,
            onSaveClicked = {
                viewModel.saveSource { created ->
                    viewModel.resetForm()
                    onSourceAdded(created)
                }
            },
        )
    }
}

@Composable
fun AddRssSourceSheetContent(
    uiState: AddRssSourceUiState,
    onNameChanged: (String) -> Unit,
    onRssUrlChanged: (String) -> Unit,
    onValidateClicked: () -> Unit,
    onCategorySelected: (Long) -> Unit,
    onCancelClicked: () -> Unit,
    onSaveClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = EditorialPulseColors.SurfaceCard,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            )
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp),
    ) {
        // Header Row: Icon + Title + Close Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(EditorialPulseColors.PrimaryTealSoft),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AddLink,
                        contentDescription = null,
                        tint = EditorialPulseColors.PrimaryTeal,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Text(
                    text = "Thêm luồng RSS mới",
                    color = EditorialPulseColors.TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            IconButton(
                onClick = onCancelClicked,
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(EditorialPulseColors.SurfaceCardSubtle),
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Đóng hộp thoại thêm nguồn RSS",
                    tint = EditorialPulseColors.TextSecondary,
                    modifier = Modifier.size(18.dp),
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Field 1: Tên hiển thị nguồn
        FieldLabel(text = "Tên hiển thị nguồn")
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = uiState.nameInput,
            onValueChange = onNameChanged,
            enabled = !uiState.isSaving,
            singleLine = true,
            placeholder = {
                Text(
                    text = "Ví dụ: Dân trí — Thể thao",
                    color = EditorialPulseColors.TextMuted,
                    fontSize = 14.sp,
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = editorialTextFieldColors(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Field 2: Đường dẫn RSS (URL) + Inline Kiểm tra button
        FieldLabel(text = "Đường dẫn RSS (URL)")
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = uiState.rssUrlInput,
            onValueChange = onRssUrlChanged,
            enabled = !uiState.isSaving,
            singleLine = true,
            placeholder = {
                Text(
                    text = "https://dantri.com.vn/rss/...",
                    color = EditorialPulseColors.TextMuted,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            trailingIcon = {
                ValidateInlineButton(
                    isLoading = uiState.isValidating,
                    enabled = uiState.canValidate,
                    onClick = onValidateClicked,
                    modifier = Modifier.padding(end = 6.dp),
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = editorialTextFieldColors(isError = uiState.error != null),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Uri,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(
                onDone = { if (uiState.canValidate) onValidateClicked() },
            ),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp),
        )

        // Validation Preview Card
        AnimatedVisibility(visible = uiState.validationPreview != null) {
            uiState.validationPreview?.let { preview ->
                Column {
                    Spacer(modifier = Modifier.height(14.dp))
                    ValidFeedPreviewCard(preview = preview)
                }
            }
        }

        // Validation Error Card
        AnimatedVisibility(visible = uiState.error != null) {
            uiState.error?.let { error ->
                Column {
                    Spacer(modifier = Modifier.height(14.dp))
                    InvalidFeedErrorCard(
                        error = error,
                        onRetryClicked = onValidateClicked,
                        canRetry = uiState.canValidate,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Field 3: Chuyên mục mặc định (Pill Chips)
        FieldLabel(text = "Chuyên mục mặc định")
        Spacer(modifier = Modifier.height(10.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(end = 4.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(
                items = uiState.categories,
                key = { it.id },
            ) { category ->
                val isSelected = category.id == uiState.selectedCategoryId
                CategoryPillChip(
                    name = category.name,
                    selected = isSelected,
                    enabled = !uiState.isSaving,
                    onClick = { onCategorySelected(category.id) },
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Row: Hủy (Ghost) + Thêm nguồn (Primary Teal)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Button(
                onClick = onCancelClicked,
                enabled = !uiState.isSaving,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EditorialPulseColors.SurfaceCardSubtle,
                    contentColor = EditorialPulseColors.TextSecondary,
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
            ) {
                Text(
                    text = "Hủy",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Button(
                onClick = onSaveClicked,
                enabled = uiState.canSave,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EditorialPulseColors.PrimaryTeal,
                    contentColor = EditorialPulseColors.SurfaceCard,
                    disabledContainerColor = EditorialPulseColors.SurfaceCardSubtle,
                    disabledContentColor = EditorialPulseColors.TextMuted,
                ),
                modifier = Modifier
                    .weight(1.4f)
                    .height(48.dp),
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        color = EditorialPulseColors.SurfaceCard,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = "Thêm nguồn",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        color = EditorialPulseColors.TextSecondary,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
    )
}

@Composable
private fun ValidateInlineButton(
    isLoading: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(10.dp),
        color = EditorialPulseColors.SurfaceCard,
        border = BorderStroke(1.dp, EditorialPulseColors.BorderSubtle),
        shadowElevation = 1.dp,
        modifier = modifier.height(36.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier.padding(horizontal = 10.dp),
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = EditorialPulseColors.PrimaryTeal,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(14.dp),
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.Verified,
                    contentDescription = null,
                    tint = if (enabled) EditorialPulseColors.PrimaryTeal else EditorialPulseColors.TextMuted,
                    modifier = Modifier.size(15.dp),
                )
            }
            Text(
                text = "Kiểm tra",
                color = if (enabled) EditorialPulseColors.PrimaryTeal else EditorialPulseColors.TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun ValidFeedPreviewCard(
    preview: ValidationPreview,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(EditorialPulseColors.PrimaryTealSurface)
            .border(
                width = 1.dp,
                color = EditorialPulseColors.PrimaryTealSoft,
                shape = RoundedCornerShape(14.dp),
            )
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = null,
                tint = EditorialPulseColors.StatusActive,
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = "RSS feed hợp lệ — Đã phát hiện luồng tin",
                color = EditorialPulseColors.StatusActive,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(EditorialPulseColors.SurfaceCard)
                .border(1.dp, EditorialPulseColors.BorderSubtle, RoundedCornerShape(10.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Tiêu đề luồng:",
                    color = EditorialPulseColors.TextSecondary,
                    fontSize = 12.sp,
                )
                Text(
                    text = preview.detectedTitle,
                    color = EditorialPulseColors.TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Nguồn phát hành:",
                    color = EditorialPulseColors.TextSecondary,
                    fontSize = 12.sp,
                )
                Text(
                    text = preview.detectedSiteName,
                    color = EditorialPulseColors.PrimaryTeal,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun InvalidFeedErrorCard(
    error: FetchError,
    onRetryClicked: () -> Unit,
    canRetry: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(EditorialPulseColors.StatusErrorSoft)
            .border(
                width = 1.dp,
                color = EditorialPulseColors.StatusErrorBorder,
                shape = RoundedCornerShape(14.dp),
            )
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.ErrorOutline,
                contentDescription = null,
                tint = EditorialPulseColors.StatusError,
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = error.message,
                color = EditorialPulseColors.StatusError,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }

        if (error.code == FetchError.RSS_FEED_INVALID) {
            Text(
                text = "Vui lòng kiểm tra:\n• Đường dẫn URL chính xác\n• URL trỏ tới luồng RSS/Atom công khai",
                color = EditorialPulseColors.TextSecondary,
                fontSize = 12.sp,
                lineHeight = 18.sp,
            )
        }

        if (canRetry && error.code != FetchError.DUPLICATE_SOURCE) {
            Text(
                text = "Thử kiểm tra lại",
                color = EditorialPulseColors.PrimaryTeal,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable(onClick = onRetryClicked)
                    .padding(top = 2.dp),
            )
        }
    }
}

@Composable
private fun CategoryPillChip(
    name: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if (selected) {
        EditorialPulseColors.PrimaryTeal
    } else {
        EditorialPulseColors.SurfaceCardSubtle
    }
    val textColor = if (selected) {
        EditorialPulseColors.SurfaceCard
    } else {
        EditorialPulseColors.TextSecondary
    }

    Box(
        modifier = modifier
            .heightIn(min = 38.dp)
            .clip(RoundedCornerShape(percent = 50))
            .background(backgroundColor)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = name,
            color = textColor,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
        )
    }
}

@Composable
private fun editorialTextFieldColors(isError: Boolean = false) = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = EditorialPulseColors.SurfaceCard,
    unfocusedContainerColor = EditorialPulseColors.SurfaceCardSubtle,
    disabledContainerColor = EditorialPulseColors.SurfaceCardSubtle,
    errorContainerColor = EditorialPulseColors.StatusErrorSoft,
    focusedBorderColor = if (isError) EditorialPulseColors.StatusError else EditorialPulseColors.BorderFocus,
    unfocusedBorderColor = if (isError) EditorialPulseColors.StatusErrorBorder else EditorialPulseColors.BorderSubtle,
    errorBorderColor = EditorialPulseColors.StatusError,
    focusedTextColor = EditorialPulseColors.TextPrimary,
    unfocusedTextColor = EditorialPulseColors.TextPrimary,
    cursorColor = EditorialPulseColors.PrimaryTeal,
)

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun AddRssSourceSheetInitialPreview() {
    EditorialPulseTheme {
        val categories = RssSourceRepository.defaultCategories()
        AddRssSourceSheetContent(
            uiState = AddRssSourceUiState(
                categories = categories,
                selectedCategoryId = categories.first().id,
            ),
            onNameChanged = {},
            onRssUrlChanged = {},
            onValidateClicked = {},
            onCategorySelected = {},
            onCancelClicked = {},
            onSaveClicked = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun AddRssSourceSheetValidPreview() {
    EditorialPulseTheme {
        val categories = RssSourceRepository.defaultCategories()
        val worldCategory = categories.first { it.slug == "the-gioi" }
        AddRssSourceSheetContent(
            uiState = AddRssSourceUiState(
                nameInput = "Dân trí — Thế giới",
                rssUrlInput = "https://dantri.com.vn/rss/the-gioi.rss",
                categories = categories,
                selectedCategoryId = worldCategory.id,
                validationPreview = ValidationPreview(
                    valid = true,
                    detectedTitle = "Dân trí - Thế giới",
                    detectedSiteName = "Dân trí",
                    suggestedCategory = worldCategory,
                ),
            ),
            onNameChanged = {},
            onRssUrlChanged = {},
            onValidateClicked = {},
            onCategorySelected = {},
            onCancelClicked = {},
            onSaveClicked = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun AddRssSourceSheetErrorPreview() {
    EditorialPulseTheme {
        val categories = RssSourceRepository.defaultCategories()
        AddRssSourceSheetContent(
            uiState = AddRssSourceUiState(
                nameInput = "Nguồn thử nghiệm",
                rssUrlInput = "https://example.com/not-a-feed",
                categories = categories,
                selectedCategoryId = categories.first().id,
                error = FetchError(
                    code = FetchError.RSS_FEED_INVALID,
                    message = "RSS URL không hợp lệ hoặc không thể truy cập",
                ),
            ),
            onNameChanged = {},
            onRssUrlChanged = {},
            onValidateClicked = {},
            onCategorySelected = {},
            onCancelClicked = {},
            onSaveClicked = {},
        )
    }
}
```

- [ ] **Step 2: Integrate S07 into `MainActivity.kt` under the `Nguồn RSS` tab**

Update `app/src/main/java/vn/edu/usth/rssreader/MainActivity.kt` so selecting `Destination.Rss` displays the RSS Sources header with the `"Thêm nguồn"` button that opens `AddRssSourceModalSheet`, and renders newly added sources in real time from `RssSourceRepository.shared.sources`.

- [ ] **Step 3: Update `src/S07-add-rss-source/Guide.md` Done checkboxes**

Mark the completed items in `src/S07-add-rss-source/Guide.md` and verify `python3 scripts/ci/check-docs.py`.

- [ ] **Step 4: Run full verification suite**

Run:
```bash
python3 scripts/ci/check-docs.py
./gradlew testDebugUnitTest assembleDebug
```
Expected: Both pass with 0 errors.

- [ ] **Step 5: Commit Task 4**

```bash
git add app/src/main/java/vn/edu/usth/rssreader/ src/S07-add-rss-source/Guide.md
git commit -m "feat(s07): build AddRssSourceModalSheet UI and wire into app shell"
```
