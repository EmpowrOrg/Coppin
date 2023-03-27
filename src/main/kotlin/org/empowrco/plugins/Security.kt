package org.empowrco.plugins

import io.ktor.server.application.Application
import io.ktor.server.auth.authentication
import io.ktor.server.auth.bearer
import io.ktor.server.auth.session
import org.empowrco.coppin.utils.authenticator.Authenticator
import org.empowrco.coppin.utils.routing.UserSession
import org.koin.ktor.ext.inject

fun Application.configureSecurity() {
    val authenticator: Authenticator by inject()

    authentication {
        bearer("key") {
            authenticate {
                authenticator.validateKey(it.token)
            }
        }
        session<UserSession>("auth-session") {
            validate {
                authenticator.validateSession(it.userId)
            }
            challenge("/login")
        }
    }
}
