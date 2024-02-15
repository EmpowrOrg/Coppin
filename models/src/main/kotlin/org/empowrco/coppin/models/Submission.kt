package org.empowrco.coppin.models

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import org.empowrco.coppin.utils.serialization.UUIDSerializer
import java.util.UUID

@Serializable
data class Submission(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    @Serializable(with = UUIDSerializer::class)
    val assignmentId: UUID,
    @Serializable(with = UUIDSerializer::class)
    val languageId: UUID,
    val correct: Boolean,
    val code: String,
    val attempt: Int,
    val feedback: String,
    val studentId: String,
    val createdAt: LocalDateTime,
    val lastModifiedAt: LocalDateTime,
)
