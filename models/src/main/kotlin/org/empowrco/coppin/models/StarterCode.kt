package org.empowrco.coppin.models

import kotlinx.datetime.LocalDateTime
import java.util.UUID

data class StarterCode(
    val id: UUID,
    val code: String,
    val language: Language,
    val assignmentId: UUID,
    val primary: Boolean,
    val createdAt: LocalDateTime,
    val lastModifiedAt: LocalDateTime,
)
