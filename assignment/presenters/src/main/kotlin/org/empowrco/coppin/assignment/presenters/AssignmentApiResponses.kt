package org.empowrco.coppin.assignment.presenters

import kotlinx.serialization.Serializable

object ResponseApi {
    @Serializable
    data class SubmitResponse(
        val output: String,
        val feedback: String,
        val success: Boolean,
        val finalAttempt: Boolean,
        val diff: String?,
    )

    @Serializable
    data class DeleteAssignmentResponse(val id: String)

    @Serializable
    data class GetAssignmentResponse(
        val instructions: String,
        val title: String,
        val assignmentCodes: List<AssignmentCode>,
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
}


