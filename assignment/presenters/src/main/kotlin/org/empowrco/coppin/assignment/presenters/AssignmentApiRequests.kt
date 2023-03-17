package org.empowrco.coppin.assignment.presenters

import kotlinx.serialization.Serializable

object RequestApi {

    @Serializable
    data class RunRequest(
        val code: String,
        val language: String,
        val referenceId: String,
    )

    @Serializable
    data class SubmitRequest(
        val code: String,
        val referenceId: String,
        val language: String,
        val email: String,
        val attempt: Int,
    )

    @Serializable
    data class GetAssignmentRequest(
        val referenceId: String,
        val supportedLanguageMimes: List<String>,
    )

    @Serializable
    data class DeleteAssignmentRequest(
        val id: String,
    )
}
