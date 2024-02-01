package org.empowrco.coppin.models

import kotlinx.datetime.LocalDateTime
import java.util.UUID

data class AiSettings(
    val id: UUID,
    val model: String,
    val orgKey: String,
    val prePrompt: String,
    val key: String,
    val createdAt: LocalDateTime,
    val lastModifiedAt: LocalDateTime,
)
