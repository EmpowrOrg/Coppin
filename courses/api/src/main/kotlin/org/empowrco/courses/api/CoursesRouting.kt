package org.empowrco.courses.api

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import org.empowrco.coppin.courses.presenters.CoursesPortalPresenter
import org.empowrco.coppin.courses.presenters.DeleteSubjectRequest
import org.empowrco.coppin.courses.presenters.GetCourseRequest
import org.empowrco.coppin.courses.presenters.GetCoursesRequest
import org.empowrco.coppin.courses.presenters.GetSubjectRequest
import org.empowrco.coppin.courses.presenters.LinkCoursesRequest
import org.empowrco.coppin.courses.presenters.UpdateSubjectRequest
import org.empowrco.coppin.utils.routing.Breadcrumbs
import org.empowrco.coppin.utils.routing.UserSession
import org.empowrco.coppin.utils.routing.error
import org.empowrco.coppin.utils.routing.errorRedirect
import org.empowrco.coppin.utils.routing.respondFreemarker
import org.koin.ktor.ext.inject

fun Application.coursesRouting() {
    routing {
        authenticate("auth-session") {
            val presenter: CoursesPortalPresenter by inject()
            route("courses") {
                get {
                    val email = call.sessions.get<UserSession>()!!.email
                    presenter.getCourses(GetCoursesRequest(email)).fold({
                        call.respondFreemarker("courses.ftl", it)
                    }, {
                        call.errorRedirect(it)
                    })
                }

                route("link") {
                    get {
                        val email = call.sessions.get<UserSession>()!!.email
                        presenter.getManageCourses(GetCoursesRequest(email)).fold({
                            call.respondFreemarker(
                                "manage-courses.ftl", it,
                                Breadcrumbs(
                                    crumbs = listOf(
                                        Breadcrumbs.Crumb("school", "Courses", "/courses"),
                                        Breadcrumbs.Crumb(null, "Manage Courses", null),
                                    )
                                ),
                            )
                        }, {
                            call.errorRedirect(it)
                        })
                    }
                    post {
                        val params = call.receiveParameters()
                        val email = call.sessions.get<UserSession>()!!.email
                        val classes = params.getAll("class") ?: emptyList()
                        presenter.linkCourses(LinkCoursesRequest(classIds = classes, email = email)).fold({
                            call.respondRedirect("/courses")
                        }, {
                            call.errorRedirect("/courses")
                        })
                    }
                }

                route("{uuid}") {
                    get {
                        presenter.getCourse(GetCourseRequest(id = call.parameters["uuid"].toString())).fold({
                            call.respondFreemarker(
                                "course.ftl", it, Breadcrumbs(
                                    crumbs = listOf(
                                        Breadcrumbs.Crumb("school", "Courses", "/courses"),
                                        Breadcrumbs.Crumb(null, it.name, null),
                                    )
                                )
                            )
                        }, {
                            call.errorRedirect(it, "/courses")
                        })
                    }

                    route("subjects") {
                        post {
                            presenter.createSubject(call.receive()).fold({
                                call.respond(it)
                            }, {
                                call.error(it)
                            })
                        }
                        route("{subjectId?}") {
                            get {
                                val subjectId = call.parameters["subjectId"]
                                val courseId = call.parameters["uuid"].toString()
                                presenter.getSubject(GetSubjectRequest(id = subjectId, courseId = courseId)).fold({
                                    call.respondFreemarker(
                                        "subject.ftl",
                                        it,
                                        Breadcrumbs(
                                            crumbs = listOf(
                                                Breadcrumbs.Crumb("school", "Courses", "/courses"),
                                                Breadcrumbs.Crumb(null, it.courseName, "/courses/$courseId"),
                                            )
                                        ),
                                    )
                                }, {
                                    call.errorRedirect(it, "/courses/$courseId")
                                })
                            }

                            post {
                                val name = call.receiveParameters()["name"].toString()
                                val courseId = call.parameters["uuid"].toString()
                                presenter.updateSubject(
                                    UpdateSubjectRequest(
                                        id = call.parameters["subjectId"].toString(),
                                        name = name,
                                    )
                                ).fold({
                                    call.respondRedirect("/courses/$courseId")
                                }, {
                                    call.errorRedirect(it)
                                })
                            }

                            delete {
                                presenter.deleteSubject(
                                    DeleteSubjectRequest(call.parameters["subjectId"].toString())
                                ).fold({
                                    call.respond(it)
                                }, {
                                    call.error(it)
                                })
                            }
                        }
                    }
                }

            }
        }
    }
}
