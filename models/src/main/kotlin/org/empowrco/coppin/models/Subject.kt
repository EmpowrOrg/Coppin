package org.empowrco.coppin.models

import kotlinx.datetime.LocalDateTime
import java.util.UUID

data class Subject(
    val id: UUID,
    val courseId: UUID,
    val name: String,
    val createdAt: LocalDateTime,
    val lastModifiedAt: LocalDateTime,
)
