package org.empowrco.coppin.assignment.presenters

import kotlinx.serialization.Serializable


object RequestApi {

    @Serializable
    data class RunRequest(
        val code: String,
        val language: String,
        val referenceId: String,
        val studentId: String,
        val studentEmails: List<String>?,
    )

    @Serializable
    data class SubmitRequest(
        val code: String,
        val referenceId: String,
        val language: String,
        val studentId: String,
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

    @Serializable
    data class UploadImageRequest(
        val fileName: String,
        val body: ByteArray?,
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as UploadImageRequest

            if (fileName != other.fileName) return false
            if (!body.contentEquals(other.body)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = fileName.hashCode()
            result = 31 * result + body.contentHashCode()
            return result
        }
    }
}
