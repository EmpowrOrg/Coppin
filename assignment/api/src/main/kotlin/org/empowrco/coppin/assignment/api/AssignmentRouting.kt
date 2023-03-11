package org.empowrco.coppin.assignment.api

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.freemarker.FreeMarkerContent
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.empowrco.coppin.assignment.presenters.AssignmentPortalPresenter
import org.empowrco.coppin.assignment.presenters.CreateAssignmentPortalRequest
import org.empowrco.coppin.assignment.presenters.SaveFeedbackRequest
import org.empowrco.coppin.assignment.presenters.UpdateAssignmentPortalRequest
import org.empowrco.coppin.assignment.presenters.UpdateCodePortalRequest
import org.koin.ktor.ext.inject


fun Application.assignmentRouting() {
    val presenter: AssignmentPortalPresenter by inject()
    routing {
        authenticate("auth-session") {
            route("assignments") {

                get {
                    val assignments = presenter.getAssignments()
                    call.respond(FreeMarkerContent("assignments.ftl", mapOf("assignments" to assignments)))
                }
                route("create") {
                    get {
                        call.respond(FreeMarkerContent("assignment-edit.ftl", null))
                    }
                    post {
                        val formParameters = call.receiveParameters()
                        val failureMessage = formParameters["failure-message"].toString()
                        val successMessage = formParameters["success-message"].toString()
                        val instructions = formParameters["instructions"].toString()
                        val referenceId = formParameters["reference-id"].toString()
                        val title = formParameters["title"].toString()
                        val totalAttempts = formParameters["total-attempts"].toString().toInt()
                        val gradingType = formParameters["grading-type"].toString()
                        val id = presenter.createAssignment(
                            CreateAssignmentPortalRequest(
                                referenceId = referenceId,
                                failureMessage = failureMessage,
                                successMessage = successMessage,
                                instructions = instructions,
                                title = title,
                                totalAttempts = totalAttempts,
                                gradingType = gradingType,
                            )
                        )
                        call.respondRedirect("/assignments/$id")
                    }
                }
                route("{uuid}") {
                    get {
                        val response = presenter.getAssignment(call.parameters["uuid"]!!)
                        call.respond(
                            FreeMarkerContent(
                                "assignment.ftl", mapOf(
                                    "assignment" to response.assignment,
                                    "codes" to response.codes,
                                    "feedbacks" to response.feedback,
                                )
                            )
                        )
                    }
                    post {
                        val formParameters = call.receiveParameters()
                        val failureMessage = formParameters["failure-message"].toString()
                        val successMessage = formParameters["success-message"].toString()
                        val instructions = formParameters["instructions"].toString()
                        val title = formParameters["title"].toString()
                        val totalAttempts = formParameters["total-attempts"].toString().toInt()
                        val gradingType = formParameters["grading-type"].toString()
                        presenter.updateAssignment(
                            UpdateAssignmentPortalRequest(
                                id = call.parameters["uuid"]!!,
                                failureMessage = failureMessage,
                                successMessage = successMessage,
                                instructions = instructions,
                                title = title,
                                totalAttempts = totalAttempts,
                                gradingType = gradingType
                            )
                        )
                        call.respondRedirect("/assignments")
                    }
                    post("delete") {
                        val assignmentId = call.parameters["uuid"].toString()
                        presenter.deleteAssignment(assignmentId)
                        call.respondRedirect("/assignments")
                    }
                    route("/feedback/{feedbackId?}") {
                        get {
                            val feedbackId = call.parameters["feedbackId"]
                            val uuid = call.parameters["uuid"].toString()
                            val feedback = presenter.getFeedback(feedbackId, uuid)
                            call.respond(
                                FreeMarkerContent(
                                    "assignment-feedback-edit.ftl",
                                    mapOf("feedback" to feedback)
                                )
                            )
                        }
                        post {
                            val uuid = call.parameters["uuid"].toString()
                            val feedbackId = call.parameters["feedbackId"]
                            val formParameters = call.receiveParameters()
                            val feedback = formParameters["feedback"].toString()
                            val regex = formParameters["regex"].toString()
                            val attempt = formParameters["attempt"].toString().toInt()
                            presenter.saveFeedback(
                                SaveFeedbackRequest(
                                    assignmentId = uuid,
                                    feedback = feedback,
                                    attempt = attempt,
                                    regex = regex,
                                    id = feedbackId,
                                )
                            )
                            call.respondRedirect("/assignments/$uuid")
                        }
                        post("delete") {
                            val uuid = call.parameters["uuid"].toString()
                            val feedbackId = call.parameters["feedbackId"].toString()
                            presenter.deleteFeedback(feedbackId)
                            call.respondRedirect("/assignments/$uuid")
                        }
                    }
                    route("/codes/{codeId?}") {
                        get {
                            val codeId = call.parameters["codeId"]
                            val uuid = call.parameters["uuid"].toString()
                            val code = presenter.getCode(codeId, uuid)
                            call.respond(FreeMarkerContent("assignment-code-edit.ftl", mapOf("code" to code)))
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
                            presenter.saveCode(
                                UpdateCodePortalRequest(
                                    languageMime = languageId,
                                    primary = primary,
                                    starterCode = starterCode,
                                    unitTest = unitTest,
                                    solutionCode = solutionCode,
                                    id = codeId,
                                    assignmentId = uuid,
                                )
                            )
                            call.respondRedirect("/assignments/$uuid")
                        }
                        post("delete") {
                            val uuid = call.parameters["uuid"].toString()
                            val codeId = call.parameters["codeId"].toString()
                            presenter.deleteCode(codeId)
                            call.respondRedirect("/assignments/$uuid")
                        }
                    }
                }
            }
        }
    }
}
