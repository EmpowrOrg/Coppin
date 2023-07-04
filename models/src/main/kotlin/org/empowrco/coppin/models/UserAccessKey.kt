package org.empowrco.coppin.models

import kotlinx.datetime.LocalDateTime
import java.util.UUID

data class UserAccessKey(
    val userId: UUID,
    val id: UUID,
    val key: String,
    val name: String,
    val createdAt: LocalDateTime,
    val lastModifiedAt: LocalDateTime,
)
