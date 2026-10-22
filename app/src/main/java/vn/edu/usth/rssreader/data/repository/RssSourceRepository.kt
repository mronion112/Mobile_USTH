package vn.edu.usth.rssreader.data.repository

import java.time.Instant
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import vn.edu.usth.rssreader.data.fake.FakeCategories
import vn.edu.usth.rssreader.data.model.RssSource
import vn.edu.usth.rssreader.data.model.RssSourceStatus

/**
 * Contract used by S06/S08.  The app-shell Room/fetch implementation can
 * replace the sample store without changing the screen or its ViewModel.
 */
interface RssSourceRepository {
    val sources: StateFlow<List<RssSource>>

    suspend fun refresh(sourceId: Long): SourceActionResult

    suspend fun remove(sourceId: Long): SourceActionResult
}

sealed interface SourceActionResult {
    data object Success : SourceActionResult
    data object NotFound : SourceActionResult
    data class Failure(val message: String) : SourceActionResult
}

/**
 * Temporary local implementation for the frontend scaffold.  It preserves the
 * same state transitions as the app-shell repository: a refresh first writes
 * FETCHING, then writes its result, and a removal only removes the source.
 */
object LocalRssSourceRepository : RssSourceRepository {
    private val now: Instant = Instant.now()
    private val _sources = MutableStateFlow(
        listOf(
            RssSource(
                id = 1,
                name = "Dân trí — Thời sự",
                rssUrl = "https://dantri.com.vn/rss/thoi-su.rss",
                category = FakeCategories.Current,
                status = RssSourceStatus.ACTIVE,
                lastFetchedAt = now.minusSeconds(2 * 60),
                lastError = null,
                createdAt = now.minusSeconds(16 * 24 * 60 * 60)
            ),
            RssSource(
                id = 2,
                name = "Dân trí — Công nghệ",
                rssUrl = "https://dantri.com.vn/rss/suc-manh-so.rss",
                category = FakeCategories.Tech,
                status = RssSourceStatus.ACTIVE,
                lastFetchedAt = now.minusSeconds(10 * 60),
                lastError = null,
                createdAt = now.minusSeconds(16 * 24 * 60 * 60)
            ),
            RssSource(
                id = 3,
                name = "Dân trí — Kinh doanh",
                rssUrl = "https://dantri.com.vn/rss/kinh-doanh.rss",
                category = FakeCategories.Business,
                status = RssSourceStatus.FETCHING,
                lastFetchedAt = now.minusSeconds(30 * 60),
                lastError = null,
                createdAt = now.minusSeconds(16 * 24 * 60 * 60)
            ),
            RssSource(
                id = 4,
                name = "Dân trí — Thế giới",
                rssUrl = "https://dantri.com.vn/rss/the-gioi.rss",
                category = FakeCategories.World,
                status = RssSourceStatus.ERROR,
                lastFetchedAt = now.minusSeconds(60 * 60),
                lastError = "Lỗi kết nối",
                createdAt = now.minusSeconds(16 * 24 * 60 * 60)
            )
        )
    )

    override val sources: StateFlow<List<RssSource>> = _sources.asStateFlow()

    override suspend fun refresh(sourceId: Long): SourceActionResult {
        val source = _sources.value.firstOrNull { it.id == sourceId }
            ?: return SourceActionResult.NotFound
        if (source.status == RssSourceStatus.DISABLED) return SourceActionResult.NotFound
        if (source.status == RssSourceStatus.FETCHING) return SourceActionResult.Success

        _sources.update { current ->
            current.map { item ->
                if (item.id == sourceId) {
                    item.copy(status = RssSourceStatus.FETCHING, lastError = null)
                } else {
                    item
                }
            }
        }

        // The screen only observes this local state. In the integrated app,
        // app-shell's on-device RSS fetcher performs this asynchronous work.
        delay(900)

        if (_sources.value.none { it.id == sourceId }) return SourceActionResult.NotFound
        _sources.update { current ->
            current.map { item ->
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
        }
        return SourceActionResult.Success
    }

    override suspend fun remove(sourceId: Long): SourceActionResult {
        if (_sources.value.none { it.id == sourceId }) return SourceActionResult.NotFound
        _sources.update { current -> current.filterNot { it.id == sourceId } }
        return SourceActionResult.Success
    }
}
