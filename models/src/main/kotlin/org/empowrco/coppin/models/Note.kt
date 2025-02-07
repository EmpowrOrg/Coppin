package org.empowrco.coppin.models

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import org.empowrco.coppin.utils.serialization.UUIDSerializer
import java.util.UUID

@Serializable
data class Note(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val notes: String,
    val brainrotUrl: String,
    val podcastUrl: String,
    val summary: String,
    val createdAt: LocalDateTime,
    val lastModifiedAt: LocalDateTime,
)
