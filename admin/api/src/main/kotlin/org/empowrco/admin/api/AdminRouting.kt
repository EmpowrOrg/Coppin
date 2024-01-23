package org.empowrco.admin.api

import io.ktor.server.application.Application
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.adminRouting() {
    routing {
        route("admin") {
            securityRouting()
        }
    }

}
