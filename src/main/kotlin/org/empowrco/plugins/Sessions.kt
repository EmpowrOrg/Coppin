package org.empowrco.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import io.ktor.server.sessions.maxAge
import org.empowrco.coppin.utils.routing.UserSession
import kotlin.time.Duration.Companion.days

fun Application.configureSessions() {
    install(Sessions) {
        cookie<UserSession>("user_session") {
            cookie.maxAge = 7.days
        }
    }
}
