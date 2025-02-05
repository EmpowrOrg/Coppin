package org.empowrco.coppin.models

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import org.empowrco.coppin.utils.serialization.UUIDSerializer
import java.util.UUID

@Serializable
data class AssignmentCode(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val starterCode: String,
    val solutionCode: String,
    val solutionVisibility: SolutionVisibility,
    val unitTest: String,
    val language: Language,
    val framework: Language.Framework,
    @Serializable(with = UUIDSerializer::class)
    val assignmentId: UUID,
    val primary: Boolean,
    val injectable: Boolean,
    val createdAt: LocalDateTime,
    val lastModifiedAt: LocalDateTime,
) {
    enum class SolutionVisibility {
        always, onFinish, never
    }
}
