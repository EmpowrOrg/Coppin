package org.empowrco.coppin.sources

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.empowrco.coppin.utils.DuplicateKeyException
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.BooleanColumnType
import org.jetbrains.exposed.sql.CustomFunction
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.QueryBuilder
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
                    throw DuplicateKeyException()
                }
                throw ex
            }

        }
    }

private fun customBooleanFunction(
    functionName: String, postfix: String = "", vararg params: Expression<*>,
): CustomFunction<Boolean?> =
    object : CustomFunction<Boolean?>(functionName, BooleanColumnType(), *params) {
        override fun toQueryBuilder(queryBuilder: QueryBuilder) {
            super.toQueryBuilder(queryBuilder)
            if (postfix.isNotEmpty()) {
                queryBuilder.append(postfix)
            }
        }
    }

internal fun distinctOn(vararg expressions: Expression<*>): CustomFunction<Boolean?> = customBooleanFunction(
    functionName = "DISTINCT ON",
    postfix = " TRUE",
    params = expressions
)

internal fun Query.toSQL(): String = prepareSQL(QueryBuilder(false))
