package org.empowrco.coppin.languages.api

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.freemarker.FreeMarkerContent
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.empowrco.coppin.languages.presenters.CreateLanguageRequest
import org.empowrco.coppin.languages.presenters.LanguagesPresenter
import org.empowrco.coppin.languages.presenters.UpdateLanguageRequest
import org.koin.ktor.ext.inject

fun Application.languagesRouting() {
    val presenter: LanguagesPresenter by inject()
    routing {
        route("languages") {
            get {
                val response = presenter.getLanguages()
                call.respond(FreeMarkerContent("languages.ftl", mapOf("languages" to response.languages)))
            }

            route("create") {

                get {
                    val language = presenter.getLanguage(null)
                    call.respond(FreeMarkerContent("language-edit.ftl", mapOf("language" to language)))
                }

                post {
                    val formParameters = call.receiveParameters()
                    val name = formParameters["name"].toString()
                    val mime = formParameters["mime"].toString()
                    val url = formParameters["url"].toString()
                    presenter.saveLanguage(
                        CreateLanguageRequest(
                            name = name,
                            mime = mime,
                            url = url,
                        )
                    )
                    call.respondRedirect("/languages")
                }
            }

            route("{uuid}") {
                get {
                    val uuid = call.parameters["uuid"].toString()
                    val language = presenter.getLanguage(uuid)
                    call.respond(FreeMarkerContent("language-edit.ftl", mapOf("language" to language)))
                }

                post {
                    val uuid = call.parameters["uuid"].toString()
                    val formParameters = call.receiveParameters()
                    val name = formParameters["name"].toString()
                    val mime = formParameters["mime"].toString()
                    val url = formParameters["url"].toString()
                    presenter.updateLanguage(
                        UpdateLanguageRequest(
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
                    presenter.deleteLanguage(uuid)
                }
            }
        }
    }
}
