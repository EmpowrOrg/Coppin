package org.empowrco.coppin.models.responses

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class EdxBlocksResponse(val blocks: JsonObject)
