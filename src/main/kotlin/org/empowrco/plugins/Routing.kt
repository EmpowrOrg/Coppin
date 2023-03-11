package org.empowrco.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.http.content.resources
import io.ktor.server.http.content.static
import io.ktor.server.http.content.staticBasePackage
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.empowrco.coppin.assignment.api.assignmentPortal
import org.empowrco.coppin.assignment.api.assignmentRouting
import org.empowrco.coppin.languages.api.languagesRouting

fun Application.configureRouting() {
    assignmentRouting()
    assignmentPortal()
    languagesRouting()
    routing {
        authenticate("auth-session") {
            get("/") {
                call.respondRedirect("/assignments")
            }
        }

        get("/health") {
            call.respond("Hello, World")
        }
        static("/") {
            staticBasePackage = "files"
            resources(".")
        }
    }
}
