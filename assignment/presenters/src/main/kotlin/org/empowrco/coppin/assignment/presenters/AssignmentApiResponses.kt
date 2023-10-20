package org.empowrco.coppin.assignment.presenters

import kotlinx.serialization.Serializable

object ResponseApi {
    @Serializable
    data class SubmitResponse(
        val output: String,
        val success: Boolean,
        val finalAttempt: Boolean,
        val solutionCode: String?,
        val gradePoints: Double,
        val attemptsRemaining: Int,
    )

    @Serializable
    data class RunResponse(
        val output: String,
        val success: Boolean = true,
    )

    @Serializable
    data class DeleteAssignmentResponse(val courseId: String)

    @Serializable
    data class GetAssignmentResponse(
        val instructions: String,
        val title: String,
        val assignmentCodes: List<AssignmentCode>,
        val points: Double,
        val attemptsRemaining: Int,
    ) {
        @Serializable
        data class AssignmentCode(
            val displayName: String,
            val mime: String,
            val starterCode: String?,
            val primary: Boolean,
            val solutionCode: String?,
            val url: String,
            val userCode: String?,
        )
    }
}


