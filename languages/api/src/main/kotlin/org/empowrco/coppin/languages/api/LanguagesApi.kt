package org.empowrco.coppin.languages.api

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.routing
import org.empowrco.coppin.languages.presenters.DeleteLanguageRequest
import org.empowrco.coppin.languages.presenters.LanguagesApiPresenter
import org.koin.ktor.ext.inject

fun Application.languagesApi() {
    val presenter: LanguagesApiPresenter by inject()
    routing {
        authenticate("auth-session") {
            delete("languages/{uuid}") {
                val uuid = call.parameters["uuid"].toString()
                val response = presenter.deleteLanguage(DeleteLanguageRequest(uuid))
                call.respond(response)
            }
        }
    }
}
