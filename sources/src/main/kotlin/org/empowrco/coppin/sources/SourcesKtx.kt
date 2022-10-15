package org.empowrco.coppin.sources

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

internal suspend fun <T> dbQuery(block: suspend () -> T): T =
    withContext(Dispatchers.IO) {
        newSuspendedTransaction(Dispatchers.IO) {
            if (System.getenv("DEBUG").toBoolean()) {
                addLogger(StdOutSqlLogger)
            }
            block()
        }
    }
