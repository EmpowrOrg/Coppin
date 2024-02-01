package org.empowrco.coppin.admin.backend

import org.empowrco.coppin.models.AiSettings
import org.empowrco.coppin.models.User
import org.empowrco.coppin.sources.SettingsSource
import org.empowrco.coppin.sources.UsersSource
import java.util.UUID

interface AdminAiRepository {
    suspend fun getAiSettings(): AiSettings?
    suspend fun getUser(userId: UUID): User?
    suspend fun saveAiSettings(settings: AiSettings): Boolean
    suspend fun getUserByEmail(email: String): User?
    suspend fun createAiSettings(settings: AiSettings)
}

class RealAdminAiRepository(
    private val settingsSource: SettingsSource,
    private val usersSource: UsersSource,
) : AdminAiRepository {
    override suspend fun getAiSettings(): AiSettings? {
        return settingsSource.getAiSettings()
    }

    override suspend fun createAiSettings(settings: AiSettings) {
        settingsSource.createAiSettings(settings)
    }

    override suspend fun getUser(userId: UUID): User? {
        return usersSource.getUser(userId)
    }

    override suspend fun saveAiSettings(settings: AiSettings): Boolean {
        return settingsSource.updateAiSettings(settings)
    }

    override suspend fun getUserByEmail(email: String): User? {
        return usersSource.getUserByEmail(email)
    }
}
