package org.empowrco.courses.api

import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import org.empowrco.coppin.courses.presenters.api.CoursesApiPresenter
import org.koin.ktor.ext.inject

fun Route.coursesApi() {
    route("courses") {
        val presenter by inject<CoursesApiPresenter>()
        get {
            val response = presenter.getGrades(call.receive())
            call.respond(response)
        }
    }
}
