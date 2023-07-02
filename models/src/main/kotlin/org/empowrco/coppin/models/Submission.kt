package org.empowrco.coppin.models

import kotlinx.datetime.LocalDateTime
import java.util.UUID

data class Submission(
    val id: UUID,
    val assignmentId: UUID,
    val languageId: UUID,
    val correct: Boolean,
    val code: String,
    val attempt: Int,
    val studentId: String,
    val createdAt: LocalDateTime,
    val lastModifiedAt: LocalDateTime,
)
