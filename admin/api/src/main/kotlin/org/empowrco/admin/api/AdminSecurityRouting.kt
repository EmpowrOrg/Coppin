package org.empowrco.admin.api

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import org.empowrco.coppin.admin.presenters.AdminSecurityPresenter
import org.empowrco.coppin.admin.presenters.GetSecuritySettingsRequest
import org.empowrco.coppin.admin.presenters.SaveSecuritySettingsRequest
import org.empowrco.coppin.utils.routing.UserSession
import org.empowrco.coppin.utils.routing.errorRedirect
import org.empowrco.coppin.utils.routing.respondFreemarker
import org.koin.ktor.ext.get

internal fun Route.securityRouting() {

    val presenter: AdminSecurityPresenter = get()
    route("security") {
        get {
            val userId = call.sessions.get<UserSession>()?.userId
            presenter.getSecuritySettings(GetSecuritySettingsRequest(userId)).fold({
                call.respondFreemarker("/admin/security.ftl", it)
            }, {
                call.errorRedirect(it.localizedMessage, "/")
            })
        }

        post {
            val request = call.receive<SaveSecuritySettingsRequest>()
            presenter.updateSecuritySettings(request).fold({
                call.respond(HttpStatusCode.OK, it)
            }, {
                call.errorRedirect(it.localizedMessage)
            })
        }
    }

}
