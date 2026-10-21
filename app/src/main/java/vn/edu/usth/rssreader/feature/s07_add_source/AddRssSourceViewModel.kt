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
