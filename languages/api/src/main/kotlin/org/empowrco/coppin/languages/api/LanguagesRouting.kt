package org.empowrco.coppin.languages.api

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.empowrco.coppin.languages.presenters.DeleteLanguageRequest
import org.empowrco.coppin.languages.presenters.GetLanguageRequest
import org.empowrco.coppin.languages.presenters.LanguagesPresenter
import org.empowrco.coppin.languages.presenters.UpsertLanguageRequest
import org.empowrco.coppin.utils.routing.errorRedirect
import org.empowrco.coppin.utils.routing.respondFreemarker
import org.koin.ktor.ext.inject

fun Application.languagesRouting() {
    val presenter: LanguagesPresenter by inject()
    routing {
        authenticate("auth-session") {
            route("languages") {
                get {
                    presenter.getLanguages().fold({
                        call.respondFreemarker("languages.ftl", mapOf("languages" to it.languages))
                    }, {
                        call.errorRedirect(it)
                    })
                }

                route("{uuid}") {
                    get {
                        val uuid = call.parameters["uuid"].toString()
                        presenter.getLanguage(GetLanguageRequest(uuid)).fold({
                            call.respondFreemarker("language-edit.ftl", mapOf("language" to it))
                        }, {
                            call.errorRedirect(it)
                        })
                    }

                    post {
                        val uuid = call.parameters["uuid"].toString()
                        val formParameters = call.receiveParameters()
                        val name = formParameters["name"].toString()
                        val mime = formParameters["mime"].toString()
                        val url = formParameters["url"].toString()
                        presenter.upsertLanguage(
                            UpsertLanguageRequest(
                                name = name,
                                mime = mime,
                                url = url,
                                id = uuid,
                            )
                        )
                        call.respondRedirect("/languages")
                    }
                    post("delete") {
                        val uuid = call.parameters["uuid"].toString()
                        presenter.deleteLanguage(DeleteLanguageRequest(uuid)).fold({
                            call.respondRedirect("languages")
                        }, {
                            call.errorRedirect(it)
                        })
                    }
                }
            }
        }
    }
}
