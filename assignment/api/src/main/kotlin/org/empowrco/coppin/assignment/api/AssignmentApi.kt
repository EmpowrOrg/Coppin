package org.empowrco.coppin.assignment.api

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.empowrco.coppin.assignment.presenters.AssignmentPresenter
import org.koin.ktor.ext.inject

fun Application.assignmentApi() {
    val presenter: AssignmentPresenter by inject()
    routing {
        route("/assignment") {
            post("/submit") {
                call.respond(presenter.submit(call.receive()))
            }
            post("/request") {
                call.respond(presenter.get(call.receive()))
            }
        }
    }
}
