package vn.edu.usth.rssreader.data.repository

import kotlinx.coroutines.flow.StateFlow
import vn.edu.usth.rssreader.data.model.Article

interface ArticleRepository {
    /** Danh sách bài đã cache, sort giảm dần theo publishedAt. */
    val articles: StateFlow<List<Article>>

    val bookmarkedArticles: StateFlow<List<Article>>
    /** Tập id các bài đã bookmark (persist qua filesDir). */
    val bookmarkedIds: StateFlow<Set<String>>

    /** true khi refreshAll() đang chạy. */
    val isRefreshing: StateFlow<Boolean>

    /** Lỗi gần nhất của refreshAll(); null nếu thành công. */
    val refreshError: StateFlow<String?>

    /** Fetch lại tất cả các nguồn ACTIVE và merge vào cache. */
    suspend fun refreshAll()

    /** Bật/tắt bookmark cho một article; trả về trạng thái sau khi toggle. */
    suspend fun toggleBookmark(article: Article): Boolean

    suspend fun pruneOrphanedArticles(activeSourceIds: Set<Long>): Int
}