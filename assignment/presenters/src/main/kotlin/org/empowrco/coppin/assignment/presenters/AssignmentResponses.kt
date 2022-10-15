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
data class GetAssignmentResponse(
    val instructions: String,
    val title: String,
    val solution: String?,
    val starterCodes: List<StarterCode>
) {
    @Serializable
    data class StarterCode(
        val displayName: String,
        val mime: String,
        val starterCode: String?,
        val primary: Boolean,
    )
}
