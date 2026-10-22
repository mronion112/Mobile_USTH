package vn.edu.usth.rssreader.data.repository

import android.content.Context
import java.io.File
import java.time.Instant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import vn.edu.usth.rssreader.data.model.Category
import vn.edu.usth.rssreader.data.model.RssSource
import vn.edu.usth.rssreader.data.model.RssSourceStatus

/**
 * Single source-of-truth contract for S06/S08. A null list means the persisted
 * JSON catalog is still being read from device storage.
 */
interface RssSourceRepository {
    val sources: StateFlow<List<RssSource>?>
    val loadError: StateFlow<String?>

    suspend fun reload()

    suspend fun restoreBundledCatalog(): SourceActionResult

    suspend fun validate(rssUrl: String): RssFeedValidationResult

    suspend fun add(draft: RssSourceDraft): AddSourceResult

    suspend fun refresh(sourceId: Long): SourceActionResult

    suspend fun remove(sourceId: Long): SourceActionResult
}

sealed interface SourceActionResult {
    data object Success : SourceActionResult
    data object NotFound : SourceActionResult
    data class Failure(val message: String) : SourceActionResult
}

data class RssSourceDraft(
    val name: String,
    val rssUrl: String,
    val category: Category
)

sealed interface AddSourceResult {
    data class Created(val source: RssSource) : AddSourceResult
    data object Duplicate : AddSourceResult
    data class Failure(val message: String) : AddSourceResult
}

/**
 * Uses the checked-in catalog as a seed, then keeps an app-private copy at
 * filesDir/rss_sources.json. Source removal and status updates survive
 * navigation, configuration changes and app relaunches without a backend.
 */
class JsonRssSourceRepository(context: Context) : RssSourceRepository {
    private val appContext = context.applicationContext
    private val catalogFile = File(appContext.filesDir, CATALOG_FILE_NAME)
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val writeMutex = Mutex()
    private val feedInspector = RssFeedInspector()
    private val _sources = MutableStateFlow<List<RssSource>?>(null)
    private val _loadError = MutableStateFlow<String?>(null)

    override val sources: StateFlow<List<RssSource>?> = _sources.asStateFlow()
    override val loadError: StateFlow<String?> = _loadError.asStateFlow()

    init {
        repositoryScope.launch { reload() }
    }

    /** Reads the local catalog again and never turns a malformed file into an empty list silently. */
    override suspend fun reload() {
        _sources.value = null
        _loadError.value = null

        val loaded = writeMutex.withLock {
            runCatching { readSources() }
        }
        loaded.onSuccess { sourceList ->
            _sources.value = sourceList
            resumeInterruptedFetches(sourceList)
        }.onFailure {
            _sources.value = emptyList()
            _loadError.value = CATALOG_LOAD_ERROR
        }
    }

    /** Explicit recovery only; this replaces the private catalog with the bundled seed. */
    override suspend fun restoreBundledCatalog(): SourceActionResult {
        val restored = writeMutex.withLock {
            runCatching {
                copyBundledCatalog()
                readSources()
            }
        }
        return restored.fold(
            onSuccess = { sourceList ->
                _loadError.value = null
                _sources.value = sourceList
                resumeInterruptedFetches(sourceList)
                SourceActionResult.Success
            },
            onFailure = { SourceActionResult.Failure(CATALOG_RESTORE_ERROR) }
        )
    }

    override suspend fun validate(rssUrl: String): RssFeedValidationResult =
        feedInspector.validate(rssUrl)

