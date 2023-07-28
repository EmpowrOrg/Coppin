package org.empowrco.coppin.assignment.api

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.empowrco.coppin.assignment.presenters.ArchiveAssignmentRequest
import org.empowrco.coppin.assignment.presenters.AssignmentPortalPresenter
import org.empowrco.coppin.assignment.presenters.CreateAssignmentPortalRequest
import org.empowrco.coppin.assignment.presenters.DeleteCodeRequest
import org.empowrco.coppin.assignment.presenters.GetAssignmentRequest
import org.empowrco.coppin.assignment.presenters.GetCodeRequest
import org.empowrco.coppin.assignment.presenters.GetSubmissionRequest
import org.empowrco.coppin.assignment.presenters.UpdateAssignmentPortalRequest
import org.empowrco.coppin.assignment.presenters.UpdateCodePortalRequest
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
                            val failureMessage = formParameters["failure-message"].toString()
                            val successMessage = formParameters["success-message"].toString()
                            val instructions = formParameters["instructions"].toString()
                            val referenceId = formParameters["reference-id"].toString()
                            val title = formParameters["title"].toString()
                            val totalAttempts = formParameters["total-attempts"].toString()
                            val subjectId = formParameters["subject"].toString()
                            presenter.createAssignment(
                                CreateAssignmentPortalRequest(
                                    referenceId = referenceId,
                                    failureMessage = failureMessage,
                                    successMessage = successMessage,
                                    instructions = instructions,
                                    title = title,
                                    courseId = call.parameters["courseId"].toString(),
                                    totalAttempts = totalAttempts,
                                    subjectId = subjectId,
                                )
                            ).fold({
                                call.respondRedirect("/courses/${call.parameters["courseId"]}/assignments/${it.id}")
                            }, {
                                call.errorRedirect(it, "/courses/${call.parameters["courseId"]}/assignments/")
                            })
                        }
                    }
                    route("{uuid?}") {
                        get {
                            presenter.getAssignment(
                                GetAssignmentRequest(
                                    id = call.parameters["uuid"],
                                    courseId = call.parameters["courseId"].toString(),
                                )
                            ).fold({
                                call.respondFreemarker("assignment.ftl", it)
                            }, {
                                call.errorRedirect(it, "/courses/${call.parameters["courseId"]}")
                            })
                        }
                        post {
                            val formParameters = call.receiveParameters()
                            val failureMessage = formParameters["failure-message"].toString()
                            val successMessage = formParameters["success-message"].toString()
                            val instructions = formParameters["instructions"].toString()
                            val title = formParameters["title"].toString()
                            val totalAttempts = formParameters["total-attempts"].toString().toInt()
                            val referenceId = formParameters["reference-id"].toString()
                            val subject = formParameters["subject"].toString()
                            presenter.updateAssignment(
                                UpdateAssignmentPortalRequest(
                                    id = call.parameters["uuid"],
                                    failureMessage = failureMessage,
                                    successMessage = successMessage,
                                    instructions = instructions,
                                    title = title,
                                    totalAttempts = totalAttempts,
                                    referenceId = referenceId,
                                    subject = subject,
                                )
                            ).fold({
                                call.respondRedirect("/courses/${it.courseId}")
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
                                    call.respondFreemarker("assignment-code.ftl", it)
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
                                val solutionCode = formParameters["solution-code"]
                                val unitTest = formParameters["unit-test-code"]
                                val injectable = formParameters["injectable"].toString()
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
                                    call.respondFreemarker("submission.ftl", it)
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
