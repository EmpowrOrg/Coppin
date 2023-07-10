package org.empowrco.coppin.models.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EdxStudent(
    @SerialName("is_active")
    val isActive: Boolean,
    val user: String,
    val created: String,
    val mode: String,
)
