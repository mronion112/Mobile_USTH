package vn.edu.usth.rssreader.ui.screen.detail

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import vn.edu.usth.rssreader.data.fake.FakeSavedData

object BookmarkStore {
    private val _savedIds = MutableStateFlow(FakeSavedData.savedArticleIds)
    val savedIds: StateFlow<Set<String>> = _savedIds.asStateFlow()

    fun isBookmarked(id: String): Boolean = _savedIds.value.contains(id)

    fun toggle(id: String): Boolean {
        var result = false
        _savedIds.update { current ->
            if (current.contains(id)) {
                result = false
                current - id
            } else {
                result = true
                current + id
            }
        }
        return result
    }

    fun set(id: String, bookmarked: Boolean) {
        _savedIds.update { current ->
            if (bookmarked) current + id else current - id
        }
    }
}
