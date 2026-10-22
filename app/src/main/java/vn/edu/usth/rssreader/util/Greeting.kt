package vn.edu.usth.rssreader.util

import java.time.LocalTime

fun greetingForNow(hourOverride: Int? = null): String {
    val hour = hourOverride ?: LocalTime.now().hour
    return when (hour) {
        in 5..10  -> "Xin chào buổi sáng"
        in 11..12 -> "Xin chào buổi trưa"
        in 13..17 -> "Xin chào buổi chiều"
        in 18..21 -> "Xin chào buổi tối"
        else      -> "Xin chào buổi đêm"
    }
}