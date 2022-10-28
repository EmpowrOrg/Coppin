package org.empowrco.coppin.utils

fun String.ellipsize(limit: Int = 25): String {
    return if (length <= limit) {
        this
    } else {
        take(limit - 3) + "..."
    }

}
