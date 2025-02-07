package org.empowrco.coppin.models

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import org.empowrco.coppin.utils.serialization.UUIDSerializer
import java.util.UUID

@Serializable
data class StudentNote(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val studentId: String,
    @Serializable(with = UUIDSerializer::class)
    val noteId: UUID,
    val comment: String,
    val contextBefore: String,
    val contextAfter: String,
    val highlightedText: String,
    val index: Int,
    val type: Type,
    val contextType: ContextType,
    val createdAt: LocalDateTime,
    val lastModifiedAt: LocalDateTime,
) {
    enum class Type {
        Highlight, Comment, Bookmark
    }
    enum class ContextType {
        Text, Audio, Video
    }
}
