package org.empowrco.coppin.utils

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import java.time.format.DateTimeFormatter

fun LocalDateTime.Companion.now(): LocalDateTime {
    val time = java.time.LocalDateTime.now()
    return LocalDateTime(
        year = time.year,
        month = time.month,
        dayOfMonth = time.dayOfMonth,
        hour = time.hour,
        minute = time.minute,
        second = time.second,
        nanosecond = time.nano,
    )
}

fun LocalDateTime.monthDayYear(): String {
    val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
    return this.toJavaLocalDateTime().format(formatter)
}

fun String.monthDayYear(from: String = "yyyy-MM-dd'T'HH:mm:ss'Z'"): String? {
    val dateTime = toLocalDateTime(from) ?: return null
    return dateTime.monthDayYear()
}

fun String.toLocalDateTime(format: String): LocalDateTime? {
    return try {
        val formatter = DateTimeFormatter.ofPattern(format)
        java.time.LocalDateTime.parse(this, formatter).toKotlinLocalDateTime()
    } catch (ex: Exception) {
        null
    }
}
