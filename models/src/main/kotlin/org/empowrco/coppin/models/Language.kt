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
    val testFrameworks: List<TestFramework>,
    val unitTestRegex: String,
    val createdAt: LocalDateTime,
    val lastModifiedAt: LocalDateTime,
) {
    @Serializable
    data class TestFramework(
        @Serializable(with = UUIDSerializer::class)
        val id: UUID,
        val name: String,
    )
}
