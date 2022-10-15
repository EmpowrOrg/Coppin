package org.empowrco.coppin.assignment.presenters

import kotlinx.serialization.Serializable

@Serializable
data class RunRequest(
    val referenceId: String?,
    val language: String,
    val executeSuccess: Boolean,
    val code: String,
    val output: String,
)

@Serializable
data class SubmitRequest(
    val code: String,
    val output: String,
    val executeSuccess: Boolean,
    val referenceId: String,
    val language: String,
    val email: String,
    val attempt: Int,
)

@Serializable
data class CreateAssignmentRequest(
    val referenceId: String,
    val expectedOutput: String,
    val feedback: List<Feedback>,
    val instructions: String,
    val solution: String,
    val successMessage: String,
    val failureMessage: String,
    val totalAttempts: Int,
    val title: String,
    val starterCodes: List<StarterCode>
) {
    @Serializable
    data class Feedback(
        val attempt: Int,
        val feedback: String,
        val regex: String,
    )

    @Serializable
    data class StarterCode(
        val code: String,
        val languageId: String,
        val primary: Boolean,
    )
}

@Serializable
data class GetAssignmentRequest(
    val referenceId: String,
    val supportedLanguageMimes: List<String>,
)
