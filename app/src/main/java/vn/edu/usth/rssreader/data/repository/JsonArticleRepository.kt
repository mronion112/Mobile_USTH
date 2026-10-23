package vn.edu.usth.rssreader.data.repository

import android.content.Context
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.time.Instant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import vn.edu.usth.rssreader.data.model.Article
import vn.edu.usth.rssreader.data.model.Category
import vn.edu.usth.rssreader.data.model.RssSource
import vn.edu.usth.rssreader.data.model.RssSourceStatus
import vn.edu.usth.rssreader.data.parser.RssArticleParser

class JsonArticleRepository(
    context: Context,
    private val sourceRepository: RssSourceRepository,
    private val parser: RssArticleParser = RssArticleParser()
) : ArticleRepository {

    private val appContext = context.applicationContext
    private val articlesFile = File(appContext.filesDir, "articles.json")
    private val bookmarksFile = File(appContext.filesDir, "bookmarks.json")
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val writeMutex = Mutex()

    private val lastFetchPerSource = mutableMapOf<Long, Instant>()

    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    private val _bookmarkedArticles = MutableStateFlow<List<Article>>(emptyList())
    private val _bookmarkedIds = MutableStateFlow<Set<String>>(emptySet())
    private val _isRefreshing = MutableStateFlow(false)
    private val _refreshError = MutableStateFlow<String?>(null)

    override val articles: StateFlow<List<Article>> = _articles.asStateFlow()
    override val bookmarkedArticles: StateFlow<List<Article>> = _bookmarkedArticles.asStateFlow()
    override val bookmarkedIds: StateFlow<Set<String>> = _bookmarkedIds.asStateFlow()
    override val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()
    override val refreshError: StateFlow<String?> = _refreshError.asStateFlow()

    init {
        scope.launch {
            loadFromDisk()
            observeSources()
        }
    }

    override suspend fun refreshAll() {
        if (_isRefreshing.value) return
        _isRefreshing.value = true
        _refreshError.value = null
        try {
            val sources = sourceRepository.sources.value
                ?.filter { it.status == RssSourceStatus.ACTIVE }
                ?: emptyList()
            if (sources.isEmpty()) {
                _refreshError.value = "Chưa có nguồn RSS nào đang hoạt động"
                return
            }

            val outcomes = coroutineScope {
                sources.map { source ->
                    async { source to runCatching { fetchAndMerge(source) } }
                }.awaitAll()
            }
            _refreshError.value = outcomes.firstNotNullOfOrNull { (source, result) ->
                result.exceptionOrNull()?.let { "Không thể tải ${source.name}" }
            }
        } finally {
            _isRefreshing.value = false
        }
    }

    override suspend fun toggleBookmark(article: Article): Boolean {
        val updated = writeMutex.withLock {
            val current = _bookmarkedArticles.value
            val isBookmarked = current.any { it.id == article.id }
            val next = if (isBookmarked) {
                current.filterNot { it.id == article.id }
            } else {
                (current + article.copy(savedAt = Instant.now()))
                    .sortedByDescending { it.savedAt ?: Instant.EPOCH }
            }
            runCatching { persistBookmarks(next) }
            _bookmarkedArticles.value = next
            _bookmarkedIds.value = next.map { it.id }.toSet()
            next
        }
        return updated.any { it.id == article.id }
    }

    override suspend fun pruneOrphanedArticles(activeSourceIds: Set<Long>): Int =
        writeMutex.withLock {
            val current = _articles.value
            val kept = current.filter { article ->
                article.sourceId == -1L || article.sourceId in activeSourceIds
            }
            val removed = current.size - kept.size
            if (removed > 0) {
                _articles.value = kept
                runCatching { persistArticles(kept) }
            }
            removed
        }

    // ─── background ─────────────────────────────────────────

    private suspend fun loadFromDisk() {
        val cachedArticles = runCatching { readArticles() }.getOrDefault(emptyList())
        val cachedBookmarks = runCatching { readBookmarks() }.getOrDefault(emptyList())
        _articles.value = cachedArticles.sortedByDescending { it.publishedAt }
        val sortedBookmarks = cachedBookmarks.sortedByDescending { it.savedAt ?: Instant.EPOCH }
        _bookmarkedArticles.value = sortedBookmarks
        _bookmarkedIds.value = sortedBookmarks.map { it.id }.toSet()
    }

    private suspend fun observeSources() {
        sourceRepository.sources.collect { sources ->
            if (sources == null) return@collect

            if (sourceRepository.loadError.value != null) return@collect

            val activeSourceIds = sources
                .filter { it.status != RssSourceStatus.DISABLED }
                .map { it.id }
                .toSet()

            runCatching { pruneOrphanedArticles(activeSourceIds) }

            sources
                .filter { it.status == RssSourceStatus.ACTIVE }
                .filter { source ->
                    val lastFetch = lastFetchPerSource[source.id]
                    lastFetch == null ||
                            (source.lastFetchedAt != null && source.lastFetchedAt.isAfter(lastFetch))
                }
                .forEach { source ->
                    runCatching { fetchAndMerge(source) }
                }
        }
    }

    private suspend fun fetchAndMerge(source: RssSource) {
        val fresh = fetchFromNetwork(source)
        mergeAndPersist(fresh)
        lastFetchPerSource[source.id] = Instant.now()
    }

    private suspend fun fetchFromNetwork(source: RssSource): List<Article> =
        withContext(Dispatchers.IO) {
            val connection = (URL(source.rssUrl).openConnection() as HttpURLConnection).apply {
                instanceFollowRedirects = true
                connectTimeout = CONNECT_TIMEOUT_MS
                readTimeout = READ_TIMEOUT_MS
                requestMethod = "GET"
                setRequestProperty(
                    "Accept",
                    "application/rss+xml, application/atom+xml, application/xml, text/xml"
                )
            }
            try {
                if (connection.responseCode !in 200..299) {
                    throw IOException("HTTP ${connection.responseCode}")
                }
                connection.inputStream.use { input ->
                    parser.parse(input, source).getOrThrow()
                }
            } finally {
                connection.disconnect()
            }
        }

    private suspend fun mergeAndPersist(newArticles: List<Article>) = writeMutex.withLock {
        val merged = (_articles.value + newArticles)
            .associateBy { it.id }
            .values
            .sortedByDescending { it.publishedAt }
        _articles.value = merged
        runCatching { persistArticles(merged) }
    }

    // ─── json I/O ───────────────────────────────────────────

    private suspend fun readArticles(): List<Article> = withContext(Dispatchers.IO) {
        if (!articlesFile.exists()) return@withContext emptyList()
        val root = JSONObject(articlesFile.readText(Charsets.UTF_8))
        val arr = root.optJSONArray("articles") ?: return@withContext emptyList()
        buildList {
            for (i in 0 until arr.length()) {
                runCatching { add(arr.getJSONObject(i).toArticle()) }
            }
        }
    }

    private suspend fun persistArticles(list: List<Article>) = withContext(Dispatchers.IO) {
        val payload = JSONObject()
            .put("schemaVersion", SCHEMA_VERSION)
            .put("articles", JSONArray().apply { list.forEach { put(it.toJson()) } })
            .toString(JSON_INDENT)
        atomicWrite(articlesFile, payload)
    }

    private suspend fun readBookmarks(): List<Article> = withContext(Dispatchers.IO) {
        if (!bookmarksFile.exists()) return@withContext emptyList()
        val root = JSONObject(bookmarksFile.readText(Charsets.UTF_8))

        val arr = root.optJSONArray("articles") ?: return@withContext emptyList()
        buildList {
            for (i in 0 until arr.length()) {
                runCatching { add(arr.getJSONObject(i).toArticle()) }
            }
        }
    }

    private suspend fun persistBookmarks(list: List<Article>) = withContext(Dispatchers.IO) {
        val payload = JSONObject()
            .put("schemaVersion", SCHEMA_VERSION)
            .put("articles", JSONArray().apply { list.forEach { put(it.toJson()) } })
            .toString(JSON_INDENT)
        atomicWrite(bookmarksFile, payload)
    }

    private fun atomicWrite(target: File, content: String) {
        val temp = File(target.parentFile, "${target.name}.tmp")
        temp.delete()
        temp.writeText(content, Charsets.UTF_8)
        if (!temp.renameTo(target)) {
            temp.copyTo(target, overwrite = true)
            temp.delete()
        }
    }

    private fun Article.toJson(): JSONObject = JSONObject()
        .put("id", id)
        .put("sourceId", sourceId)
        .put("title", title)
        .put("description", description)
        .put("imageUrl", imageUrl)
        .put(
            "category",
            JSONObject()
                .put("id", category.id)
                .put("name", category.name)
                .put("colorIndex", category.colorIndex)
        )
        .put("source", source)
        .put("author", author)
        .put("publishedAt", publishedAt.toString())
        .put("viewCount", viewCount)
        .put("isRssVerified", isRssVerified)
        .put("readingTimeMinutes", readingTimeMinutes)
        .put("savedAt", savedAt?.toString())

    private fun JSONObject.toArticle(): Article {
        val category = getJSONObject("category")
        return Article(
            id = getString("id"),
            sourceId = optLong("sourceId", -1L),
            title = getString("title"),
            description = optNullableString("description"),
            imageUrl = optString("imageUrl", ""),
            category = Category(
                id = category.getString("id"),
                name = category.getString("name"),
                colorIndex = category.getInt("colorIndex")
            ),
            source = getString("source"),
            author = optNullableString("author"),
            publishedAt = Instant.parse(getString("publishedAt")),
            viewCount = if (isNull("viewCount")) null else optInt("viewCount"),
            isRssVerified = optBoolean("isRssVerified", true),
            readingTimeMinutes = if (isNull("readingTimeMinutes")) null else optInt("readingTimeMinutes"),
            savedAt = optNullableInstant("savedAt")       // ← THÊM
        )
    }

    private fun JSONObject.optNullableString(key: String): String? =
        if (isNull(key) || !has(key)) null else optString(key)

    private fun JSONObject.optNullableInstant(key: String): Instant? =
        optNullableString(key)?.let { runCatching { Instant.parse(it) }.getOrNull() }   // ← THÊM

    private companion object {
        const val SCHEMA_VERSION = 1
        const val JSON_INDENT = 2
        const val CONNECT_TIMEOUT_MS = 10_000
        const val READ_TIMEOUT_MS = 15_000
    }
}