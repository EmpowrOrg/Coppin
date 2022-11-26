package org.empowrco.coppin.models

import kotlinx.datetime.LocalDateTime
import java.util.UUID

data class Language(
    val id: UUID,
    val name: String,
    val mime: String,
    val url: String,
    val supportsUnitTests: Boolean,
    val createdAt: LocalDateTime,
    val lastModifiedAt: LocalDateTime,
)
