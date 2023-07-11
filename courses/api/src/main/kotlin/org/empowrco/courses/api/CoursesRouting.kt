package org.empowrco.courses.api

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import org.empowrco.coppin.courses.presenters.CoursesPortalPresenter
import org.empowrco.coppin.courses.presenters.GetCourseRequest
import org.empowrco.coppin.courses.presenters.GetCoursesRequest
import org.empowrco.coppin.courses.presenters.LinkCoursesRequest
import org.empowrco.coppin.utils.routing.UserSession
import org.empowrco.coppin.utils.routing.errorRedirect
import org.empowrco.coppin.utils.routing.respondFreemarker
import org.koin.ktor.ext.inject

fun Application.coursesRouting() {
    routing {
        authenticate("auth-session") {
            val presenter: CoursesPortalPresenter by inject()
            route("courses") {
                get {
                    val userId = call.sessions.get<UserSession>()!!.userId
                    presenter.getCourses(GetCoursesRequest(userId)).fold({
                        call.respondFreemarker("courses.ftl", it)
                    }, {
                        call.errorRedirect(it)
                    })
                }

                route("link") {
                    get {
                        val userId = call.sessions.get<UserSession>()!!.userId
                        presenter.getUnlinkedCourses(GetCoursesRequest(userId)).fold({
                            call.respondFreemarker("unlinked-courses.ftl", it)
                        }, {
                            call.errorRedirect(it)
                        })
                    }
                    post {
                        val params = call.receiveParameters()
                        val userId = call.sessions.get<UserSession>()!!.userId
                        val classes = params.getAll("class") ?: emptyList()
                        presenter.linkCourses(LinkCoursesRequest(classIds = classes, userId = userId)).fold({
                            call.respondRedirect("/courses")
                        }, {
                            call.errorRedirect("/courses")
                        })
                    }
                }

                route("{uuid}") {
                    get {
                        presenter.getCourse(GetCourseRequest(id = call.parameters["uuid"].toString())).fold({
                            call.respondFreemarker("course.ftl", it)
                        }, {
                            call.errorRedirect(it, "/courses")
                        })
                    }
                }
            }
        }
    }
}
