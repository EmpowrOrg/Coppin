package org.empowrco.coppin.sources

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.empowrco.coppin.utils.DuplicateKeyException
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

internal suspend fun <T> dbQuery(block: suspend () -> T): T =
    withContext(Dispatchers.IO) {
        newSuspendedTransaction(Dispatchers.IO) {
            if (System.getenv("DEBUG").toBoolean()) {
                addLogger(StdOutSqlLogger)
            }
            try {
                block()
            } catch (ex: ExposedSQLException) {
                if (ex.message?.contains("duplicate key") == true) {
                    throw DuplicateKeyException(ex)
                }
                throw ex
            }

        }
    }
