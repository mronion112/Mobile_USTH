package vn.edu.usth.rssreader.data.fake

import vn.edu.usth.rssreader.data.model.Article
import vn.edu.usth.rssreader.data.model.Category
import java.time.Instant
import java.time.temporal.ChronoUnit

// ─────────────────────────────────────────────────────────────
// 1. Category pills cho Explore
// ─────────────────────────────────────────────────────────────
object FakeExploreTopics {
    private val All            = Category("all",         "Tất cả",           2)
    private val AI             = Category("ai",          "Trí tuệ nhân tạo", 2)
    private val DigitalEconomy = Category("kinh-te-so",  "Kinh tế số",       5)
    private val Current        = Category("thoi-su",     "Thời sự",          5)
    private val EV             = Category("xe-dien",     "Xe điện",          3)
    private val Health         = Category("y-te",        "Y tế",             6)

    val list = listOf(All, AI, DigitalEconomy, Current, EV, Health)
}

// ─────────────────────────────────────────────────────────────
// 2. Trending topics (3 card carousel)
// ─────────────────────────────────────────────────────────────
object FakeExploreTrending {
    private fun minutesAgo(m: Long): Instant =
        Instant.now().minus(m, ChronoUnit.MINUTES)

    // Category riêng cho trending — name = badge label, colorIndex = màu badge
    private val AIBadge      = Category("trending-ai", "AI & Đột phá", 2) // teal
    private val EVBadge      = Category("trending-ev", "Xe Điện",      1) // orange
    private val DigitalBadge = Category("trending-de", "Kinh Tế Số",   0) // blue

    val list: List<Article> = listOf(
        Article(
            id = "t1",
            title = "OpenAI và các tập đoàn công nghệ chạy đua trung tâm dữ liệu mới",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuC8hWzF9H1my-0TMQf2QO_1sHc7xAKqRNCKLs1j7pdHjODqsbK-CCVNqlZYj0CSiZ_9LJu2snQgtaEogMq-eqiNDMoXYEzbvmHhEeWttE-SJI6_yWaQDDRNC0t3BcCvGLmmV_9__hv5ZKeafvvh3r0e6-aOVkcdVPIGV8KCwDk443SrGyHvkkNP8ds9FNBwBmnMADji30mn6dCaG1JmGhp0qaZU-0gqU4AfoqEgsAKmMyvavd2mSwih",
            category = AIBadge,
            source = "VnExpress",
            publishedAt = minutesAgo(12)
        ),
        Article(
            id = "t2",
            title = "Thị trường pin thể rắn và bước nhảy vọt xe điện 2025",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDjKzrx-YV03HNqOd8vsMqbge2wbNSw5v0mBk8cLmASkZVAB6ucSxwdCZUE_mD3_DmxGvr6xEESCfaeTvPzY3MVU6BNqT4AEUHYY7f3zWI7SKz7lOfuagDgM57pO7Zj1lLvt1Rdqkv80mJCRXdifT_O83k3OakRkwtBUZr0t2lkp-05tao5lJtnZu-IdBrZEd8cX1HI70SVbqyPz7kU16oj0moyrXR1sKZqswl30XyKQNQ1DyZY13lb",
            category = EVBadge,
            source = "VnExpress",
            publishedAt = minutesAgo(45)
        ),
        Article(
            id = "t3",
            title = "Dòng vốn mạo hiểm quay trở lại hệ sinh thái khởi nghiệp số",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDQCCH7uR34Pjo8gA1ER-esNoozuWtSIWbalKZDJvLLjSuOclGy2lPXkEjiV63ny3eQAo6P0U6Nm7ynypcWRbot1JIHWeY0ME46UxqMzesq8-lsWVPQytqKA_vPGispfyrI0GIXdzjNmhOyXjPz5hRa9Apre91E3fmFNQJfv59TupBiJfXmvJydxABuyeabsOltxS4ymFnzdUvkC5ohcL9mXVpJ8V6cUUGkSbsgbPMntfjkFm4HnlUB",
            category = DigitalBadge,
            source = "VnExpress",
            publishedAt = minutesAgo(60)
        )
    )
}

