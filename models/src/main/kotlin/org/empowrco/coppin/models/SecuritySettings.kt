package org.empowrco.coppin.models

import kotlinx.datetime.LocalDateTime
import java.util.UUID

data class SecuritySettings(
    val id: UUID,
    val oktaEnabled: Boolean,
    val createdAt: LocalDateTime,
    val lastModifiedAt: LocalDateTime,
)
