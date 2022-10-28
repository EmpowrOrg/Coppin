package org.empowrco.coppin.assignment.api

import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.empowrco.coppin.assignment.presenters.AssignmentPresenter
import org.empowrco.coppin.utils.routing.authPost
import org.koin.ktor.ext.inject

fun Application.assignmentRouting() {
    val presenter: AssignmentPresenter by inject()
    routing {
        route("/assignment") {
            authPost("/submit") {
                presenter.submit(it.receive())
            }
            authPost("/request") {
                presenter.get(it.receive())
            }
        }
    }
}
