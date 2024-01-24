package org.empowrco.coppin.admin.backend

import org.empowrco.coppin.models.SecuritySettings
import org.empowrco.coppin.models.User
import org.empowrco.coppin.sources.SettingsSource
import org.empowrco.coppin.sources.UsersSource
import java.util.UUID

interface AdminSecurityRepository {
    suspend fun getSecuritySettings(): SecuritySettings
    suspend fun getUser(userId: UUID): User?
    suspend fun saveSecuritySettings(settings: SecuritySettings): Boolean
    suspend fun getUserByEmail(email: String): User?
}

class RealAdminSecurityRepository(
    private val settingsSource: SettingsSource,
    private val usersSource: UsersSource,
) : AdminSecurityRepository {
    override suspend fun getSecuritySettings(): SecuritySettings {
        return settingsSource.getSecuritySettings() ?: settingsSource.createSecuritySettings()
    }

    override suspend fun getUser(userId: UUID): User? {
        return usersSource.getUser(userId)
    }

    override suspend fun saveSecuritySettings(settings: SecuritySettings): Boolean {
        return settingsSource.updateSecuritySettings(settings)
    }

    override suspend fun getUserByEmail(email: String): User? {
        return usersSource.getUserByEmail(email)
    }
}
