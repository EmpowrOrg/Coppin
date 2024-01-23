package org.empowrco.admin.api

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import org.empowrco.coppin.admin.presenters.AdminSecurityPresenter
import org.empowrco.coppin.admin.presenters.GetSecuritySettingsRequest
import org.empowrco.coppin.utils.routing.UserSession
import org.empowrco.coppin.utils.routing.errorRedirect
import org.empowrco.coppin.utils.routing.respondFreemarker
import org.koin.ktor.ext.get

internal fun Application.securityRouting() {
    routing {
        val presenter: AdminSecurityPresenter = get()
        route("security") {
            get {
                val userId = call.sessions.get<UserSession>()?.userId
                presenter.getSecuritySettings(GetSecuritySettingsRequest(userId)).fold({
                    call.respondFreemarker("users.ftl", it)
                }, {
                    call.errorRedirect(it.localizedMessage, "/")
                })
            }
        }
    }
}
