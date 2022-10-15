package org.empowrco.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.empowrco.coppin.assignment.api.assignmentRouting

fun Application.configureRouting() {
    assignmentRouting()
    routing {
        get("/health") {
            call.respond("Hello, World")
        }
    }
}
