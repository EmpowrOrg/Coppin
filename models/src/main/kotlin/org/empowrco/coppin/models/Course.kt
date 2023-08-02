package org.empowrco.coppin.models

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import org.empowrco.coppin.utils.serialization.UUIDSerializer
import java.util.UUID

@Serializable
data class Course(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val edxId: String,
    val title: String,
    val number: String,
    val org: String,
    val createdAt: LocalDateTime,
    val lastModifiedAt: LocalDateTime,
)
