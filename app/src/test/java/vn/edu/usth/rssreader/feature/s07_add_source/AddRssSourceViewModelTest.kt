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