    /**
     * S07 can use this without introducing a second store. A newly added feed
     * starts in FETCHING and is immediately written to the same local catalog.
     */
    override suspend fun add(draft: RssSourceDraft): AddSourceResult {
        val result = writeMutex.withLock {
            val current = _sources.value ?: return@withLock AddSourceResult.Failure(
                "Danh sách nguồn RSS chưa sẵn sàng"
            )
            val name = draft.name.trim()
            val url = draft.rssUrl.trim()
            if (name.isBlank() || !url.isHttpUrl()) {
                return@withLock AddSourceResult.Failure("Tên nguồn hoặc RSS URL không hợp lệ")
            }
            if (current.any { it.rssUrl.normalizedUrl() == url.normalizedUrl() }) {
                return@withLock AddSourceResult.Duplicate
            }

            val source = RssSource(
                id = (current.maxOfOrNull { it.id } ?: 0L) + 1L,
                name = name,
                rssUrl = url,
                category = draft.category,
                status = RssSourceStatus.FETCHING,
                lastFetchedAt = null,
                lastError = null,
                createdAt = Instant.now()
            )
            val updated = current + source
            runCatching {
                persist(updated)
                _sources.value = updated
                AddSourceResult.Created(source)
            }.getOrElse { AddSourceResult.Failure("Không thể lưu nguồn RSS mới") }
        }

        if (result is AddSourceResult.Created) {
            repositoryScope.launch { completeFetch(result.source.id, result.source.rssUrl) }
        }
        return result
    }

    /** Fetches and parses the source on device, persisting both state transitions. */
    override suspend fun refresh(sourceId: Long): SourceActionResult {
        return when (val start = markFetching(sourceId)) {
            is FetchStart.Started -> completeFetch(sourceId, start.rssUrl)
            FetchStart.AlreadyFetching -> SourceActionResult.Success
            FetchStart.NotFound -> SourceActionResult.NotFound
            is FetchStart.Failure -> SourceActionResult.Failure(start.message)
        }
    }

    override suspend fun remove(sourceId: Long): SourceActionResult = writeMutex.withLock {
        val current = _sources.value ?: return@withLock SourceActionResult.Failure(
            "Danh sách nguồn RSS chưa sẵn sàng"
        )
        if (current.none { it.id == sourceId }) return@withLock SourceActionResult.NotFound

        val updated = current.filterNot { it.id == sourceId }
        runCatching {
            persist(updated)
            _sources.value = updated
            SourceActionResult.Success
        }.getOrElse { SourceActionResult.Failure("Không thể lưu danh sách nguồn RSS") }
    }

    private suspend fun markFetching(sourceId: Long): FetchStart = writeMutex.withLock {
        val current = _sources.value ?: return@withLock FetchStart.Failure(
            "Danh sách nguồn RSS chưa sẵn sàng"
        )
        val source = current.firstOrNull { it.id == sourceId } ?: return@withLock FetchStart.NotFound
        when (source.status) {
            RssSourceStatus.FETCHING -> return@withLock FetchStart.AlreadyFetching
            RssSourceStatus.DISABLED -> return@withLock FetchStart.NotFound
            else -> Unit
        }

        val fetching = current.map { item ->
            if (item.id == sourceId) {
                item.copy(status = RssSourceStatus.FETCHING, lastError = null)
            } else {
                item
            }
        }
        runCatching {
            persist(fetching)
            _sources.value = fetching
            FetchStart.Started(source.rssUrl)
        }.getOrElse { FetchStart.Failure("Không thể lưu trạng thái nguồn RSS") }
    }

    private suspend fun completeFetch(sourceId: Long, rssUrl: String): SourceActionResult {
        val validation = feedInspector.validate(rssUrl)

        return writeMutex.withLock {
            val current = _sources.value ?: return@withLock SourceActionResult.NotFound
            val source = current.firstOrNull { it.id == sourceId }
                ?: return@withLock SourceActionResult.NotFound
            if (source.status == RssSourceStatus.DISABLED) return@withLock SourceActionResult.Success

            val completed = current.map { item ->
                if (item.id != sourceId) {
                    item
                } else {
                    when (validation) {
                        is RssFeedValidationResult.Valid -> item.copy(
                            status = RssSourceStatus.ACTIVE,
                            lastFetchedAt = Instant.now(),
                            lastError = null
                        )

                        is RssFeedValidationResult.Invalid -> item.copy(
                            status = RssSourceStatus.ERROR,
                            lastError = validation.message
                        )
                    }
                }
            }
            runCatching {
                persist(completed)
                _sources.value = completed
                SourceActionResult.Success
            }.getOrElse { SourceActionResult.Failure("Không thể lưu trạng thái nguồn RSS") }
        }
    }

