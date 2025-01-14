package org.empowrco.coppin.models.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EdxBlocksResponse(
    val root: String,
    val blocks: Map<String, Block>,
)

@Serializable
data class Block(
    val id: String,
    val type: String,
    val due: String?,
    @SerialName("display_name")
    val displayName: String?,
)
