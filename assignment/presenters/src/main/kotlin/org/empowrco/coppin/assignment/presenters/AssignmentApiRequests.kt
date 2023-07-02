package org.empowrco.coppin.assignment.presenters

import kotlinx.serialization.Serializable

object RequestApi {

    @Serializable
    data class RunRequest(
        val code: String,
        val language: String,
        val referenceId: String,
        val studentId: String,
    )

    @Serializable
    data class SubmitRequest(
        val code: String,
        val referenceId: String,
        val language: String,
        val attempt: Int,
        val studentId: String,
    )

    @Serializable
    data class GetAssignmentRequest(
        val referenceId: String,
        val studentId: String,
    )

    @Serializable
    data class DeleteAssignmentRequest(
        val id: String,
    )
}
