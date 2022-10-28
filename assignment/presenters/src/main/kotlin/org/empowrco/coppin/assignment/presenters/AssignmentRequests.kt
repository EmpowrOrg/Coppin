package org.empowrco.coppin.assignment.presenters

import kotlinx.serialization.Serializable

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
    val instructions: String,
    val successMessage: String,
    val failureMessage: String,
    val totalAttempts: Int,
    val title: String,
)


@Serializable
data class UpdateAssignmentRequest(
    val assignmentId: String,
    val expectedOutput: String,
    val instructions: String,
    val successMessage: String,
    val failureMessage: String,
    val totalAttempts: Int,
    val title: String,
)

@Serializable
data class CreateAssignmentFeedbackRequest(
    val assignmentId: String,
    val attempt: Int,
    val feedback: String,
    val regex: String,
)

@Serializable
data class CreateAssignmentCodeRequest(
    val assignmentId: String,
    val starterCode: String,
    val solutionCode: String,
    val languageId: String,
    val primary: Boolean,
)

@Serializable
data class GetAssignmentRequest(
    val referenceId: String,
    val supportedLanguageMimes: List<String>,
)
