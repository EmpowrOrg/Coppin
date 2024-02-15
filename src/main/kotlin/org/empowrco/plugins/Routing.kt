package org.empowrco.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.http.content.resources
import io.ktor.server.http.content.static
import io.ktor.server.http.content.staticBasePackage
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.empowrco.admin.api.adminRouting
import org.empowrco.coppin.assignment.api.assignmentApi
import org.empowrco.coppin.assignment.api.assignmentRouting
import org.empowrco.coppin.languages.api.languagesApi
import org.empowrco.coppin.languages.api.languagesRouting
import org.empowrco.coppin.users.api.usersRouting
import org.empowrco.courses.api.coursesApi
import org.empowrco.courses.api.coursesRouting

fun Application.configureRouting() {
    assignmentApi()
    assignmentRouting()
    coursesRouting()
    usersRouting()
    languagesApi()
    languagesRouting()
    adminRouting()
    routing {
        route("api") {
            coursesApi()
        }
        authenticate("keycloakOAuth") {
            get("/") {
                call.respondRedirect("/courses")
            }
        }

        get("/health") {
            call.respond(HttpStatusCode.OK)
        }
        static("/") {
            staticBasePackage = "files"
            resources(".")
        }
    }
}
