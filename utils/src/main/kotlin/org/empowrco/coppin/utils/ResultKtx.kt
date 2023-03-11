package org.empowrco.coppin.utils

fun <T> failure(error: String): Result<T> {
    return Result.failure(Exception(error))
}

fun <T> T.toResult(): Result<T> {
    return Result.success(this)
}
