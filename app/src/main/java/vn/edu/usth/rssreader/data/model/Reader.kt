package vn.edu.usth.rssreader.data.model

data class Reader(
    val name: String,
    val avatarUrl: String?
) {
    companion object {
        val DEFAULT = Reader(
            name = "RSSReader",
            avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAMO-YLMQm5aAM0NFgSEP6U6kkVUS5P7wJT9HyOcnrt3lbnNOuylSu-nvDnvXyXo8_x6IqwijIDPoUGFAsDei8rpWjhI5IsBj71OlTCK7WqWBGLztQNWLR-j7bf0m_SJNULEEFdaVe7y_fGG56cVGh_TCU6eEj-_DXjOqIsHZt-arSUF67hvsn30rzgWplFmYmpIUhN5bgXTyIGpxpXaockE5bZ6HRPS03UD4B4caIZkDgdyOriV3uP"
        )
    }
}