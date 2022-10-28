package org.empowrco.coppin.assignment.presenters

import kotlinx.serialization.Serializable

@Serializable
data class SubmitResponse(
    val output: String,
    val expectedOutput: String,
    val feedback: String,
    val success: Boolean,
    val finalAttempt: Boolean,
    val diff: String?,
)

@Serializable
object CreateAssignmentResponse
@Serializable
object UpdateAssignmentResponse

@Serializable
data class GetAssignmentResponse(
    val instructions: String,
    val title: String,
    val assignmentCodes: List<AssignmentCode>
) {
    @Serializable
    data class AssignmentCode(
        val displayName: String,
        val mime: String,
        val starterCode: String?,
        val primary: Boolean,
        val solutionCode: String?,
    )
}

@Serializable
data class CreateAssignmentFeedbackResponse(
    val assignmentId: String,
    val attempt: Int,
    val feedback: String,
    val regex: String,
)

@Serializable
data class CreateAssignmentCodeResponse(
    val assignmentId: String,
    val starterCode: String,
    val solutionCode: String,
    val languageId: String,
    val primary: Boolean,
)
