package org.empowrco.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.sessions.SessionTransportTransformerEncrypt
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import io.ktor.server.sessions.maxAge
import io.ktor.util.hex
import org.empowrco.coppin.utils.routing.UserSession
import kotlin.time.Duration.Companion.days

fun Application.configureSessions() {
    install(Sessions) {
        val secretEncryptKey = hex(System.getenv("SESSION_ENCRYPT_KEY"))
        val secretSignKey = hex(System.getenv("SESSION_SIGN_KEY"))
        cookie<UserSession>("user_session") {
            cookie.path = "/"
            cookie.maxAge = 7.days
            transform(SessionTransportTransformerEncrypt(secretEncryptKey, secretSignKey))
        }
    }
}
