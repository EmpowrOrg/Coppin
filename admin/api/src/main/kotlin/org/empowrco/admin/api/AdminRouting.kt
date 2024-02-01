package org.empowrco.admin.api

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import org.empowrco.coppin.admin.presenters.AdminOrgPresenter
import org.empowrco.coppin.admin.presenters.GetOrgSettingsRequest
import org.empowrco.coppin.admin.presenters.SaveOrgSettingsRequest
import org.empowrco.coppin.utils.routing.UserSession
import org.empowrco.coppin.utils.routing.errorRedirect
import org.empowrco.coppin.utils.routing.respondFreemarker
import org.koin.ktor.ext.get

fun Application.adminRouting() {
    routing {
        route("admin") {
            val presenter: AdminOrgPresenter = get()
            get {
                val email = call.sessions.get<UserSession>()?.email
                presenter.getOrgSettings(GetOrgSettingsRequest(email)).fold({
                    call.respondFreemarker("/admin/org.ftl", it, isAdminPanel = true)
                }, {
                    call.errorRedirect(it.localizedMessage, "/")
                })
            }
            post {
                val params = call.receiveParameters()
                presenter.updateOrgSettings(
                    SaveOrgSettingsRequest(
                        edxUsername = params["username"],
                        edxClientId = params["edx-client-id"],
                        edxClientSecret = params["edx-client-secret"],
                        doctorUrl = params["doctor-url"],
                        edxApiUrl = params["edx-api-url"],
                        userEmail = call.sessions.get<UserSession>()?.email.toString(),
                    )
                ).fold({
                    call.respondRedirect("/admin")
                }, {
                    call.errorRedirect(it.localizedMessage)
                })
            }
            aiRouting()
        }
    }

}
