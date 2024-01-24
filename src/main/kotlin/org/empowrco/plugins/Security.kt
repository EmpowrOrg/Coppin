package org.empowrco.plugins

import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.auth.OAuthServerSettings
import io.ktor.server.auth.authentication
import io.ktor.server.auth.bearer
import io.ktor.server.auth.oauth
import io.ktor.server.auth.session
import org.empowrco.coppin.utils.authenticator.Authenticator
import org.empowrco.coppin.utils.routing.UserSession
import org.koin.ktor.ext.inject

private val keycloakAddress = System.getenv("KEYCLOAK_ADDRESS")

private val keycloakProvider = OAuthServerSettings.OAuth2ServerSettings(
    name = "keycloak",
    authorizeUrl = "$keycloakAddress/realms/${System.getenv("KEYCLOAK_REALM")}/protocol/openid-connect/auth",
    accessTokenUrl = "$keycloakAddress/realms/${System.getenv("KEYCLOAK_REALM")}/protocol/openid-connect/token",
    clientId = System.getenv("KEYCLOAK_CLIENT_ID"),
    clientSecret = System.getenv("KEYCLOAK_CLIENT_SECRET"),
    accessTokenRequiresBasicAuth = false,
    requestMethod = HttpMethod.Post, // must POST to token endpoint
    defaultScopes = listOf("roles", "email", "profile", "acr")
)
private val keycloakOAuth = "keycloakOAuth"

fun Application.configureSecurity() {
    val authenticator: Authenticator by inject()

    authentication {
        oauth(keycloakOAuth) {
            client = HttpClient()
            providerLookup = { keycloakProvider }
            urlProvider = {
                "http://localhost:3000/"
            }
        }
        bearer("key") {
            authenticate {
                authenticator.validateKey(it.token)
            }
        }
        session<UserSession>("auth-session") {
            validate {
                authenticator.validateSession(it.email)
            }
            challenge("/login")
        }
    }
}