    private fun resumeInterruptedFetches(sourceList: List<RssSource>) {
        sourceList
            .filter { it.status == RssSourceStatus.FETCHING }
            .forEach { source ->
                repositoryScope.launch { completeFetch(source.id, source.rssUrl) }
            }
    }

    private suspend fun readSources(): List<RssSource> = withContext(Dispatchers.IO) {
        ensureCatalogExists()
        val root = JSONObject(catalogFile.readText(Charsets.UTF_8))
        val records = root.getJSONArray("sources")
        buildList {
            for (index in 0 until records.length()) {
                add(records.getJSONObject(index).toRssSource())
            }
        }
    }

    private suspend fun persist(sources: List<RssSource>) = withContext(Dispatchers.IO) {
        val payload = JSONObject()
            .put("schemaVersion", SCHEMA_VERSION)
            .put("sources", JSONArray().apply { sources.forEach { put(it.toJson()) } })
            .toString(JSON_INDENT)

        replaceCatalogWith { temporaryFile ->
            temporaryFile.writeText(payload, Charsets.UTF_8)
        }
    }

    private fun ensureCatalogExists() {
        if (!catalogFile.exists()) copyBundledCatalog()
    }

    private fun copyBundledCatalog() {
        replaceCatalogWith { temporaryFile ->
            appContext.assets.open(ASSET_CATALOG_PATH).use { input ->
                temporaryFile.outputStream().use(input::copyTo)
            }
        }
    }

    private fun replaceCatalogWith(writeTemporaryFile: (File) -> Unit) {
        val temporaryFile = File(catalogFile.parentFile, "$CATALOG_FILE_NAME.tmp")
        temporaryFile.delete()
        writeTemporaryFile(temporaryFile)
        if (!temporaryFile.renameTo(catalogFile)) {
            temporaryFile.copyTo(catalogFile, overwrite = true)
            temporaryFile.delete()
        }
    }

    private fun JSONObject.toRssSource(): RssSource {
        val category = getJSONObject("category")
        return RssSource(
            id = getLong("id"),
            name = getString("name"),
            rssUrl = getString("rssUrl"),
            category = Category(
                id = category.getString("id"),
                name = category.getString("name"),
                colorIndex = category.getInt("colorIndex")
            ),
            status = RssSourceStatus.valueOf(getString("status")),
            lastFetchedAt = optNullableInstant("lastFetchedAt"),
            lastError = optNullableString("lastError"),
            createdAt = Instant.parse(getString("createdAt"))
        )
    }

    private fun RssSource.toJson(): JSONObject = JSONObject()
        .put("id", id)
        .put("name", name)
        .put("rssUrl", rssUrl)
        .put(
            "category",
            JSONObject()
                .put("id", category.id)
                .put("name", category.name)
                .put("colorIndex", category.colorIndex)
        )
        .put("status", status.name)
        .put("lastFetchedAt", lastFetchedAt?.toString())
        .put("lastError", lastError)
        .put("createdAt", createdAt.toString())

    private fun JSONObject.optNullableInstant(name: String): Instant? =
        optNullableString(name)?.let(Instant::parse)

    private fun JSONObject.optNullableString(name: String): String? =
        if (isNull(name) || !has(name)) null else getString(name)

    private fun String.isHttpUrl(): Boolean =
        startsWith("https://", ignoreCase = true) || startsWith("http://", ignoreCase = true)

    private fun String.normalizedUrl(): String = trim().trimEnd('/').lowercase()

    private sealed interface FetchStart {
        data class Started(val rssUrl: String) : FetchStart
        data object AlreadyFetching : FetchStart
        data object NotFound : FetchStart
        data class Failure(val message: String) : FetchStart
    }

    private companion object {
        const val ASSET_CATALOG_PATH = "rss_sources.json"
        const val CATALOG_FILE_NAME = "rss_sources.json"
        const val SCHEMA_VERSION = 1
        const val JSON_INDENT = 2
        const val CATALOG_LOAD_ERROR = "Không thể đọc danh sách nguồn RSS trên thiết bị."
        const val CATALOG_RESTORE_ERROR = "Không thể khôi phục danh sách nguồn mặc định."
    }
}
