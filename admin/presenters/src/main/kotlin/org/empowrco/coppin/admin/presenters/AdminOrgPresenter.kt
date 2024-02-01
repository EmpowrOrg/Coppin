package org.empowrco.coppin.admin.presenters

import kotlinx.datetime.LocalDateTime
import org.empowrco.coppin.admin.backend.AdminOrgRepository
import org.empowrco.coppin.models.OrgSettings
import org.empowrco.coppin.models.User
import org.empowrco.coppin.utils.failure
import org.empowrco.coppin.utils.isValidUrl
import org.empowrco.coppin.utils.logs.logError
import org.empowrco.coppin.utils.now
import org.empowrco.coppin.utils.toResult
import java.util.UUID

interface AdminOrgPresenter {
    suspend fun getOrgSettings(request: GetOrgSettingsRequest): Result<GetOrgSettingsResponse>
    suspend fun updateOrgSettings(request: SaveOrgSettingsRequest): Result<SaveOrgSettingsResponse>
}

internal class RealAdminOrgPresenter(
    private val repo: AdminOrgRepository,
) : AdminOrgPresenter {
    override suspend fun getOrgSettings(request: GetOrgSettingsRequest): Result<GetOrgSettingsResponse> {
        val email = request.email ?: return failure("Unauthorized user")
        val user = repo.getUserByEmail(email) ?: return failure("Unauthorized user")
        if (user.type != User.Type.Admin) {
            return failure("Unauthorized User")
        }
        val orgSettings = repo.getOrgSettings() ?: run {
            val currentTime = LocalDateTime.now()
            logError(RuntimeException("Org Settings are missing. This should never happen. tf"))
            OrgSettings(
                id = UUID.randomUUID(),
                edxUsername = "",
                edxClientSecret = "",
                edxApiUrl = "",
                edxClientId = "",
                doctorUrl = "",
                createdAt = currentTime,
                lastModifiedAt = currentTime,
            )
        }
        return GetOrgSettingsResponse(
            edxUsername = orgSettings.edxUsername,
            edxClientId = orgSettings.edxClientId,
            edxApiUrl = orgSettings.edxApiUrl,
            edxClientSecretDisplay = orgSettings.edxClientSecret.displaySecret(),
            doctorUrl = orgSettings.doctorUrl,
        ).toResult()
    }

    override suspend fun updateOrgSettings(request: SaveOrgSettingsRequest): Result<SaveOrgSettingsResponse> {
        val failure = authorizeAdmin<SaveOrgSettingsResponse>(request.userEmail)
        if (failure != null) {
            return failure
        }
        val currentTime = LocalDateTime.now()
        val orgSettings = repo.getOrgSettings() ?: run {
            logError(RuntimeException("Org Settings are missing. This should never happen. tf"))
            val settings = OrgSettings(
                id = UUID.randomUUID(),
                edxUsername = "",
                edxClientSecret = "",
                edxApiUrl = "",
                edxClientId = "",
                doctorUrl = "",
                createdAt = currentTime,
                lastModifiedAt = currentTime,
            )
            repo.createOrgSettings(settings)
            settings
        }
        if (request.edxUsername.isNullOrBlank()) {
            return failure("You must specify your edx username")
        }
        if (request.edxClientId.isNullOrBlank()) {
            return failure("You must specify your client id")
        }
        if (request.edxClientSecret.isNullOrBlank()) {
            return failure("You must specify your client secret")
        }
        if (request.doctorUrl.isNullOrBlank() || !request.doctorUrl.isValidUrl()) {
            return failure("You must input a valid doctor url")
        }
        if (request.edxApiUrl.isNullOrBlank() || !request.edxApiUrl.isValidUrl()) {
            return failure("You must input a valid api url")
        }

        val updateOrgSettings = orgSettings.copy(
            edxUsername = request.edxUsername.trim(),
            edxClientId = request.edxClientId.trim(),
            edxClientSecret = request.edxClientSecret.trim(),
            edxApiUrl = request.edxApiUrl.trim(),
            doctorUrl = request.doctorUrl.trim(),
            lastModifiedAt = currentTime,
        )
        repo.saveOrgSettings(updateOrgSettings)
        return SaveOrgSettingsResponse.toResult()
    }

    private suspend fun <T : Any> authorizeAdmin(email: String): Result<T>? {
        val user = repo.getUserByEmail(email) ?: return failure("No user found")
        if (user.type != User.Type.Admin) {
            return failure("Unauthorized User")
        }
        return null
    }
}
