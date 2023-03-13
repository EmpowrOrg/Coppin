package org.empowrco.coppin.assignment.presenters

import kotlinx.serialization.Serializable

@Serializable
data class CreateAssignmentPortalRequest(
    val referenceId: String,
    val instructions: String,
    val successMessage: String,
    val failureMessage: String,
    val totalAttempts: Int,
    val title: String,
    val gradingType: String,
)

data class GetAssignmentRequest(
    val id: String?,
)

data class GetCodeRequest(
    val id: String?,
    val assignmentId: String,
)

@Serializable
data class UpdateAssignmentPortalRequest(
    val id: String?,
    val instructions: String,
    val successMessage: String,
    val failureMessage: String,
    val totalAttempts: Int,
    val title: String,
    val gradingType: String,
)

@Serializable
data class UpdateCodePortalRequest(
    val id: String?,
    val languageMime: String,
    val starterCode: String?,
    val solutionCode: String?,
    val primary: String,
    val assignmentId: String,
    val unitTest: String?,
)

data class DeleteCodeRequest(val id: String)
