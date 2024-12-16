package org.empowrco.coppin.sources.fakes

import org.empowrco.coppin.models.AiSettings
import org.empowrco.coppin.models.OrgSettings
import org.empowrco.coppin.models.SecuritySettings
import org.empowrco.coppin.sources.SettingsSource

class FakeSettingsSource : SettingsSource {
    override suspend fun getSecuritySettings(): SecuritySettings? {
        TODO("Not yet implemented")
    }

    override suspend fun createSecuritySettings(): SecuritySettings {
        TODO("Not yet implemented")
    }

    override suspend fun updateSecuritySettings(settings: SecuritySettings): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getOrgSettings(): OrgSettings? {
        TODO("Not yet implemented")
    }

    override suspend fun createOrgSettings(settings: OrgSettings) {
        TODO("Not yet implemented")
    }

    override suspend fun updateOrgSettings(settings: OrgSettings): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getAiSettings(): AiSettings? {
        TODO("Not yet implemented")
    }

    override suspend fun createAiSettings(settings: AiSettings) {
        TODO("Not yet implemented")
    }

    override suspend fun updateAiSettings(settings: AiSettings): Boolean {
        TODO("Not yet implemented")
    }
}