package org.empowrco.coppin.utils.logs

import ch.qos.logback.classic.Level
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val fileLogger: ch.qos.logback.classic.Logger =
    (LoggerFactory.getLogger("org.empowrco.coppin") as ch.qos.logback.classic.Logger)
        .apply {
            this.level = Level.DEBUG
        }
private val stdLogger: ch.qos.logback.classic.Logger =
    (LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as ch.qos.logback.classic.Logger)
        .apply {
            this.level = Level.ALL
        }

fun logDebug(message: String) {
    log(null, message, Level.DEBUG)
}

fun logInfo(message: String) {
    log(null, message, Level.INFO)
}

fun logError(exception: Throwable, message: String = exception.localizedMessage) {
    log(exception, message, Level.ERROR)
}

private fun log(exception: Throwable?, message: String, level: Level) {
    when (level) {
        Level.DEBUG -> {
            exception?.let {
                stdLogger.debug(message, it)
                fileLogger.debug(message, it)
            } ?: run {
                stdLogger.debug(message)
                fileLogger.debug(message)
            }
        }

        Level.INFO -> {
            exception?.let {
                fileLogger.info(message, it)
                stdLogger.info(message, it)
            } ?: run {
                stdLogger.info(message)
                fileLogger.info(message)
            }
        }

        Level.TRACE -> {
            exception?.let {
                fileLogger.trace(message, it)
                stdLogger.trace(message, it)
            } ?: run {
                stdLogger.trace(message)
                fileLogger.trace(message)
            }
        }

        Level.WARN -> {
            exception?.let {
                fileLogger.warn(message, it)
                stdLogger.debug(message, it)
            } ?: run {
                stdLogger.warn(message)
                fileLogger.warn(message)
            }
        }

        Level.ERROR -> {
            exception?.let {
                fileLogger.error(message, it)
                stdLogger.error(message, it)
            } ?: run {
                stdLogger.error(message)
                fileLogger.error(message)
            }
        }
    }
}
