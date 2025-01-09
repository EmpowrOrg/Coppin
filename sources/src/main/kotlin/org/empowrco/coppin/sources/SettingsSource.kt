package org.empowrco.coppin.sources

import kotlinx.datetime.LocalDateTime
import org.empowrco.coppin.models.AiSettings
import org.empowrco.coppin.models.OrgSettings
import org.empowrco.coppin.models.SecuritySettings
import org.empowrco.coppin.utils.now
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

interface SettingsSource {
    suspend fun getSecuritySettings(): SecuritySettings?
    suspend fun createSecuritySettings(): SecuritySettings
    suspend fun updateSecuritySettings(settings: SecuritySettings): Boolean
    suspend fun getOrgSettings(): OrgSettings?
    suspend fun createOrgSettings(settings: OrgSettings)
    suspend fun updateOrgSettings(settings: OrgSettings): Boolean
    suspend fun getAiSettings(): AiSettings?
    suspend fun createAiSettings(settings: AiSettings)
    suspend fun updateAiSettings(settings: AiSettings): Boolean
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
            it[oktaEnabled] = settings.oktaEnabled
            it[oktaDomain] = settings.oktaDomain
            it[oktaClientId] = settings.oktaClientId
            it[oktaClientSecret] = settings.oktaClientSecret
            it[oktaClientSecretDisplay] = settings.oktaClientSecretDisplay
            it[lastModifiedAt] = settings.lastModifiedAt
        }
        result > 0
    }

    override suspend fun getOrgSettings(): OrgSettings? = dbQuery {
        org.empowrco.coppin.db.OrgSettings.selectAll().map {
            return@map OrgSettings(
                id = it[org.empowrco.coppin.db.OrgSettings.id].value,
                doctorUrl = it[org.empowrco.coppin.db.OrgSettings.doctorUrl],
                edxUsername = it[org.empowrco.coppin.db.OrgSettings.edxUsername],
                edxApiUrl = it[org.empowrco.coppin.db.OrgSettings.edxApiUrl],
                edxClientId = it[org.empowrco.coppin.db.OrgSettings.edxClientId],
                edxClientSecret = it[org.empowrco.coppin.db.OrgSettings.edxClientSecret],
                createdAt = it[org.empowrco.coppin.db.OrgSettings.createdAt],
                lastModifiedAt = it[org.empowrco.coppin.db.OrgSettings.lastModifiedAt],
            )
        }.firstOrNull()
    }

    override suspend fun createOrgSettings(settings: OrgSettings) = dbQuery {
        org.empowrco.coppin.db.OrgSettings.insert {
            it[org.empowrco.coppin.db.OrgSettings.id] = settings.id
            it[org.empowrco.coppin.db.OrgSettings.doctorUrl] = settings.doctorUrl
            it[org.empowrco.coppin.db.OrgSettings.edxUsername] = settings.edxUsername
            it[org.empowrco.coppin.db.OrgSettings.edxApiUrl] = settings.edxApiUrl
            it[org.empowrco.coppin.db.OrgSettings.edxClientId] = settings.edxClientId
            it[org.empowrco.coppin.db.OrgSettings.edxClientSecret] = settings.edxClientSecret
            it[org.empowrco.coppin.db.OrgSettings.createdAt] = settings.createdAt
            it[org.empowrco.coppin.db.OrgSettings.lastModifiedAt] = settings.lastModifiedAt
        }
        Unit
    }

    override suspend fun updateOrgSettings(settings: OrgSettings): Boolean = dbQuery {
        org.empowrco.coppin.db.OrgSettings.update {
            it[org.empowrco.coppin.db.OrgSettings.doctorUrl] = settings.doctorUrl
            it[org.empowrco.coppin.db.OrgSettings.edxUsername] = settings.edxUsername
            it[org.empowrco.coppin.db.OrgSettings.edxApiUrl] = settings.edxApiUrl
            it[org.empowrco.coppin.db.OrgSettings.edxClientId] = settings.edxClientId
            it[org.empowrco.coppin.db.OrgSettings.edxClientSecret] = settings.edxClientSecret
            it[org.empowrco.coppin.db.OrgSettings.lastModifiedAt] = settings.lastModifiedAt
        } > 0
    }

    override suspend fun getAiSettings(): AiSettings? = dbQuery {
        org.empowrco.coppin.db.AiSettings.selectAll().map {
            AiSettings(
                id = it[org.empowrco.coppin.db.AiSettings.id].value,
                model = it[org.empowrco.coppin.db.AiSettings.model],
                orgKey = it[org.empowrco.coppin.db.AiSettings.orgKey],
                key = it[org.empowrco.coppin.db.AiSettings.key],
                prePrompt = it[org.empowrco.coppin.db.AiSettings.prePrompt],
                createdAt = it[org.empowrco.coppin.db.AiSettings.createdAt],
                lastModifiedAt = it[org.empowrco.coppin.db.AiSettings.lastModifiedAt],
            )
        }.firstOrNull()
    }

    override suspend fun createAiSettings(settings: AiSettings) = dbQuery {
        org.empowrco.coppin.db.AiSettings.insert {
            it[org.empowrco.coppin.db.AiSettings.id] = settings.id
            it[org.empowrco.coppin.db.AiSettings.model] = settings.model
            it[org.empowrco.coppin.db.AiSettings.orgKey] = settings.orgKey
            it[org.empowrco.coppin.db.AiSettings.prePrompt] = settings.prePrompt
            it[org.empowrco.coppin.db.AiSettings.key] = settings.key
            it[org.empowrco.coppin.db.AiSettings.createdAt] = settings.createdAt
            it[org.empowrco.coppin.db.AiSettings.lastModifiedAt] = settings.lastModifiedAt
        }
        Unit
    }

    override suspend fun updateAiSettings(settings: AiSettings): Boolean = dbQuery {
        org.empowrco.coppin.db.AiSettings.update {
            it[org.empowrco.coppin.db.AiSettings.id] = settings.id
            it[org.empowrco.coppin.db.AiSettings.model] = settings.model
            it[org.empowrco.coppin.db.AiSettings.orgKey] = settings.orgKey
            it[org.empowrco.coppin.db.AiSettings.prePrompt] = settings.prePrompt
            it[org.empowrco.coppin.db.AiSettings.key] = settings.key
            it[org.empowrco.coppin.db.AiSettings.lastModifiedAt] = settings.lastModifiedAt
        } > 0
    }
}
