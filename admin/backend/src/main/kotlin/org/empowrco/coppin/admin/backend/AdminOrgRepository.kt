package org.empowrco.coppin.admin.backend

import org.empowrco.coppin.models.OrgSettings
import org.empowrco.coppin.models.User
import org.empowrco.coppin.sources.SettingsSource
import org.empowrco.coppin.sources.UsersSource
import java.util.UUID

interface AdminOrgRepository {
    suspend fun getOrgSettings(): OrgSettings?
    suspend fun getUser(userId: UUID): User?
    suspend fun saveOrgSettings(settings: OrgSettings): Boolean
    suspend fun getUserByEmail(email: String): User?
    suspend fun createOrgSettings(settings: OrgSettings)
}

class RealAdminOrgRepository(
    private val settingsSource: SettingsSource,
    private val usersSource: UsersSource,
) : AdminOrgRepository {
    override suspend fun getOrgSettings(): OrgSettings? {
        return settingsSource.getOrgSettings()
    }

    override suspend fun createOrgSettings(settings: OrgSettings) {
        settingsSource.createOrgSettings(settings)
    }

    override suspend fun getUser(userId: UUID): User? {
        return usersSource.getUser(userId)
    }

    override suspend fun saveOrgSettings(settings: OrgSettings): Boolean {
        return settingsSource.updateOrgSettings(settings)
    }

    override suspend fun getUserByEmail(email: String): User? {
        return usersSource.getUserByEmail(email)
    }
}
