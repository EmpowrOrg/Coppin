package org.empowrco.coppin.models

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import org.empowrco.coppin.utils.serialization.UUIDSerializer
import java.util.UUID

@Serializable
data class Language(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val name: String,
    val mime: String,
    val url: String,
    val versions: List<String>,
    val frameworks: List<Framework>,
    val unitTestRegex: String,
    val createdAt: LocalDateTime,
    val lastModifiedAt: LocalDateTime,
) {
    @Serializable
    data class Framework(
        @Serializable(with = UUIDSerializer::class)
        val id: UUID,
        val name: String,
        val version: String,
        @Serializable(with = UUIDSerializer::class)
        val languageId: UUID,
        val commands: List<Command>,
        val createdAt: LocalDateTime,
        val lastModifiedAt: LocalDateTime,
    ) {
        @Serializable
        data class Command(
            @Serializable(with = UUIDSerializer::class)
            val id: UUID,
            val command: String,
            val order: Int,
            val createdAt: LocalDateTime,
            val lastModifiedAt: LocalDateTime,
        )
    }
}
