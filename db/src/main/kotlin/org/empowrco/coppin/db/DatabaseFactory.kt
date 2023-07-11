package org.empowrco.coppin.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    private val tables = listOf(
        AssignmentCodes,
        Assignments,
        Courses,
        CoursesUsers,
        Languages,
        Submissions,
        UserAccessKeys,
        Users,
    ).toTypedArray()

    fun init() {
        Database.connect(hikari())
        transaction {
            SchemaUtils.createMissingTablesAndColumns(*tables)
        }
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig()
        val databaseDriver = System.getenv("DATABASE_DRIVER")
        config.dataSourceClassName = databaseDriver
        config.addDataSourceProperty("databaseName", System.getenv("DATABASE_NAME"))
        config.addDataSourceProperty("portNumber", System.getenv("DATABASE_PORT"))
        config.addDataSourceProperty("serverName", System.getenv("DATABASE_SERVER"))
        config.maximumPoolSize = 3
        config.isAutoCommit = false
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        val user = System.getenv("DB_USER") // 3
        if (user != null) {
            config.username = user
        }
        val password = System.getenv("DB_PASSWORD") // 4
        if (password != null) {
            config.password = password
        }
        config.validate()
        return HikariDataSource(config)
    }
}
