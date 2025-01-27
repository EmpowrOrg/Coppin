package org.empowrco.coppin.models

import kotlinx.datetime.LocalDateTime
import java.util.UUID

data class Application(
    val id: UUID,
    val name: String,
    val accessGroups: List<User.Type>,
    val createdAt: LocalDateTime,
    val lastModifiedAt: LocalDateTime,
)
