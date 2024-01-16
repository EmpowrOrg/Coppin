package org.empowrco.coppin.sources

import kotlinx.datetime.LocalDateTime
import org.empowrco.coppin.models.SecuritySettings
import org.empowrco.coppin.utils.now
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

interface SettingsSource {
    suspend fun getSecuritySettings(): SecuritySettings?
    suspend fun createSecuritySettings()
    suspend fun updateSecuritySettings(settings: SecuritySettings): Boolean
}

internal class RealSettingsSource : SettingsSource {
    override suspend fun getSecuritySettings(): SecuritySettings? = dbQuery {
        org.empowrco.coppin.db.SecuritySettings.selectAll().map {
            SecuritySettings(
                id = it[org.empowrco.coppin.db.SecuritySettings.id].value,
                oktaEnabled = it[org.empowrco.coppin.db.SecuritySettings.okta],
                createdAt = it[org.empowrco.coppin.db.SecuritySettings.createdAt],
                lastModifiedAt = it[org.empowrco.coppin.db.SecuritySettings.lastModifiedAt],
            )
        }.firstOrNull()
    }

    override suspend fun createSecuritySettings() = dbQuery {
        val currentTime = LocalDateTime.now()
        org.empowrco.coppin.db.SecuritySettings.insert {
            it[okta] = false
            it[lastModifiedAt] = currentTime
            it[createdAt] = currentTime
        }
        Unit
    }

    override suspend fun updateSecuritySettings(settings: SecuritySettings): Boolean = dbQuery {
        val result = org.empowrco.coppin.db.SecuritySettings.update {
            it[okta] = false
            it[lastModifiedAt] = settings.lastModifiedAt
        }
        result > 0
    }
}
