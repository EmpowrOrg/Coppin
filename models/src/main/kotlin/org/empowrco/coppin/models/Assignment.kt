package org.empowrco.coppin.models

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import org.empowrco.coppin.utils.serialization.UUIDSerializer
import java.util.UUID

@Serializable
data class Assignment(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val referenceId: String,
    val assignmentCodes: List<AssignmentCode>,
    val instructions: String,
    val successMessage: String,
    val failureMessage: String,
    val createdAt: LocalDateTime,
    val lastModifiedAt: LocalDateTime,
    val totalAttempts: Int,
    val points: Double,
    val title: String,
    val archived: Boolean,
    val blockId: String?,
    @Serializable(with = UUIDSerializer::class)
    val courseId: UUID,
    val subject: Subject,
)
