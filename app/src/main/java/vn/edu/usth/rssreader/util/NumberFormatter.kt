package vn.edu.usth.rssreader.util

/**
 * Format số view gọn:
 *   999      → "999"
 *   3400     → "3.4k"
 *   12_000   → "12k"
 *   1_500_000 → "1.5M"
 */
fun formatCompact(n: Int): String {
    if (n < 1_000) return n.toString()

    val k = n / 1_000.0
    if (k < 10) {
        return if (k == k.toInt().toDouble()) "${k.toInt()}k"
        else "%.1fk".format(k)
    }
    if (n < 1_000_000) return "${k.toInt()}k"

    val m = n / 1_000_000.0
    return if (m == m.toInt().toDouble()) "${m.toInt()}M"
    else "%.1fM".format(m)
}