package vn.edu.usth.rssreader.ui.screen.home


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import vn.edu.usth.rssreader.data.fake.FakeArticles
import vn.edu.usth.rssreader.data.fake.FakeCategories
import vn.edu.usth.rssreader.data.model.Reader
import vn.edu.usth.rssreader.util.greetingForNow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        _uiState.value = HomeUiState(
            greeting = greetingForNow(),
            reader = Reader.DEFAULT,
            hasUnreadNotifications = true,
            categories = FakeCategories.strip,
            selectedCategoryId = FakeCategories.All.id,
            featuredArticle = FakeArticles.featured,
            latestArticles = FakeArticles.latest
        )
    }

    fun onSyncClick() {
        if (_uiState.value.isSyncing) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true) }
            delay(850)
            _uiState.update { it.copy(isSyncing = false) }
        }
    }

    fun onNotificationClick() {
        _uiState.update { it.copy(hasUnreadNotifications = false) }
    }

    fun onCategorySelected(categoryId: String) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
    }

    fun onToggleBookmark(articleId: String, bookmarked: Boolean) {
        _uiState.update { state ->
            val updated = if (bookmarked) {
                state.savedArticleIds + articleId
            } else {
                state.savedArticleIds - articleId
            }
            state.copy(savedArticleIds = updated)
        }
    }


    fun onRefreshClick() {
        if (_uiState.value.isRefreshing) return
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            delay(850)
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }
}