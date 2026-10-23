package vn.edu.usth.rssreader

import android.app.Application
import vn.edu.usth.rssreader.data.repository.JsonArticleRepository
import vn.edu.usth.rssreader.data.repository.JsonRssSourceRepository

class RssReaderApplication : Application() {
    /** One app-scoped source store so every screen observes the same JSON data. */
    val rssSourceRepository by lazy { JsonRssSourceRepository(applicationContext) }

    val articleRepository by lazy {
        JsonArticleRepository(applicationContext, rssSourceRepository)
    }
}
