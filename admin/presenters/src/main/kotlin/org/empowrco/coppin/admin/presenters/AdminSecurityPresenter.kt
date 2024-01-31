package org.empowrco.coppin.admin.presenters

import kotlinx.datetime.LocalDateTime
import org.empowrco.coppin.admin.backend.AdminSecurityRepository
import org.empowrco.coppin.models.User
import org.empowrco.coppin.utils.authenticator.Authenticator
import org.empowrco.coppin.utils.failure
import org.empowrco.coppin.utils.now
import org.empowrco.coppin.utils.toResult
import org.empowrco.coppin.utils.toUuid

interface AdminSecurityPresenter {
    suspend fun getSecuritySettings(request: GetSecuritySettingsRequest): Result<GetSecuritySettingsResponse>
    suspend fun updateSecuritySettings(request: SaveSecuritySettingsRequest): Result<SaveSecuritySettingsResponse>
}

internal class RealAdminSecurityPresenter(
    private val repo: AdminSecurityRepository,
    private val auth: Authenticator,
) : AdminSecurityPresenter {
    override suspend fun getSecuritySettings(request: GetSecuritySettingsRequest): Result<GetSecuritySettingsResponse> {
        val securitySettings = repo.getSecuritySettings()
        val email = request.email ?: return failure("Unauthorized user")
        val user = repo.getUserByEmail(email) ?: return failure("Unauthorized user")
        if (user.type != User.Type.Admin) {
            return failure("Unauthorized User")
        }
        return GetSecuritySettingsResponse(
            oktaEnabled = securitySettings.oktaEnabled,
            clientId = securitySettings.oktaClientId,
            oktaDomain = securitySettings.oktaDomain,
            clientSecret = securitySettings.oktaClientSecretDisplay,
            userId = user.id.toString(),
        ).toResult()
    }

    override suspend fun updateSecuritySettings(request: SaveSecuritySettingsRequest): Result<SaveSecuritySettingsResponse> {
        val failure = authorizeAdmin<SaveSecuritySettingsResponse>(request.userId)
        if (failure != null) {
            return failure
        }
        val securitySettings = repo.getSecuritySettings()
        if (request.enableOkta) {
            if (request.oktaDomain.isNullOrBlank()) {
                return failure("You must specify your okta domain")
            }
            if (request.clientId.isNullOrBlank()) {
                return failure("You must specify your client id")
            }
            if (request.clientSecret.isNullOrBlank()) {
                return failure("You must specify your client secret")
            }
        }
        val clientSecretDisplay = ("******************************" + request.clientSecret?.takeLast(4)) ?: ""
        val updatedSecuritySettings = securitySettings.copy(
            oktaEnabled = request.enableOkta,
            oktaDomain = request.oktaDomain ?: "",
            oktaClientId = request.clientId ?: "",
            oktaClientSecret = request.clientSecret ?: "",
            oktaClientSecretDisplay = clientSecretDisplay,
            lastModifiedAt = LocalDateTime.now(),
        )
        repo.saveSecuritySettings(updatedSecuritySettings)
        return SaveSecuritySettingsResponse.toResult()
    }

    private suspend fun <T : Any> authorizeAdmin(userIdParam: String): Result<T>? {
        val userId = userIdParam.toUuid() ?: return failure("Invalid user id")
        val user = repo.getUser(userId) ?: return failure("No user found")
        if (user.type != User.Type.Admin) {
            return failure("Unauthorized User")
        }
        return null
    }
}
