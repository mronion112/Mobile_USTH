package vn.edu.usth.rssreader.data.repository

import android.content.Context
import java.io.File
import java.time.Instant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
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
    private val _sources = MutableStateFlow<List<RssSource>?>(null)

    override val sources: StateFlow<List<RssSource>?> = _sources.asStateFlow()

    init {
        repositoryScope.launch {
            _sources.value = runCatching { readSources() }.getOrElse { emptyList() }
        }
    }

    /**
     * S07 can use this without introducing a second store. A newly added feed
     * starts in FETCHING and is immediately written to the same local catalog.
     */
    override suspend fun add(draft: RssSourceDraft): AddSourceResult = writeMutex.withLock {
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
        return@withLock runCatching {
            persist(updated)
            _sources.value = updated
            AddSourceResult.Created(source)
        }.getOrElse { AddSourceResult.Failure("Không thể lưu nguồn RSS mới") }
    }

    override suspend fun refresh(sourceId: Long): SourceActionResult {
        val didStart = writeMutex.withLock {
            val current = _sources.value ?: return@withLock false
            val source = current.firstOrNull { it.id == sourceId }
                ?: return@withLock false
            if (source.status == RssSourceStatus.FETCHING || source.status == RssSourceStatus.DISABLED) {
                return@withLock false
            }

            val fetching = current.map { item ->
                if (item.id == sourceId) {
                    item.copy(status = RssSourceStatus.FETCHING, lastError = null)
                } else {
                    item
                }
            }
            persist(fetching)
            _sources.value = fetching
            true
        }

        if (!didStart) {
            return if (_sources.value?.any { it.id == sourceId } == true) {
                SourceActionResult.Success
            } else {
                SourceActionResult.NotFound
            }
        }

        // The app-shell RSS parser can replace this demo completion. S06 still
        // persists every state transition, so it is ready for that integration.
        delay(900)

        return writeMutex.withLock {
            val current = _sources.value ?: return@withLock SourceActionResult.NotFound
            if (current.none { it.id == sourceId }) return@withLock SourceActionResult.NotFound

            val completed = current.map { item ->
                if (item.id == sourceId) {
                    item.copy(
                        status = RssSourceStatus.ACTIVE,
                        lastFetchedAt = Instant.now(),
                        lastError = null
                    )
                } else {
                    item
                }
            }
            runCatching {
                persist(completed)
                _sources.value = completed
                SourceActionResult.Success
            }.getOrElse { SourceActionResult.Failure("Không thể lưu trạng thái nguồn RSS") }
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

    private suspend fun readSources(): List<RssSource> = withContext(Dispatchers.IO) {
        ensureCatalogExists()
        val root = JSONObject(catalogFile.readText(Charsets.UTF_8))
        val records = root.optJSONArray("sources") ?: JSONArray()
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

        val temporaryFile = File(catalogFile.parentFile, "$CATALOG_FILE_NAME.tmp")
        temporaryFile.writeText(payload, Charsets.UTF_8)
        if (!temporaryFile.renameTo(catalogFile)) {
            temporaryFile.copyTo(catalogFile, overwrite = true)
            temporaryFile.delete()
        }
    }

    private fun ensureCatalogExists() {
        if (catalogFile.exists()) return
        appContext.assets.open(ASSET_CATALOG_PATH).use { input ->
            catalogFile.outputStream().use(input::copyTo)
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

    private companion object {
        const val ASSET_CATALOG_PATH = "rss_sources.json"
        const val CATALOG_FILE_NAME = "rss_sources.json"
        const val SCHEMA_VERSION = 1
        const val JSON_INDENT = 2
    }
}
