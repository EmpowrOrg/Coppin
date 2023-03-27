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
                        call.respondFreemarker("languages.ftl", it)
                    }, {
                        call.errorRedirect(it)
                    })
                }

                route("{uuid?}") {
                    get {
                        val uuid = call.parameters["uuid"]
                        presenter.getLanguage(GetLanguageRequest(uuid)).fold({
                            call.respondFreemarker("language.ftl", it)
                        }, {
                            call.errorRedirect(it)
                        })
                    }

                    post {
                        val uuid = call.parameters["uuid"]
                        val formParameters = call.receiveParameters()
                        val name = formParameters["name"].toString()
                        val mime = formParameters["mime"].toString()
                        val url = formParameters["url"].toString()
                        val unitTestRegex = formParameters["unitTestRegex"].toString()
                        presenter.upsertLanguage(
                            UpsertLanguageRequest(
                                name = name,
                                mime = mime,
                                url = url,
                                id = uuid,
                                unitTestRegex = unitTestRegex,
                            )
                        ).fold({
                            call.respondRedirect("/languages")
                        }, {
                            call.errorRedirect(it)
                        })

                    }
                }
            }
        }
    }
}
