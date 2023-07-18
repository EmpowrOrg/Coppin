package org.empowrco.coppin.assignment.presenters

import kotlinx.serialization.Serializable

data class GetAssignmentPortalResponse(
    val id: String?,
    val referenceId: String?,
    val courseId: String,
    val instructions: String?,
    val successMessage: String?,
    val failureMessage: String?,
    val attempts: Int?,
    val title: String?,
    val codes: List<Code>,
    val subjects: List<Subject>,
    val subjectId: String?,
) {
    data class Code(
        val id: String,
        val assignmentId: String,
        val language: String,
        val primary: String,
        val hasSolution: String,
        val hasStarter: String,
        val injectable: String,
    )

    data class Subject(
        val id: String,
        val name: String,
    )
}

data class GetAssignmentsResponse(val assignments: List<AssignmentListItem>) {
    data class AssignmentListItem(val referenceId: String, val id: String, val title: String, val createdAt: String)
}

data class UpdateAssignmentResponse(val courseId: String)


data class GetCodeResponse(
    val id: String?,
    val starterCode: String?,
    val solutionCode: String?,
    val assignmentId: String,
    val courseId: String,
    val unitTest: String?,
    val primary: Boolean,
    val injectable: Boolean,
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

data class UpdateCodeResponse(val courseId: String)

@Serializable
data class ArchiveAssignmentResponse(val courseId: String)

@Serializable
data class DeleteCodeResponse(val courseId: String)
