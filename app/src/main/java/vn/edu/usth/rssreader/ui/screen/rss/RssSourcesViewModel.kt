package vn.edu.usth.rssreader.ui.screen.rss

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import vn.edu.usth.rssreader.RssReaderApplication
import vn.edu.usth.rssreader.data.fake.FakeCategories
import vn.edu.usth.rssreader.data.model.RssSourceStatus
import vn.edu.usth.rssreader.data.repository.AddSourceResult
import vn.edu.usth.rssreader.data.repository.RssFeedValidationResult
import vn.edu.usth.rssreader.data.repository.RssSourceDraft
import vn.edu.usth.rssreader.data.repository.SourceActionResult

class RssSourcesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = (application as RssReaderApplication).rssSourceRepository
    private val _uiState = MutableStateFlow(RssSourcesUiState())
    val uiState: StateFlow<RssSourcesUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<String>()
    val events = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            repository.sources.collect { sources ->
                _uiState.update { current ->
                    current.copy(
                        isLoading = sources == null,
                        sources = sources.orEmpty().filter { it.status != RssSourceStatus.DISABLED }
                    )
                }
            }
        }
        viewModelScope.launch {
            repository.loadError.collect { error ->
                _uiState.update { current -> current.copy(loadError = error) }
            }
        }
    }

    fun reloadSources() {
        viewModelScope.launch { repository.reload() }
    }

    fun restoreBundledCatalog() {
        viewModelScope.launch {
            when (repository.restoreBundledCatalog()) {
                SourceActionResult.Success -> _events.emit("Đã khôi phục danh sách nguồn mặc định")
                SourceActionResult.NotFound -> Unit
                is SourceActionResult.Failure -> _events.emit("Không thể khôi phục danh sách nguồn mặc định")
            }
        }
    }

    fun openAddSource() {
        if (_uiState.value.addSource != null) return
        _uiState.update {
            it.copy(
                addSource = AddSourceUiState(
                    name = "",
                    rssUrl = "",
                    category = DEFAULT_CATEGORY,
                    categories = CATEGORY_OPTIONS
                )
            )
        }
    }

    fun dismissAddSource() {
        if (_uiState.value.addSource?.isSaving == true) return
        _uiState.update { it.copy(addSource = null) }
    }

    fun updateNewSourceName(name: String) {
        updateAddSource { it.copy(name = name, errorMessage = null) }
    }

    fun updateNewSourceUrl(rssUrl: String) {
        updateAddSource {
            it.copy(
                rssUrl = rssUrl,
                validatedRssUrl = null,
                detectedTitle = null,
                detectedSiteName = null,
                errorMessage = null
            )
        }
    }

    fun selectNewSourceCategory(categoryId: String) {
        updateAddSource { form ->
            val category = form.categories.firstOrNull { it.id == categoryId } ?: return@updateAddSource form
            form.copy(category = category)
        }
    }

    fun validateNewSource() {
        val form = _uiState.value.addSource ?: return
        val url = form.rssUrl.trim()
        if (form.isValidating || form.isSaving) return
        if (!url.isHttpUrl()) {
            updateAddSource { it.copy(errorMessage = "Hãy nhập URL RSS bắt đầu bằng http:// hoặc https://") }
            return
        }

        updateAddSource { it.copy(isValidating = true, errorMessage = null) }
        viewModelScope.launch {
            when (val result = repository.validate(url)) {
                is RssFeedValidationResult.Valid -> {
                    _uiState.update { current ->
                        val activeForm = current.addSource
                        if (activeForm == null || activeForm.rssUrl.trim() != url) {
                            current
                        } else {
                            val suggestedCategory = suggestedCategory(
                                url = url,
                                title = result.detectedTitle,
                                categories = activeForm.categories
                            )
                            current.copy(
                                addSource = activeForm.copy(
                                    category = suggestedCategory ?: activeForm.category,
                                    isValidating = false,
                                    validatedRssUrl = url,
                                    detectedTitle = result.detectedTitle,
                                    detectedSiteName = result.detectedSiteName,
                                    errorMessage = null
                                )
                            )
                        }
                    }
                }

                is RssFeedValidationResult.Invalid -> {
                    _uiState.update { current ->
                        val activeForm = current.addSource
                        if (activeForm == null || activeForm.rssUrl.trim() != url) {
                            current
                        } else {
                            current.copy(
                                addSource = activeForm.copy(
                                    isValidating = false,
                                    errorMessage = result.message
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    fun saveNewSource() {
        val form = _uiState.value.addSource ?: return
        if (!form.canSave) {
            updateAddSource { it.copy(errorMessage = "Hãy kiểm tra nguồn RSS hợp lệ trước khi lưu") }
            return
        }

        _uiState.update { current ->
            current.copy(addSource = current.addSource?.copy(isSaving = true, errorMessage = null))
        }
        viewModelScope.launch {
            when (
                val result = repository.add(
                    RssSourceDraft(
                        name = form.name,
                        rssUrl = form.rssUrl,
                        category = form.category
                    )
                )
            ) {
                is AddSourceResult.Created -> {
                    _uiState.update { it.copy(addSource = null) }
                    _events.emit("Đã thêm nguồn RSS. Đang tải dữ liệu…")
                }

                AddSourceResult.Duplicate -> updateAddSource {
                    it.copy(isSaving = false, errorMessage = "URL này đã có trong danh sách nguồn RSS")
                }

                is AddSourceResult.Failure -> updateAddSource {
                    it.copy(isSaving = false, errorMessage = result.message)
                }
            }
        }
    }

    fun refresh(sourceId: Long) {
        val source = _uiState.value.sources.firstOrNull { it.id == sourceId } ?: return
        if (source.status == RssSourceStatus.FETCHING) return

        viewModelScope.launch {
            when (repository.refresh(sourceId)) {
                SourceActionResult.Success -> Unit
                SourceActionResult.NotFound -> _events.emit("Nguồn RSS này không còn trong danh sách.")
                is SourceActionResult.Failure -> _events.emit("Không thể làm mới nguồn RSS. Vui lòng thử lại.")
            }
        }
    }

    fun requestRemoval(sourceId: Long) {
        val source = _uiState.value.sources.firstOrNull { it.id == sourceId } ?: return
        if (source.status == RssSourceStatus.FETCHING) return

        _uiState.update {
            it.copy(
                pendingRemoval = PendingSourceRemoval(id = source.id, name = source.name),
                removalError = null
            )
        }
    }

    fun dismissRemoval() {
        if (_uiState.value.removingSourceId != null) return
        _uiState.update { it.copy(pendingRemoval = null, removalError = null) }
    }

    fun confirmRemoval() {
        val request = _uiState.value.pendingRemoval ?: return
        if (_uiState.value.removingSourceId != null) return

        // Set this before launching work so two quick taps cannot issue two
        // delete calls for the same (or a newly selected) source.
        _uiState.update { it.copy(removingSourceId = request.id, removalError = null) }
        viewModelScope.launch {
            when (repository.remove(request.id)) {
                SourceActionResult.Success -> {
                    _uiState.update {
                        it.copy(
                            pendingRemoval = null,
                            removingSourceId = null,
                            removalError = null
                        )
                    }
                    _events.emit("Đã xóa nguồn RSS khỏi danh sách")
                }

                SourceActionResult.NotFound -> {
                    _uiState.update {
                        it.copy(
                            pendingRemoval = null,
                            removingSourceId = null,
                            removalError = null
                        )
                    }
                    _events.emit("Nguồn RSS này không còn trong danh sách.")
                }

                is SourceActionResult.Failure -> {
                    _uiState.update {
                        it.copy(
                            removingSourceId = null,
                            removalError = "Không thể xóa nguồn RSS. Vui lòng thử lại."
                        )
                    }
                }
            }
        }
    }

    private fun updateAddSource(transform: (AddSourceUiState) -> AddSourceUiState) {
        _uiState.update { current ->
            val form = current.addSource ?: return@update current
            current.copy(addSource = transform(form))
        }
    }

    private fun suggestedCategory(
        url: String,
        title: String?,
        categories: List<vn.edu.usth.rssreader.data.model.Category>
    ) = categories.firstOrNull { category ->
        url.contains(category.id, ignoreCase = true) ||
            title?.contains(category.name, ignoreCase = true) == true
    }

    private fun String.isHttpUrl(): Boolean =
        startsWith("https://", ignoreCase = true) || startsWith("http://", ignoreCase = true)

    private companion object {
        val CATEGORY_OPTIONS = listOf(
            FakeCategories.Current,
            FakeCategories.Tech,
            FakeCategories.Business,
            FakeCategories.World,
            FakeCategories.Sports,
            FakeCategories.Entertainment,
            FakeCategories.Health,
            FakeCategories.Education,
            FakeCategories.Science,
            FakeCategories.RealEstate
        )
        val DEFAULT_CATEGORY = FakeCategories.Current
    }
}
