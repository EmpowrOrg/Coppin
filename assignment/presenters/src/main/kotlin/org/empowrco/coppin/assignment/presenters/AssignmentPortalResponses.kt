package org.empowrco.coppin.assignment.presenters

import org.empowrco.coppin.models.portal.CodeListItem
import org.empowrco.coppin.models.portal.FeedbackListItem

data class GetAssignmentPortalResponse(
    val id: String?,
    val referenceId: String?,
    val instructions: String?,
    val successMessage: String?,
    val failureMessage: String?,
    val attempts: Int?,
    val title: String?,
    val codes: List<CodeListItem>,
    val feedback: List<FeedbackListItem>,
) {}

data class GetAssignmentsResponse(val assignments: List<AssignmentListItem>) {
    data class AssignmentListItem(val referenceId: String, val id: String, val title: String, val createdAt: String)
}

object UpdateAssignmentResponse

data class FeedbackResponse(
    val feedback: String,
    val id: String,
    val attempt: Int,
    val regex: String,
    val assignmentId: String,
)

data class GetCodeResponse(
    val id: String?,
    val starterCode: String,
    val solutionCode: String,
    val assignmentId: String,
    val unitTest: String,
    val primary: Boolean,
    val language: Language,
    val languages: List<Language>,
) {
    data class Language(
        val name: String,
        val id: String,
        val url: String,
        val mime: String,
    )
}

data class CreateAssignmentResponse(val id: String)

object SaveAssignmentResponse
object SaveCodeResponse


object SaveFeedbackResponse
object DeleteCodeResponse
object DeleteFeedbackResponse
