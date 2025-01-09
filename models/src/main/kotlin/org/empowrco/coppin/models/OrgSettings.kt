package org.empowrco.coppin.models

import kotlinx.datetime.LocalDateTime
import java.util.UUID

data class OrgSettings(
    val id: UUID,
    val edxClientId: String,
    val edxClientSecret: String,
    val edxUsername: String,
    val edxApiUrl: String,
    val doctorUrl: String,
    val createdAt: LocalDateTime,
    val lastModifiedAt: LocalDateTime,
)
