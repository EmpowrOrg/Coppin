package org.empowrco.courses.api

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.empowrco.coppin.courses.presenters.CoursesPortalPresenter
import org.empowrco.coppin.courses.presenters.GetCourseRequest
import org.empowrco.coppin.utils.routing.errorRedirect
import org.empowrco.coppin.utils.routing.respondFreemarker
import org.koin.ktor.ext.inject

fun Application.coursesRouting() {
    routing {
        authenticate("auth-session") {
            val presenter: CoursesPortalPresenter by inject()
            route("courses") {
                get {
                    presenter.getCourses().fold({
                        call.respondFreemarker("courses.ftl", it)
                    }, {
                        call.errorRedirect(it)
                    })
                }

                route("{uuid}") {
                    get {
                        presenter.getCourse(GetCourseRequest(id = call.parameters["uuid"].toString())).fold({
                            call.respondFreemarker("course.ftl", it)
                        }, {
                            call.errorRedirect(it, "courses")
                        })
                    }
                }
            }
        }
    }
}
