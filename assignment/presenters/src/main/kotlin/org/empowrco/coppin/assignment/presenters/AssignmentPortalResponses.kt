package org.empowrco.coppin.assignment.presenters

data class GetAssignmentPortalResponse(
    val id: String?,
    val referenceId: String?,
    val instructions: String?,
    val successMessage: String?,
    val failureMessage: String?,
    val attempts: Int?,
    val title: String?,
    val codes: List<Code>,
) {
    data class Code(
        val id: String,
        val assignmentId: String,
        val language: String,
        val primary: String,
        val hasSolution: String,
        val hasStarter: String,
    )
}

data class GetAssignmentsResponse(val assignments: List<AssignmentListItem>) {
    data class AssignmentListItem(val referenceId: String, val id: String, val title: String, val createdAt: String)
}

object UpdateAssignmentResponse


data class GetCodeResponse(
    val id: String?,
    val starterCode: String?,
    val solutionCode: String?,
    val assignmentId: String,
    val unitTest: String?,
    val primary: Boolean,
    val language: Language,
    val languages: List<Language>,
) {
    data class Language(
        val name: String,
        val id: String,
        val url: String,
        val mime: String,
        val selected: Boolean,
    )
}

data class CreateAssignmentResponse(val id: String)

object SaveCodeResponse


object DeleteCodeResponse
