package vn.edu.usth.rssreader.ui.state

sealed interface ScreenState<out T> {
    data object Loading : ScreenState<Nothing>

    data class Content<T>(val data: T) : ScreenState<T>

    data class Empty(
        val title: String,
        val message: String,
        val actionLabel: String? = null,
    ) : ScreenState<Nothing>

    data class Error(
        val message: String,
        val retryLabel: String = "Thử lại",
    ) : ScreenState<Nothing>
}
