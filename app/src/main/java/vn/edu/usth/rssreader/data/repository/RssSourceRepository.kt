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
