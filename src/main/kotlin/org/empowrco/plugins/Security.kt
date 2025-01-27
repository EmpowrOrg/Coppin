package org.empowrco.plugins

import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwt.JWT
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.OAuthAccessTokenResponse
import io.ktor.server.auth.OAuthServerSettings
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.authentication
import io.ktor.server.auth.bearer
import io.ktor.server.auth.jwt.*
import io.ktor.server.auth.oauth
import io.ktor.server.auth.principal
import io.ktor.server.auth.session
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import org.empowrco.coppin.models.UserAccessKey
import org.empowrco.coppin.utils.authenticator.Authenticator
import org.empowrco.coppin.utils.logs.logDebug
import org.empowrco.coppin.utils.routing.UserSession
import org.koin.ktor.ext.inject
import java.net.URL
import java.util.concurrent.TimeUnit

private val keycloakAddress = System.getenv("KEYCLOAK_ADDRESS")


private val keycloakOAuth = "keycloakOAuth"

fun Application.configureSecurity() {
    val authenticator: Authenticator by inject()
    val redirects = mutableMapOf<String, String>()
    authentication {
        // Authentication for all apps
        jwt("apps") {
            val jwkProvider = JwkProviderBuilder(
                URL("$keycloakAddress/realms/${System.getenv("KEYCLOAK_REALM")}/protocol/openid-connect/certs")
            )
                .cached(10, 24, TimeUnit.HOURS)
                .rateLimited(10, 1, TimeUnit.MINUTES)
                .build()

            val issuer = "$keycloakAddress/realms/${System.getenv("KEYCLOAK_REALM")}"

            verifier(jwkProvider, issuer) {
                acceptLeeway(3)
            }

            validate { credential ->
                // Optional: check audience or other claims:
                // e.g., does the 'aud' claim match one of the user’s client IDs?
                authenticator.validateApp(credential)
                JWTPrincipal(credential.payload)
            }
        }
        oauth(keycloakOAuth) {
            client = HttpClient()
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "keycloak",
                    authorizeUrl = "$keycloakAddress/realms/${System.getenv("KEYCLOAK_REALM")}/protocol/openid-connect/auth",
                    accessTokenUrl = "$keycloakAddress/realms/${System.getenv("KEYCLOAK_REALM")}/protocol/openid-connect/token",
                    clientId = System.getenv("KEYCLOAK_CLIENT_ID"),
                    clientSecret = System.getenv("KEYCLOAK_CLIENT_SECRET"),
                    accessTokenRequiresBasicAuth = false,
                    requestMethod = HttpMethod.Post, // must POST to token endpoint
                    defaultScopes = listOf("roles", "email", "profile", "acr"),
                    onStateCreated = { call, state ->
                        call.request.queryParameters["redirectUrl"]?.let {
                            redirects[state] = it
                        }
                    },
                )
            }
            urlProvider = {
                System.getenv("SERVER_CALLBACK_URL")
            }
        }
        bearer("api") {
            authenticate {
                authenticator.validateKey(it.token, UserAccessKey.Type.Api)
            }
        }
        bearer("application") {
            authenticate {
                authenticator.validateKey(it.token, UserAccessKey.Type.Application)
            }
        }
        session<UserSession>("auth-session") {
            validate {
                authenticator.validateSession(it.email)
            }
            challenge("/login")
        }
    }
    routing {
        authenticate(keycloakOAuth) {
            get("/callback") {
                val currentPrincipal: OAuthAccessTokenResponse.OAuth2? = call.principal()
                if (currentPrincipal?.state != null) {
                    currentPrincipal.state?.let { state ->
                        val jwtToken = currentPrincipal.accessToken
                        val token = JWT.decode(jwtToken)
                        val email = token.getClaim("email").asString()
                        val isAdmin =
                            token.getClaim("realm_access").asMap()["roles"].toString().trim('[', ']').split(',')
                                .map { it.trim() }.contains("admin")
                        call.sessions.set(
                            UserSession(
                                email = email,
                                state = state,
                                token = currentPrincipal.accessToken,
                                isAdmin = isAdmin,
                            )
                        )
                        redirects[state]?.let { redirect ->
                            logDebug("Login redirecting to $redirect")
                            call.respondRedirect(redirect)
                            return@get
                        }
                    }
                }
                call.respondRedirect("/courses")
            }
            get("/login") {

            }
        }

    }
}
