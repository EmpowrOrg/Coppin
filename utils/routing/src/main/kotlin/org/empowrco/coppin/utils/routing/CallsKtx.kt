package org.empowrco.coppin.utils.routing

import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.path
import io.ktor.server.application.ApplicationCall
import io.ktor.server.freemarker.FreeMarkerContent
import io.ktor.server.plugins.origin
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.util.url
import org.empowrco.coppin.models.User

suspend fun ApplicationCall.respondFreemarker(template: String, content: Any) {
    respondFreemarker(template, mapOf("content" to content))
}


suspend fun ApplicationCall.respondFreemarker(template: String, content: Map<String, Any?> = mapOf()) {
    val userType = sessions.get<UserSession>()?.type
    val isAdmin = userType == User.Type.Admin
    val updatedContent = content.toMutableMap()
    updatedContent["isAdmin"] = isAdmin
    val error = request.queryParameters["error"]
    if (error == null) {
        respond(FreeMarkerContent(template, updatedContent))
        return
    }
    updatedContent["error"] = error.replace('-', ' ')
    respond(FreeMarkerContent(template, updatedContent))
}

suspend fun ApplicationCall.errorRedirect(error: String, path: String? = null) {
    val errorUrl = path?.let {
        val origin = request.origin
        val builder = URLBuilder()
        builder.protocol = URLProtocol.byName[origin.scheme] ?: URLProtocol(origin.scheme, 0)
        builder.host = origin.serverHost
        builder.port = origin.serverPort
        builder.path(it)
        builder.parameters.appendAll(request.queryParameters)
        builder.parameters.append("error", error)
        builder.buildString()
    } ?: url {
        parameters.append("error", error)
    }
    respondRedirect(errorUrl)
}

data class UserSession(val userId: String, val type: User.Type)
