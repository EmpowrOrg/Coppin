package org.empowrco.coppin.models.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EdxCourse(
    val name: String,
    val id: String,
    val number: String,
    val org: String,
    val start: String,
    val end: String?,
    @SerialName("short_description")
    val shortDescription: String?,
    val media: Media,
) {
    @Serializable
    data class Media(val image: Image) {
        @Serializable
        data class Image(val raw: String)
    }
}
