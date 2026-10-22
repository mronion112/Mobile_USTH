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
import vn.edu.usth.rssreader.data.model.RssSourceStatus
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
}
