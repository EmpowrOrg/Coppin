package org.empowrco.coppin.assignment.api

import io.ktor.http.HttpStatusCode
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
import org.empowrco.coppin.assignment.presenters.ArchiveAssignmentRequest
import org.empowrco.coppin.assignment.presenters.AssignmentPortalPresenter
import org.empowrco.coppin.assignment.presenters.CreateAssignmentPortalRequest
import org.empowrco.coppin.assignment.presenters.DeleteCodeRequest
import org.empowrco.coppin.assignment.presenters.GenerateAssignmentRequest
import org.empowrco.coppin.assignment.presenters.GetAssignmentRequest
import org.empowrco.coppin.assignment.presenters.GetCodeRequest
import org.empowrco.coppin.assignment.presenters.GetSubmissionRequest
import org.empowrco.coppin.assignment.presenters.UpdateAssignmentPortalRequest
import org.empowrco.coppin.assignment.presenters.UpdateCodePortalRequest
import org.empowrco.coppin.utils.routing.Breadcrumbs
import org.empowrco.coppin.utils.routing.UserSession
import org.empowrco.coppin.utils.routing.error
import org.empowrco.coppin.utils.routing.errorRedirect
import org.empowrco.coppin.utils.routing.respondFreemarker
import org.koin.ktor.ext.inject


