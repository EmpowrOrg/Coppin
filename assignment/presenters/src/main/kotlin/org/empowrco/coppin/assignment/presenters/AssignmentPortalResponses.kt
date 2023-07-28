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
    val submissions: List<Submission>,
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

    data class Submission(
        val id: String,
        val success: String,
        val numberOfAttempts: Int,
        val username: String,
        val language: String,
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

data class GetSubmissionResponse(
    val assignment: String,
    val studentId: String,
    val submissions: List<Submission>,
    val submissionsJson: String,
) {
    @Serializable
    data class Language(
        val name: String,
        val url: String,
        val mime: String,
    )

    @Serializable
    data class Submission(
        val code: String,
        val language: Language,
        val attempt: Int,
    )
}

data class CreateAssignmentResponse(val id: String)

data class UpdateCodeResponse(val courseId: String)

@Serializable
data class ArchiveAssignmentResponse(val courseId: String)

@Serializable
data class DeleteCodeResponse(val courseId: String)
