package org.empowrco.coppin.sources

import kotlinx.datetime.LocalDateTime
import org.empowrco.coppin.models.SecuritySettings
import org.empowrco.coppin.utils.now
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

interface SettingsSource {
    suspend fun getSecuritySettings(): SecuritySettings?
    suspend fun createSecuritySettings(): SecuritySettings
    suspend fun updateSecuritySettings(settings: SecuritySettings): Boolean
}

internal class RealSettingsSource : SettingsSource {
    override suspend fun getSecuritySettings(): SecuritySettings? = dbQuery {
        org.empowrco.coppin.db.SecuritySettings.selectAll().map {
            SecuritySettings(
                id = it[org.empowrco.coppin.db.SecuritySettings.id].value,
                oktaEnabled = it[org.empowrco.coppin.db.SecuritySettings.oktaEnabled],
                oktaDomain = it[org.empowrco.coppin.db.SecuritySettings.oktaDomain],
                oktaClientSecret = it[org.empowrco.coppin.db.SecuritySettings.oktaClientSecret],
                oktaClientId = it[org.empowrco.coppin.db.SecuritySettings.oktaClientId],
                oktaClientSecretDisplay = it[org.empowrco.coppin.db.SecuritySettings.oktaClientSecretDisplay],
                createdAt = it[org.empowrco.coppin.db.SecuritySettings.createdAt],
                lastModifiedAt = it[org.empowrco.coppin.db.SecuritySettings.lastModifiedAt],
            )
        }.firstOrNull()
    }

    override suspend fun createSecuritySettings(): SecuritySettings {
        dbQuery {
            val currentTime = LocalDateTime.now()
            org.empowrco.coppin.db.SecuritySettings.insert {
                it[oktaEnabled] = false
                it[oktaDomain] = ""
                it[oktaClientId] = ""
                it[oktaClientSecret] = ""
                it[oktaClientSecretDisplay] = ""
                it[lastModifiedAt] = currentTime
                it[createdAt] = currentTime
            }
            Unit
        }
        return getSecuritySettings()!!
    }

    override suspend fun updateSecuritySettings(settings: SecuritySettings): Boolean = dbQuery {
        val result = org.empowrco.coppin.db.SecuritySettings.update {
            it[oktaEnabled] = false
            it[oktaDomain] = settings.oktaDomain
            it[oktaClientId] = settings.oktaClientId
            it[oktaClientSecret] = settings.oktaClientSecret
            it[oktaClientSecretDisplay] = settings.oktaClientSecretDisplay
            it[lastModifiedAt] = settings.lastModifiedAt
        }
        result > 0
    }
}
