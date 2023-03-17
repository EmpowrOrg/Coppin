package org.empowrco.coppin.assignment.api

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.empowrco.coppin.assignment.presenters.AssignmentApiPresenter
import org.empowrco.coppin.assignment.presenters.RequestApi.DeleteAssignmentRequest
import org.koin.ktor.ext.inject

fun Application.assignmentApi() {
    val presenter: AssignmentApiPresenter by inject()
    routing {
        route("/assignment") {
            post("/submit") {
                call.respond(presenter.submit(call.receive()))
            }
            post("/request") {
                call.respond(presenter.get(call.receive()))
            }
            post("/run") {
                call.respond(presenter.run(call.receive()))
            }
            authenticate("auth-session") {
                delete("{uuid}") {
                    val request = DeleteAssignmentRequest(
                        id = call.parameters["uuid"].toString()
                    )
                    call.respond(presenter.deleteAssignment(request))
                }
            }

        }
    }
}