fun Application.assignmentRouting() {
    val presenter: AssignmentPortalPresenter by inject()
    routing {
        authenticate("auth-session") {
            route("courses/{courseId}") {
                route("assignments") {
                    route("create") {
                        post {
                            val formParameters = call.receiveParameters()
                            val failureMessage = formParameters["failure"].toString()
                            val successMessage = formParameters["success"].toString()
                            val instructions = formParameters["instructions"].toString()
                            val title = formParameters["title"].toString()
                            val totalAttempts = formParameters["total-attempts"].toString()
                            val subjectId = formParameters["subject"].toString()
                            val points = formParameters["points"].toString()
                            presenter.createAssignment(
                                CreateAssignmentPortalRequest(
                                    failureMessage = failureMessage,
                                    successMessage = successMessage,
                                    instructions = instructions,
                                    title = title,
                                    courseId = call.parameters["courseId"].toString(),
                                    totalAttempts = totalAttempts,
                                    subjectId = subjectId,
                                    points = points,
                                )
                            ).fold({
                                call.respondRedirect("/courses/${call.parameters["courseId"]}/assignments/${it.id}")
                            }, {
                                call.errorRedirect(it, "/courses/${call.parameters["courseId"]}/assignments/")
                            })
                        }
                    }
                    route("generate") {
                        post {
                            presenter.generateAssignment(call.receive<GenerateAssignmentRequest>()).fold({
                                call.respond(it)
                            }, {
                                call.error(it)
                            })
                        }
                    }
                    route("{uuid?}") {
                        get {
                            val email = call.sessions.get<UserSession>()!!.email
                            presenter.getAssignment(
                                GetAssignmentRequest(
                                    id = call.parameters["uuid"],
                                    courseId = call.parameters["courseId"].toString(),
                                    email = email,
                                )
                            ).fold({
                                call.respondFreemarker(
                                    "assignment.ftl",
                                    it,
                                    Breadcrumbs(
                                        crumbs = listOf(
                                            Breadcrumbs.Crumb("school", "Courses", "/courses"),
                                            Breadcrumbs.Crumb(null, it.courseName, "/courses/${it.courseId}"),
                                            Breadcrumbs.Crumb(null, it.title ?: "New Assignment", null),
                                        )
                                    ),
                                )
                            }, {
                                call.errorRedirect(it, "/courses/${call.parameters["courseId"]}")
                            })
                        }
                        post {
                            val formParameters = call.receiveParameters()
                            val assignmentId = call.parameters["uuid"]
                            val failureMessage = formParameters["failure"].toString()
                            val successMessage = formParameters["success"].toString()
                            val instructions = formParameters["instructions"].toString()
                            val title = formParameters["title"].toString()
                            val totalAttempts = formParameters["total-attempts"].toString().toInt()
                            val subject = formParameters["subject"].toString()
                            val points = formParameters["points"].toString().toInt()
                            presenter.updateAssignment(
                                UpdateAssignmentPortalRequest(
                                    id = assignmentId,
                                    failureMessage = failureMessage,
                                    successMessage = successMessage,
                                    instructions = instructions,
                                    title = title,
                                    totalAttempts = totalAttempts,
                                    subject = subject,
                                    points = points,
                                )
                            ).fold({
                                call.respondRedirect("/courses/${it.courseId}/assignments/$assignmentId")
                            }, {
                                call.errorRedirect(it)
                            })

                        }
                        delete {
                            val uuid = call.parameters["uuid"].toString()
                            presenter.archiveAssignment(ArchiveAssignmentRequest(uuid)).fold({
                                call.respond(it)
                            }, {
                                call.respond(HttpStatusCode.InternalServerError, it.localizedMessage)
                            })
                        }
                        route("/codes/{codeId?}") {
                            get {
                                val codeId = call.parameters["codeId"]
                                val assignmentId = call.parameters["uuid"].toString()
                                presenter.getCode(
                                    GetCodeRequest(
                                        id = codeId,
                                        assignmentId = assignmentId
                                    )
                                ).fold({
                                    call.respondFreemarker(
                                        "assignment-code.ftl",
                                        it,
                                        Breadcrumbs(
                                            crumbs = listOf(
                                                Breadcrumbs.Crumb("school", "Courses", "/courses"),
                                                Breadcrumbs.Crumb(null, it.courseName, "/courses/${it.courseId}"),
                                                Breadcrumbs.Crumb(
                                                    null,
                                                    it.assignmentName,
                                                    "/courses/${it.courseId}/assignments/${it.assignmentId}",
                                                ),
                                                Breadcrumbs.Crumb(null, "Code", null),
                                            )
                                        ),
                                    )
                                }, {
                                    call.errorRedirect(it)
                                })
                            }
                            post {
                                val uuid = call.parameters["uuid"].toString()
                                val codeId = call.parameters["codeId"]
                                val formParameters = call.receiveParameters()
                                val languageId = formParameters["language"].toString()
                                val primary = formParameters["primary"].toString()
                                val starterCode = formParameters["starter-code"]
                                val solutionCode = formParameters["solution-code"].toString()
                                val unitTest = formParameters["unit-test-code"]
                                val injectable = formParameters["injectable"].toString()
                                val solutionVisibility = formParameters["solution-visibility"].toString()
                                presenter.updateCode(
                                    UpdateCodePortalRequest(
                                        languageMime = languageId,
                                        primary = primary,
                                        starterCode = starterCode,
                                        unitTest = unitTest,
                                        solutionCode = solutionCode,
                                        id = codeId,
                                        injectable = injectable,
                                        assignmentId = uuid,
                                        solutionVisibility = solutionVisibility,
                                    )
                                ).fold({
                                    call.respondRedirect("/courses/${it.courseId}/assignments/$uuid")
                                }, {
                                    call.errorRedirect(it)
                                })
                            }
                            delete {
                                val codeId = call.parameters["codeId"].toString()
                                presenter.deleteCode(
                                    DeleteCodeRequest(
                                        id = codeId
                                    )
                                ).fold({
                                    call.respond(it)
                                }, {
                                    call.respond(HttpStatusCode.InternalServerError, it.localizedMessage)
                                })

                            }
                        }
                        route("/submissions/{studentId}") {
                            get {
                                presenter.getSubmission(
                                    GetSubmissionRequest(
                                        assignmentId = call.parameters["uuid"].toString(),
                                        studentId = call.parameters["studentId"].toString(),
                                    )
                                ).fold({
                                    call.respondFreemarker(
                                        "submission.ftl", it,
                                        Breadcrumbs(
                                            crumbs = listOf(
                                                Breadcrumbs.Crumb("school", "Courses", "/courses"),
                                                Breadcrumbs.Crumb(null, it.courseName, "/courses/${it.courseId}"),
                                                Breadcrumbs.Crumb(
                                                    null,
                                                    it.assignment,
                                                    "/courses/${it.courseId}/assignments/${it.assignmentId}",
                                                ),
                                                Breadcrumbs.Crumb(null, call.parameters["studentId"].toString(), null),
                                            )
                                        ),
                                    )
                                }, {
                                    call.errorRedirect(
                                        it,
                                        "/courses/${call.parameters["courseId"]}/assignments/${
                                            call
                                                .parameters["uuid"]
                                        }",
                                    )
                                })
                            }
                        }
                    }
                }
            }
        }
    }
}
