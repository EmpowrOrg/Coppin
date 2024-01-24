package org.empowrco.coppin.utils.authenticator

import io.ktor.server.auth.UserIdPrincipal
import io.ktor.util.hex
import org.empowrco.coppin.sources.UsersSource
import org.empowrco.coppin.utils.logs.logDebug
import java.util.Base64
import java.util.UUID
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

interface Authenticator {
    suspend fun validateSession(email: String): UserIdPrincipal?
    suspend fun validateKey(accessKey: String): UserIdPrincipal?
    suspend fun hash(password: String): String
    suspend fun isValidPassword(password: String): Result<Boolean>
}

internal class RealAuthenticator(
    private val usersSource: UsersSource,
) : Authenticator {

    private val hashKey = hex(System.getenv("SECRET_KEY"))
    private val algorithm = "HmacSHA1"


    override suspend fun validateSession(email: String): UserIdPrincipal? {
        val user = usersSource.getUserByEmail(email) ?: return null
        if (!user.isAuthorized) {
            logDebug("User not authorized for session $email")
            return null
        }
        return UserIdPrincipal(user.email)
    }

    override suspend fun validateKey(accessKey: String): UserIdPrincipal? {
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
        return UserIdPrincipal(user.email)
    }

    override suspend fun hash(password: String): String {
        val hmac = Mac.getInstance(algorithm)
        val hmacKey = SecretKeySpec(hashKey, algorithm)
        hmac.init(hmacKey)
        return hex(hmac.doFinal(password.toByteArray()))
    }

    override suspend fun isValidPassword(password: String): Result<Boolean> {
        if (password.contains("(.*?)\\s(.*?)")) {
            return Result.failure(Exception("Must not contain any spaces"))
        } else if (password.length < 8) {
            return Result.failure(Exception("Must be at least 8 characters"))
        } else if (password.length > 64) {
            return Result.failure(Exception("Cannot be over 64 characters"))
        } else if (!password.any { it.isDigit() }) {
            return Result.failure(Exception("Must contain at least one digit"))
        } else if (!password.any { it.isUpperCase() }) {
            return Result.failure(Exception("Must contain at least one uppercase character"))
        } else if (!password.any { it.isLowerCase() }) {
            return Result.failure(Exception("Must contain at least one lowercase character"))
        } else if (!password.any { "~`! @#$%^&*()_-+={[}]|\\:;\"<,>.?/".contains(it) }) {
            return Result.failure(Exception("Must contain at least one symbol"))
        }
        return Result.success(true)
    }


}
