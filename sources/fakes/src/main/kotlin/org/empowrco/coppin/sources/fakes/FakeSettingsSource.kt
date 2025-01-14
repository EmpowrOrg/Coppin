package org.empowrco.coppin.sources.fakes

import kotlinx.datetime.LocalDateTime
import org.empowrco.coppin.models.AiSettings
import org.empowrco.coppin.models.OrgSettings
import org.empowrco.coppin.models.SecuritySettings
import org.empowrco.coppin.sources.SettingsSource
import org.empowrco.coppin.utils.now
import java.util.UUID

class FakeSettingsSource : SettingsSource {

    private var securitySettings: SecuritySettings? = null
    private var orgSettings: OrgSettings? = null
    private var aiSettings: AiSettings? = null

    override suspend fun getSecuritySettings(): SecuritySettings? {
        return securitySettings
    }

    override suspend fun createSecuritySettings(): SecuritySettings {
        securitySettings = SecuritySettings(
            id = UUID.randomUUID(),
            oktaEnabled = false,
            oktaClientId = "",
            oktaClientSecretDisplay = "",
            oktaDomain = "",
            oktaClientSecret = "",
            lastModifiedAt = LocalDateTime.now(),
            createdAt = LocalDateTime.now(),
        )
        return securitySettings!!
    }

    override suspend fun updateSecuritySettings(settings: SecuritySettings): Boolean {
        securitySettings = settings
        return true
    }

    override suspend fun getOrgSettings(): OrgSettings? {
        return orgSettings
    }

    override suspend fun createOrgSettings(settings: OrgSettings) {
        orgSettings = settings
    }

    override suspend fun updateOrgSettings(settings: OrgSettings): Boolean {
        orgSettings = settings
        return true
    }

    override suspend fun getAiSettings(): AiSettings? {
        return aiSettings
    }

    override suspend fun createAiSettings(settings: AiSettings) {
        aiSettings = settings
    }

    override suspend fun updateAiSettings(settings: AiSettings): Boolean {
        aiSettings = settings
        return true
    }
}