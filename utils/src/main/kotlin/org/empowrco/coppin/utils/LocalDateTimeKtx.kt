package org.empowrco.coppin.utils

import kotlinx.datetime.LocalDateTime

fun LocalDateTime.Companion.now(): LocalDateTime {
    val time = java.time.LocalDateTime.now()
    return LocalDateTime(
        year = time.year,
        month = time.month,
        dayOfMonth = time.dayOfMonth,
        hour = time.hour,
        minute = time.minute
    )
}
