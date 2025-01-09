package org.empowrco.coppin.admin.presenters

import kotlinx.datetime.LocalDateTime
import org.empowrco.coppin.admin.backend.AdminAiRepository
import org.empowrco.coppin.models.AiSettings
import org.empowrco.coppin.models.User
import org.empowrco.coppin.utils.failure
import org.empowrco.coppin.utils.now
import org.empowrco.coppin.utils.toResult
import java.util.UUID

interface AdminAiPresenter {
    suspend fun getAiSettings(request: GetAiSettingsRequest): Result<GetAiSettingsResponse>
    suspend fun updateAiSettings(request: SaveAiSettingsRequest): Result<SaveAiSettingsResponse>
}

internal class RealAdminAiPresenter(
    private val repo: AdminAiRepository,
) : AdminAiPresenter {
    override suspend fun getAiSettings(request: GetAiSettingsRequest): Result<GetAiSettingsResponse> {
        val email = request.email ?: return failure("Unauthorized user")
        val user = repo.getUserByEmail(email) ?: return failure("Unauthorized user")
        if (user.type != User.Type.Admin) {
            return failure("Unauthorized User")
        }
        val aiSettings = repo.getAiSettings()
        return GetAiSettingsResponse(
            model = aiSettings?.model ?: "",
            org = aiSettings?.orgKey ?: "",
            key = aiSettings?.key ?: "",
            prePrompt = aiSettings?.prePrompt ?: "",
        ).toResult()
    }

    override suspend fun updateAiSettings(request: SaveAiSettingsRequest): Result<SaveAiSettingsResponse> {
        val failure = authorizeAdmin<SaveAiSettingsResponse>(request.userEmail)
        if (failure != null) {
            return failure
        }
        var isNew = false
        val currentTime = LocalDateTime.now()
        val aiSettings = repo.getAiSettings() ?: run {
            isNew = true
            AiSettings(
                id = UUID.randomUUID(),
                key = "",
                orgKey = "",
                model = "",
                prePrompt = "",
                createdAt = currentTime,
                lastModifiedAt = currentTime,
            )
        }
        if (request.model.isNullOrBlank()) {
            return failure("You must specify your model")
        }
        if (request.orgKey.isNullOrBlank()) {
            return failure("You must specify your org key")
        }
        if (request.key.isNullOrBlank()) {
            return failure("You must specify your key")
        }

        val updatedAiSettings = aiSettings.copy(
            key = request.key.trim(),
            orgKey = request.orgKey.trim(),
            model = request.model.trim(),
            prePrompt = request.prePrompt?.trim() ?: "",
            lastModifiedAt = currentTime,
        )
        if (isNew) {
            repo.createAiSettings(updatedAiSettings)
        } else {
            repo.saveAiSettings(updatedAiSettings)
        }
        return SaveAiSettingsResponse.toResult()
    }

    private suspend fun <T : Any> authorizeAdmin(email: String): Result<T>? {
        val user = repo.getUserByEmail(email) ?: return failure("No user found")
        if (user.type != User.Type.Admin) {
            return failure("Unauthorized User")
        }
        return null
    }
}
