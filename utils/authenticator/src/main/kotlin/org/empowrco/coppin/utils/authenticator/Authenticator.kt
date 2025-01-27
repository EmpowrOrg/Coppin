package org.empowrco.coppin.utils.authenticator

import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.jwt.*
import org.empowrco.coppin.models.UserAccessKey
import org.empowrco.coppin.sources.UsersSource
import org.empowrco.coppin.utils.logs.logDebug
import java.util.Base64
import java.util.UUID

interface Authenticator {
    suspend fun validateSession(email: String): UserIdPrincipal?
    suspend fun validateKey(accessKey: String, type: UserAccessKey.Type): UserIdPrincipal?
    suspend fun validateApp(credential: JWTCredential): JWTPrincipal?
}

internal class RealAuthenticator(
    private val usersSource: UsersSource,
) : Authenticator {

    override suspend fun validateSession(email: String): UserIdPrincipal? {
        val user = usersSource.getUserByEmail(email) ?: return null
        if (!user.isAuthorized) {
            logDebug("User not authorized for session $email")
            return null
        }
        return UserIdPrincipal(user.email)
    }

    override suspend fun validateKey(accessKey: String, type: UserAccessKey.Type): UserIdPrincipal? {
        val keyParts = accessKey.split(".")
        if (keyParts.size != 2) {
            return null
        }
        val keyIdString = Base64.getDecoder().decode(keyParts.first()).decodeToString()
        val keyId = try {
            UUID.fromString(keyIdString)
        } catch (ex: Exception) {
            logDebug("UUID Exception with $accessKey")
            logDebug(ex.localizedMessage)
            return null
        }
        val userAccessKey = usersSource.getKey(keyId) ?: run {
            logDebug("Key not found for $accessKey")
            return null
        }
        val user = usersSource.getUser(userAccessKey.userId) ?: run {
            logDebug("user not found for $accessKey")
            return null
        }
        if (!user.isAuthorized) {
            logDebug("User not authorized for $accessKey")
            return null
        }
        if (accessKey != userAccessKey.key) {
            logDebug("User access key does not match db $userAccessKey")
            return null
        }
        if (userAccessKey.type != type) {
            logDebug("Incorrect key type for api $userAccessKey")
            return null
        }
        return UserIdPrincipal(user.email)
    }

    override suspend fun validateApp(credential: JWTCredential): JWTPrincipal? {
        TODO("Not yet implemented")
    }
}
