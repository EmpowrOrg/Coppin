package org.empowrco.coppin.sources

import io.ktor.server.routing.*
import kotlinx.datetime.LocalDateTime
import org.empowrco.coppin.db.Applications
import org.empowrco.coppin.db.ApplicationsAccess
import org.empowrco.coppin.models.Application
import org.empowrco.coppin.models.User
import org.empowrco.coppin.utils.now
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import java.util.UUID

interface ApplicationsSource {
    suspend fun createApplication(application: Application)
    suspend fun getApplicationById(id: UUID): Application?
    suspend fun getApplications(): List<Application>
    suspend fun updateApplication(application: Application): Boolean
    suspend fun deleteApplication(id: UUID): Boolean
    suspend fun deleteApplicationAccessGroup(type: User.Type): Boolean
    suspend fun createApplicationAccessGroup(id: UUID, type: User.Type)
}

private class DatabaseApplicationsSource: ApplicationsSource {
    override suspend fun createApplication(application: Application) = dbQuery {
        Applications.insert { 
            it.build(application, false)
        }
        ApplicationsAccess.batchInsert(application.accessGroups, ignore = true) { 
            this[ApplicationsAccess.application] = application.id
            this[ApplicationsAccess.type] = it
            this[ApplicationsAccess.createdAt] = application.createdAt
            this[ApplicationsAccess.lastModifiedAt] = application.lastModifiedAt
        }
        Unit
    }

    override suspend fun getApplicationById(id: UUID): Application? = dbQuery {
        Applications.select { Applications.id eq id }.singleOrNull()?.toApplication()
    }

    override suspend fun getApplications(): List<Application> = dbQuery {
        Applications.selectAll().map { it.toApplication() }
    }

    override suspend fun updateApplication(application: Application): Boolean = dbQuery {
        Applications.update ({ Applications.id eq application.id }) {
            it.build(application, true)
        } > 0
    }

    override suspend fun deleteApplicationAccessGroup(type: User.Type): Boolean = dbQuery {
        TODO("Not yet implemented")
    }

    override suspend fun createApplicationAccessGroup(id: UUID, type: User.Type) = dbQuery {
        val time = LocalDateTime.now()
        ApplicationsAccess.insertIgnore {
            it[ApplicationsAccess.id] = UUID.randomUUID()
            it[ApplicationsAccess.application] = id
            it[ApplicationsAccess.type] = type
            it[ApplicationsAccess.createdAt] = time
            it[ApplicationsAccess.lastModifiedAt] = time
        }
        Unit
    }

    override suspend fun deleteApplication(id: UUID): Boolean = dbQuery {
        Applications.deleteWhere { Applications.id eq id } > 0
    }

    private fun UpdateBuilder<*>.build(application: Application, update: Boolean) {
        if (!update) {
            this[Applications.id] = application.id
            this[Applications.createdAt] = application.createdAt
        }
        this[Applications.name] = application.name
        this[Applications.lastModifiedAt] = application.lastModifiedAt
    }

    private fun ResultRow.toApplication(): Application {
        val applicationId = this[Applications.id].value
        val accessGroups = ApplicationsAccess.select { ApplicationsAccess.application eq applicationId }.map { 
            this[ApplicationsAccess.type]
        }
        return Application(
            id = applicationId,
            name = this[Applications.name],
            createdAt = this[Applications.createdAt],
            accessGroups = accessGroups,
            lastModifiedAt = this[Applications.lastModifiedAt],
        )
    }
}