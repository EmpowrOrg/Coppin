package org.empowrco.coppin.utils

import java.util.Locale
import java.util.UUID

fun String.ellipsize(limit: Int = 25): String {
    return if (length <= limit) {
        this
    } else {
        take(limit - 3) + "..."
    }

}

fun String.capitalize(): String {
    return this.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(
            Locale.getDefault()
        ) else it.toString()
    }
}

fun String?.nonEmpty(): String? {
    return if (isNullOrBlank()) {
        null
    } else {
        this
    }
}


fun String.toUuid(): UUID? {
    return try {
        UUID.fromString(this)
    } catch (ex: IllegalArgumentException) {
        null
    }
}
