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
