package vn.edu.usth.rssreader.data.fake

import vn.edu.usth.rssreader.data.model.Article
import vn.edu.usth.rssreader.data.model.Category
import java.time.Instant
import java.time.temporal.ChronoUnit

object FakeCategories {

    val All           = Category("all",         "Tất cả",       2)
    val World         = Category("the-gioi",    "Thế giới",     0)
    val Business      = Category("kinh-doanh",  "Kinh doanh",   1)
    val Tech          = Category("cong-nghe",   "Công nghệ",    2)
    val Sports        = Category("the-thao",    "Thể thao",     3)
    val Entertainment = Category("giai-tri",    "Giải trí",     4)
    val Current       = Category("thoi-su",     "Thời sự",      5)
    val Health        = Category("suc-khoe",    "Sức khỏe",     6)
    val Education     = Category("giao-duc",    "Giáo dục",     7)
    val Science       = Category("khoa-hoc",    "Khoa học",     8)
    val RealEstate    = Category("bat-dong-san","Bất động sản", 9)

    /** 7 pill hiển thị trên Home (giống HTML) */
    val strip: List<Category> = listOf(
        All, World, Business, Tech, Sports, Entertainment, Current
    )
}

object FakeArticles {

    private fun minutesAgo(m: Long): Instant =
        Instant.now().minus(m, ChronoUnit.MINUTES)

    val featured: Article = Article(
        id = "a1",
        title = "Hội nghị thượng đỉnh kinh tế toàn cầu đạt thỏa thuận chuyển đổi xanh lịch sử",
        description = "Đại diện hơn 120 quốc gia đã thông qua nghị định khung với cam kết giải ngân 800 tỷ USD vào các nguồn năng lượng sạch và hạ tầng bền vững giai đoạn 2025–2030.",
        imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuD_WH7OY8DHmRdjivtwUl7GaG5g_tdCzPwWGlScTK-0uw9AbmrdlLIHF2MCT0cXcZwbRjfmZjyArRPvEPImcuRvw2tqmtnaJkdO93NRfzSl0ITpYSMiOtaMnZ-fxbkmBJ4yYtCSshhnmIWJVkUi0qrwPiOU0OYvrl9NONp5OusZOdNLB_xh7W8MO8DydFjNbMDZOiBqxLkwyjoUKmpSHReDrdyQ83siVvJF3vVNzKLH47xOcTdPVCQf",
        category = FakeCategories.World,
        source = "Báo Dân trí",
        author = "Ban Kinh tế Quốc tế",
        publishedAt = minutesAgo(20),
        viewCount = 3_400,
        isRssVerified = true
    )

    val latest: List<Article> = listOf(
        Article(
            id = "a2",
            title = "Việt Nam đẩy mạnh đầu tư hạ tầng bán dẫn và công nghệ AI bán đảo",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDO5dWWL4TpxnsHnYjF6x5ggfiDuwY7jKtn57IleVMWbqXHdMaiiT6CVhKCbd4ulxV8oAgrtO3axoR5RCToHg_In3tsWrNPLCO7q68dCeyoRec-AlIbzwQA0sKB9dmj6ihcUBN6ucBOZ2IoMO_sIFst7q66xWF6KtM9dwdtFacmXmVxg63TeGR9JP9kKTAve8rIdjEtFGFNOtvG1iFaAWDqNm2OdJ6RmXZRRCEDdQUfTnoBiGaPdDol",
            category = FakeCategories.Tech,
            source = "Dân trí • Tin số",
            publishedAt = minutesAgo(35)
        ),
        Article(
            id = "a3",
            title = "Giá vàng hôm nay biến động mạnh khi thị trường tài chính quốc tế phản ứng",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAABLTHXb4pWwRB7RQP35KJ-1CnTlD4sARZd1sOsHNKD_KT00cLOvHp0_BYdyPEStixWtYinEE8b052zwzmPSdlJ3EdUBnEXkquzKSh_9nYEGiKN8Crqurm5KPCgy2tnM9ekmwRFbu5N5iTccRsPSR-oeyMBYAfGR2xWv60ZzHMzen0Q8LHff5BMt1jIGtqU8z66i0WoBKYk-kT-lIK4CTXwiEZEAC0z8LioXkzVHL_iBwvLw26xnUT",
            category = FakeCategories.Business,
            source = "Dân trí • Tài chính",
            publishedAt = minutesAgo(60)
        ),
        Article(
            id = "a4",
            title = "Đội tuyển bóng đá quốc gia chuẩn bị cho chiến dịch vòng loại châu Á",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuB46Z9o4YQnw_xGL0jzePdumvWrq-E6nu-KsdUxd1gpSO7elh9iVReMPrLO4tXWB-734yh5qYZadSOEa9PXXlaW_J6K16deUuBU-2o6U7S05jWqo9cVWj1b0EtaG1dWxQrUmg3eOyLywTnjFa6Lz8J-6LMJbne4VBBIUhjp9Hh2g3AqbPqZ5wCNXwYALZkX6ECAQf3ETJ0OoodxL4Jj3_YBwXfOkzTqRU8jTdjeLI5rMkHzcih1dGq5",
            category = FakeCategories.Sports,
            source = "Dân trí • Thể thao",
            publishedAt = minutesAgo(120)
        )
    )

    val all: List<Article> = listOf(featured) + latest
}