package org.empowrco.coppin.utils.authenticator

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.jwt.JWTCredential
import io.ktor.util.hex
import org.empowrco.coppin.sources.UsersSource
import java.util.UUID
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

interface Authenticator {
    val verifier: JWTVerifier
    suspend fun validate(email: String, password: String): UserIdPrincipal?
    suspend fun validate(jwt: JWTCredential): UserIdPrincipal?
    suspend fun validateSession(id: String): UserIdPrincipal?
    suspend fun hash(password: String): String
    suspend fun isValidPassword(password: String): Result<Boolean>

}

internal class RealAuthenticator(
    private val usersSource: UsersSource,
) : Authenticator {

    private val hashKey = hex(System.getenv("SECRET_KEY"))
    private val algorithm = "HmacSHA1"
    private val hmacKey = SecretKeySpec(hashKey, algorithm)
    private val issuer = "adminServer"
    private val jwtSecret = System.getenv("JWT_SECRET") // 1
    private val jwtAlgorithm = Algorithm.HMAC512(jwtSecret)
    override val verifier: JWTVerifier = JWT
        .require(jwtAlgorithm)
        .withIssuer(issuer)
        .build()

    override suspend fun validate(email: String, password: String): UserIdPrincipal? {
        val user = usersSource.getUserByEmail(email) ?: return null
        val passwordHash = hash(password)
        return if (user.passwordHash == passwordHash) {
            return UserIdPrincipal(user.email)
        } else {
            null
        }
    }

    override suspend fun validate(jwt: JWTCredential): UserIdPrincipal? {
        val payload = jwt.payload
        val claim = payload.getClaim("id")
        val claimString = claim.asString()
        val userUuid = UUID.fromString(claimString) ?: return null
        val user = usersSource.getUser(userUuid) ?: return null
        return UserIdPrincipal(user.email)
    }

    override suspend fun validateSession(id: String): UserIdPrincipal? {
        val uuid = UUID.fromString(id) ?: return null
        val user = usersSource.getUser(uuid) ?: return null
        if (!user.isAuthorized) {
            return null
        }
        return UserIdPrincipal(user.email)
    }

    override suspend fun hash(password: String): String {
        val hmac = Mac.getInstance(algorithm)
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
