package org.empowrco.coppin.models

import kotlinx.datetime.LocalDateTime
import java.util.UUID

data class Course(
    val id: UUID,
    val edxId: String,
    val title: String,
    val number: String,
    val org: String,
    val createdAt: LocalDateTime,
    val lastModifiedAt: LocalDateTime,
)