// ─────────────────────────────────────────────────────────────
// 3. Search results (4 item list)
// ─────────────────────────────────────────────────────────────
object FakeExploreArticles {
    private fun minutesAgo(m: Long): Instant =
        Instant.now().minus(m, ChronoUnit.MINUTES)

    private val Tech      = Category("cong-nghe",  "Công nghệ",        2)
    private val Business  = Category("kinh-doanh", "Kinh doanh",       1)
    private val Health    = Category("suc-khoe",   "Y tế & Sức khỏe",  6)
    private val Education = Category("giao-duc",   "Giáo dục",         7)

    val results = listOf(
        Article(
            id = "e-a1",
            title = "Ứng dụng Trí tuệ nhân tạo giúp nâng cao năng suất kỹ sư phần mềm Việt Nam",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDBcLItdzdckgDhLJW3WqgpwGfwsZkIG3NXVy6OINDprXLIECebN1nQf5ZkaBGn__frqA4hUs68uFYLAiFf5filLLPfTtU5P24GYUcZF7nceytF9OpgAm1k_KWE3Nsudzk8pWef__T1ErmYitS5N1x6DxSODFBkBwP9m5HII5vRS5RBeafj-zEBjg2fJJxtAxQ4K9LfUNXIT-V7-oOprBk_Hj6d-TmByq-bhmuU4-cwK5vHJspmeUzd",
            category = Tech,
            source = "Dân trí",
            publishedAt = minutesAgo(15),
            readingTimeMinutes = 4
        ),
        Article(
            id = "e-a2",
            title = "Làn sóng đầu tư vào chip bán dẫn và hệ sinh thái Trí tuệ nhân tạo tại Đà Nẵng",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBqOaNhcIXLuGiACIFMVWON--wRq5xtm8_hJ5Y3zR0fpvyu2J4Dfyt03ICyfhzrOrsbDgBqHo5mzhZYQZ9Mp0Cg5inQzvijYKf7pXiEoELMIOvqvoIZzuEQ6FXR_YrPcMSdragLSAkdC9t7dwhTDtvn7rQyrli6KNvRcoq-O2b6T94Z8-1YjstXDaV_d4xl9rF_QaNcf5TB1dEDgMPf2cskvlmsKCoXaRYn6P-lHC8FSy0QjWaKXVZp",
            category = Business,
            source = "Dân trí",
            publishedAt = minutesAgo(38),
            readingTimeMinutes = 6
        ),
        Article(
            id = "e-a3",
            title = "Bệnh viện triển khai mô hình Trí tuệ nhân tạo chẩn đoán sớm ung thư phổi qua phim X-quang",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDFMoTNxQi38cOUJqZoB4hgq20sQKrJ68tOYpvFprexfm-hJtp6OeYd93732VwvU3Kg49DypEpzQK4LNiyzuB45wUtqRhIgsn4T213KLcFMW1YjnNkFJBREKhmDniGHhJW-y6nSozNuLH_ytCcf4lLr1dakSjaqgUCZaE_ZrzjMj1IIRCld8zmCTXugKWCkqm-wrFhXarbRXUCdIFwYNvIIA5xOIJwA116Y6EyraJw7bzNJ-mp8ypZI",
            category = Health,
            source = "Dân trí",
            publishedAt = minutesAgo(60),
            readingTimeMinutes = 5
        ),
        Article(
            id = "e-a4",
            title = "Đại học Quốc gia mở rộng chuyên ngành đào tạo Trí tuệ nhân tạo ứng dụng thực tiễn",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDpyvfAMIVcG56AVDEhWqV8m8WnVdvo0HKp53LmSZeKMOaXE_8LxUM2pbZOREz96fewu3akpTTnXP5dPwjJvM44auPpHg3UKB7i6Aq4kSG7mIfHIEplhhz5P6WhTmYRotZg7F_UBpRn5CDJHjBF64cqvjrwX5QtMMdNkfm_fhA5A1IKdBEMw_ehUG-Oub9xddqF4O2vy-KQWvP1EE1TsQb5hajGqnfbiLHF9TIMr_0Zx43HEcpfxT4V",
            category = Education,
            source = "Dân trí",
            publishedAt = minutesAgo(120),
            readingTimeMinutes = 3
        )
    )

    val initialBookmarkedId: String = "e-a3"
}