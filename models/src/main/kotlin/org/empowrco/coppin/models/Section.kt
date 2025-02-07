package org.empowrco.coppin.models

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import org.empowrco.coppin.utils.serialization.UUIDSerializer
import java.util.*

@Serializable
data class Section(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    @Serializable(with = UUIDSerializer::class)
    val subjectId: UUID,
    val name: String,
    val order: Int,
    val createdAt: LocalDateTime,
    val lastModifiedAt: LocalDateTime,
)
