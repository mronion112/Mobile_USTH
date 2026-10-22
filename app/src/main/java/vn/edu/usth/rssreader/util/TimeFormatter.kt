package vn.edu.usth.rssreader.util

import java.time.Duration
import java.time.Instant

/**
 * Chuyển Instant → chuỗi tương đối tiếng Việt:
 *   < 1 phút   → "Vừa xong"
 *   < 60 phút  → "X phút trước"
 *   < 24 giờ   → "X giờ trước"
 *   < 7 ngày   → "X ngày trước"
 *   còn lại    → "dd/MM/yyyy"
 */
fun formatRelativeTime(
    instant: Instant,
    now: Instant = Instant.now()
): String {
    val seconds = Duration.between(instant, now).seconds
    if (seconds < 0) return "Vừa xong"

    val minutes = seconds / 60
    val hours   = seconds / 3600
    val days    = seconds / 86_400

    return when {
        minutes < 1   -> "Vừa xong"
        minutes < 60  -> "$minutes phút trước"
        hours   < 24  -> "$hours giờ trước"
        days    < 7   -> "$days ngày trước"
        else -> {
            val date = instant.atZone(java.time.ZoneId.systemDefault()).toLocalDate()
            "%02d/%02d/%04d".format(
                date.dayOfMonth,
                date.monthValue,
                date.year
            )
        }
    }
}