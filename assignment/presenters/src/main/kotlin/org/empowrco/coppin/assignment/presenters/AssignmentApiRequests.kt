package org.empowrco.coppin.assignment.presenters

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject


object RequestApi {

    @Serializable
    data class RunRequest(
        val code: String,
        val language: String,
        val referenceId: String,
        val studentId: String,
        val studentExtras: JsonObject?,
        val studentEmails: List<String>?,
    )

    @Serializable
    data class SubmitRequest(
        val code: String,
        val referenceId: String,
        val language: String,
        val studentId: String,
        val studentExtras: JsonObject?,
        val studentEmails: List<String>?,
    )

    @Serializable
    data class GetAssignmentRequest(
        val referenceId: String,
        val studentId: String,
        val blockId: String,
    )

    @Serializable
    data class DeleteAssignmentRequest(
        val id: String,
    )
}
