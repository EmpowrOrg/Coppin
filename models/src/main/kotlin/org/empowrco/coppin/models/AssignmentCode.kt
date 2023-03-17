package org.empowrco.coppin.models

import kotlinx.datetime.LocalDateTime
import java.util.UUID

data class AssignmentCode(
    val id: UUID,
    val starterCode: String,
    val solutionCode: String,
    val unitTest: String,
    val language: Language,
    val assignmentId: UUID,
    val primary: Boolean,
    val injectable: Boolean,
    val createdAt: LocalDateTime,
    val lastModifiedAt: LocalDateTime,
)
