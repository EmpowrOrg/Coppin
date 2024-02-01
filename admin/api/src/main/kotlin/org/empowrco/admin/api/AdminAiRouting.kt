package org.empowrco.admin.api

import io.ktor.server.application.call
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import org.empowrco.coppin.admin.presenters.AdminAiPresenter
import org.empowrco.coppin.admin.presenters.GetAiSettingsRequest
import org.empowrco.coppin.admin.presenters.SaveAiSettingsRequest
import org.empowrco.coppin.utils.routing.UserSession
import org.empowrco.coppin.utils.routing.errorRedirect
import org.empowrco.coppin.utils.routing.respondFreemarker
import org.koin.ktor.ext.get

internal fun Route.aiRouting() {
    val presenter: AdminAiPresenter = get()
    route("ai") {
        get {
            val email = call.sessions.get<UserSession>()?.email
            presenter.getAiSettings(GetAiSettingsRequest(email)).fold({
                call.respondFreemarker("/admin/ai.ftl", it, isAdminPanel = true)
            }, {
                call.errorRedirect(it.localizedMessage, "/")
            })
        }

        post {
            val params = call.receiveParameters()
            presenter.updateAiSettings(
                SaveAiSettingsRequest(
                    model = params["ai-model"],
                    orgKey = params["org-key"],
                    key = params["key"],
                    prePrompt = params["pre-prompt"],
                    userEmail = call.sessions.get<UserSession>()?.email.toString(),
                )
            ).fold({
                call.respondRedirect("/admin/ai")
            }, {
                call.errorRedirect(it.localizedMessage)
            })
        }
    }

}
